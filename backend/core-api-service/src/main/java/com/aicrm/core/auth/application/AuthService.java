package com.aicrm.core.auth.application;

import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.UserAccount;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.dto.LoginRequest;
import com.aicrm.core.auth.dto.LoginResponse;
import com.aicrm.core.auth.dto.MeResponse;
import com.aicrm.core.auth.dto.SignUpRequest;
import com.aicrm.core.auth.dto.SignUpResponse;
import com.aicrm.core.auth.domain.AgentGrade;
import com.aicrm.core.attendance.application.AgentAttendanceService;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
import com.aicrm.core.auth.infrastructure.UserAccountRepository;
import com.aicrm.core.auth.security.AuthenticatedUser;
import com.aicrm.core.auth.security.CurrentUserProvider;
import com.aicrm.core.auth.security.JwtTokenProvider;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserAccountRepository userAccountRepository;
    private final AgentAccountRepository agentAccountRepository;
    private final CustomerRepository customerRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final CurrentUserProvider currentUserProvider;
    private final AgentAttendanceService agentAttendanceService;

    public AuthService(
            UserAccountRepository userAccountRepository,
            AgentAccountRepository agentAccountRepository,
            CustomerRepository customerRepository,
            PasswordEncoder passwordEncoder,
            JwtTokenProvider jwtTokenProvider,
            CurrentUserProvider currentUserProvider,
            AgentAttendanceService agentAttendanceService
    ) {
        this.userAccountRepository = userAccountRepository;
        this.agentAccountRepository = agentAccountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.currentUserProvider = currentUserProvider;
        this.agentAttendanceService = agentAttendanceService;
    }

    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UserAccount account = userAccountRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_FAILED));

        if (!passwordEncoder.matches(request.password(), account.getPasswordHash())) {
            throw new BusinessException(ErrorCode.AUTH_FAILED);
        }
        if (account.isWithdrawn()) {
            throw new BusinessException(ErrorCode.ACCOUNT_WITHDRAWN);
        }
        if (account.isSuspended()) {
            throw new BusinessException(ErrorCode.ACCOUNT_SUSPENDED);
        }

        if (account.getRole() == UserRole.AGENT) {
            AgentAccount agent = agentAccountRepository.findByUserId(account.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_FAILED));
            if (agent.getStatus() != AgentAccountStatus.ACTIVE) {
                throw new BusinessException(ErrorCode.AGENT_APPROVAL_REQUIRED);
            }
        }

        AuthenticatedUser user = new AuthenticatedUser(
                account.getId(),
                account.getEmail(),
                account.getName(),
                account.getRole()
        );
        String token = jwtTokenProvider.generateToken(user);
        if (user.role() == UserRole.AGENT) {
            agentAttendanceService.markAgentLogin(user.userId());
        }
        return new LoginResponse(token, "Bearer", user.userId(), user.email(), user.role());
    }

    @Transactional
    public SignUpResponse signupCustomer(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        ensureEmailAvailable(email);

        UserAccount account = userAccountRepository.save(UserAccount.create(
                email,
                passwordEncoder.encode(request.password()),
                request.name().trim(),
                UserRole.CUSTOMER
        ));
        customerRepository.save(Customer.create(request.name().trim(), "", email, account.getId()));

        return new SignUpResponse(account.getId(), account.getEmail(), account.getRole(), "ACTIVE");
    }

    @Transactional
    public SignUpResponse signupAgent(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        ensureEmailAvailable(email);

        UserAccount account = userAccountRepository.save(UserAccount.create(
                email,
                passwordEncoder.encode(request.password()),
                request.name().trim(),
                UserRole.AGENT
        ));
        agentAccountRepository.save(AgentAccount.createPending(account.getId(), account.getName()));

        return new SignUpResponse(account.getId(), account.getEmail(), account.getRole(), "PENDING");
    }

    @Transactional(readOnly = true)
    public MeResponse me() {
        AuthenticatedUser user = currentUserProvider.require();
        return new MeResponse(user.userId(), user.email(), user.name(), user.role());
    }

    @Transactional
    public void approveAgent(Long agentId) {
        AgentAccount agentAccount = agentAccountRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "상담원 계정을 찾을 수 없습니다: " + agentId));
        agentAccount.approve();
        agentAccountRepository.save(agentAccount);
    }

    @Transactional
    public void updateAgentGrade(Long agentId, AgentGrade grade) {
        AgentAccount agentAccount = agentAccountRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "상담원 계정을 찾을 수 없습니다: " + agentId));
        agentAccount.changeGrade(grade);
        agentAccountRepository.save(agentAccount);
    }

    @Transactional
    public void withdrawCurrentUser() {
        AuthenticatedUser current = currentUserProvider.require();
        UserAccount account = userAccountRepository.findById(current.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다: " + current.userId()));
        account.setWithdrawnYn("Y");
        userAccountRepository.save(account);
    }

    private void ensureEmailAvailable(String email) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
