package com.aicrm.core.auth.application;

import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.UserAccount;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.dto.LoginRequest;
import com.aicrm.core.auth.dto.LoginResponse;
import com.aicrm.core.auth.dto.MeResponse;
import com.aicrm.core.auth.dto.AgentSignUpRequest;
import com.aicrm.core.auth.dto.SignUpRequest;
import com.aicrm.core.auth.dto.SignUpResponse;
import com.aicrm.core.auth.dto.UpdateMeRequest;
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
            AgentAttendanceService agentAttendanceService) {
        this.userAccountRepository = userAccountRepository;
        this.agentAccountRepository = agentAccountRepository;
        this.customerRepository = customerRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.currentUserProvider = currentUserProvider;
        this.agentAttendanceService = agentAttendanceService;
    }

    @Transactional
    public LoginResponse login(LoginRequest request) {
        UserAccount account = userAccountRepository.findByEmail(request.email().trim().toLowerCase())
                .orElseThrow(() -> new BusinessException(ErrorCode.AUTH_FAILED));

        if (!matchesPassword(request.password(), account.getPasswordHash())) {
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
                throw new BusinessException(ErrorCode.ACCOUNT_UNAVAILABLE);
            }
        }

        UserRole role = account.getRole() == null ? UserRole.CUSTOMER : account.getRole();
        AuthenticatedUser user = new AuthenticatedUser(
                account.getId(),
                account.getEmail(),
                account.getName(),
                role);
        String token = jwtTokenProvider.generateToken(user);
        if (role == UserRole.AGENT) {
            agentAttendanceService.markAgentLogin(user.userId());
        }
        return new LoginResponse(token, "Bearer", user.userId(), user.email(), role);
    }

    private boolean matchesPassword(String rawPassword, String passwordHash) {
        if (passwordHash == null || passwordHash.isBlank()) {
            return false;
        }
        try {
            return passwordEncoder.matches(rawPassword, passwordHash);
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    @Transactional
    public SignUpResponse signupCustomer(SignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        ensureEmailAvailable(email);

        UserAccount account = userAccountRepository.save(UserAccount.create(
                email,
                passwordEncoder.encode(request.password()),
                request.name().trim(),
                UserRole.CUSTOMER));
        customerRepository.save(Customer.create(request.name().trim(), "", email, account.getId()));

        return new SignUpResponse(account.getId(), account.getEmail(), account.getRole(), "ACTIVE");
    }

    @Transactional
    public SignUpResponse signupAgent(AgentSignUpRequest request) {
        String email = request.email().trim().toLowerCase();
        String employeeNo = AgentEmployeeNoValidator.normalize(request.employeeNo());
        AgentEmployeeNoValidator.validate(employeeNo);
        ensureEmailAvailable(email);
        ensureEmployeeNoAvailable(employeeNo);

        UserAccount account = userAccountRepository.save(UserAccount.create(
                email,
                passwordEncoder.encode(request.password()),
                request.name().trim(),
                UserRole.AGENT));
        agentAccountRepository.save(AgentAccount.createPending(account.getId(), account.getName(), employeeNo));

        return new SignUpResponse(account.getId(), account.getEmail(), account.getRole(), "PENDING");
    }

    @Transactional(readOnly = true)
    public MeResponse me() {
        AuthenticatedUser current = currentUserProvider.require();
        UserAccount account = userAccountRepository.findById(current.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "USER_NOT_FOUND", current.userId()));
        return toMeResponse(account);
    }

    @Transactional
    public MeResponse updateCurrentUser(UpdateMeRequest request) {
        AuthenticatedUser current = currentUserProvider.require();
        UserAccount account = userAccountRepository.findById(current.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "USER_NOT_FOUND", current.userId()));

        if (request.password() != null && !request.password().isBlank()) {
            account.updatePasswordHash(passwordEncoder.encode(request.password()));
            userAccountRepository.save(account);
        }

        if (account.getRole() == UserRole.ADMIN && request.name() != null && !request.name().isBlank()) {
            account.updateName(request.name().trim());
            userAccountRepository.save(account);
        } else if (account.getRole() == UserRole.CUSTOMER) {
            Customer customer = customerRepository.findByUserId(account.getId())
                    .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "CUSTOMER_NOT_FOUND", account.getId()));
            String phone = request.phone() == null ? "" : request.phone().trim();
            customer.updatePhone(phone);
            customerRepository.save(customer);
        }

        return toMeResponse(account);
    }

    private MeResponse toMeResponse(UserAccount account) {
        String phone = "";
        String employeeNo = "";
        if (account.getRole() == UserRole.CUSTOMER) {
            phone = customerRepository.findByUserId(account.getId())
                    .map(customer -> customer.getPhone() == null ? "" : customer.getPhone())
                    .orElse("");
        } else if (account.getRole() == UserRole.AGENT) {
            employeeNo = agentAccountRepository.findByUserId(account.getId())
                    .map(AgentAccount::getEmployeeNo)
                    .orElse("");
        }
        return new MeResponse(
                account.getId(),
                account.getEmail(),
                account.getName(),
                account.getRole(),
                phone,
                employeeNo);
    }

    @Transactional
    public void approveAgent(Long agentId) {
        AgentAccount agentAccount = agentAccountRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "AGENT_NOT_FOUND", agentId));
        agentAccount.approve();
        agentAccountRepository.save(agentAccount);
    }

    @Transactional
    public void updateAgentGrade(Long agentId, AgentGrade grade) {
        AgentAccount agentAccount = agentAccountRepository.findById(agentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "AGENT_NOT_FOUND", agentId));
        agentAccount.changeGrade(grade);
        agentAccountRepository.save(agentAccount);
    }

    @Transactional
    public void withdrawCurrentUser() {
        AuthenticatedUser current = currentUserProvider.require();
        UserAccount account = userAccountRepository.findById(current.userId())
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "USER_NOT_FOUND", current.userId()));
        account.setWithdrawnYn("Y");
        userAccountRepository.save(account);
    }

    private void ensureEmailAvailable(String email) {
        if (userAccountRepository.existsByEmail(email)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    private void ensureEmployeeNoAvailable(String employeeNo) {
        if (agentAccountRepository.existsByEmployeeNo(employeeNo)) {
            throw new BusinessException(ErrorCode.DUPLICATE_EMPLOYEE_NO);
        }
    }
}
