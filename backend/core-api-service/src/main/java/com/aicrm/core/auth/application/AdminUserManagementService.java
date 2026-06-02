package com.aicrm.core.auth.application;

import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.AgentGrade;
import com.aicrm.core.auth.domain.UserAccount;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.dto.AdminAgentUserResponse;
import com.aicrm.core.auth.dto.AdminCustomerUserResponse;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
import com.aicrm.core.auth.infrastructure.UserAccountRepository;
import com.aicrm.core.customer.domain.Customer;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AdminUserManagementService {

    private final UserAccountRepository userAccountRepository;
    private final AgentAccountRepository agentAccountRepository;
    private final CustomerRepository customerRepository;

    public AdminUserManagementService(
            UserAccountRepository userAccountRepository,
            AgentAccountRepository agentAccountRepository,
            CustomerRepository customerRepository
    ) {
        this.userAccountRepository = userAccountRepository;
        this.agentAccountRepository = agentAccountRepository;
        this.customerRepository = customerRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminCustomerUserResponse> getCustomerUsers(String keyword) {
        String normalized = normalize(keyword);
        Map<Long, Customer> customerByUserId = customerRepository.findAllRegisteredUsers().stream()
                .filter(customer -> customer.getUserId() != null)
                .collect(Collectors.toMap(Customer::getUserId, customer -> customer));

        return userAccountRepository.findAllByRole(UserRole.CUSTOMER).stream()
                .filter(byKeyword(normalized))
                .map(user -> {
                    Customer customer = customerByUserId.get(user.getId());
                    return new AdminCustomerUserResponse(
                            user.getId(),
                            user.getName(),
                            user.getEmail(),
                            customer != null ? customer.getPhone() : "",
                            user.getWithdrawnYn(),
                            user.getSuspendedYn()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminAgentUserResponse> getAgentUsers(
            String keyword,
            AgentAccountStatus approvalStatus,
            AgentGrade grade
    ) {
        String normalized = normalize(keyword);
        Map<Long, AgentAccount> agentByUserId = agentAccountRepository.findAll().stream()
                .collect(Collectors.toMap(AgentAccount::getUserId, agent -> agent));

        return userAccountRepository.findAllByRole(UserRole.AGENT).stream()
                .filter(byKeyword(normalized))
                .map(user -> {
                    AgentAccount agent = agentByUserId.get(user.getId());
                    if (agent == null) {
                        return null;
                    }
                    return new AdminAgentUserResponse(
                            user.getId(),
                            agent.getId(),
                            user.getName(),
                            user.getEmail(),
                            agent.getStatus(),
                            agent.getGrade(),
                            user.getWithdrawnYn(),
                            user.getSuspendedYn()
                    );
                })
                .filter(response -> response != null)
                .filter(response -> approvalStatus == null || response.approvalStatus() == approvalStatus)
                .filter(response -> grade == null || response.grade() == grade)
                .toList();
    }

    @Transactional
    public void updateSuspendedYn(Long userId, String value) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));
        user.setSuspendedYn(value);
        userAccountRepository.save(user);
    }

    @Transactional
    public void updateWithdrawnYn(Long userId, String value) {
        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND, "사용자를 찾을 수 없습니다: " + userId));
        user.setWithdrawnYn(value);
        userAccountRepository.save(user);
    }

    private Predicate<UserAccount> byKeyword(String keyword) {
        if (keyword.isBlank()) {
            return user -> true;
        }
        return user -> user.getName().toLowerCase(Locale.ROOT).contains(keyword)
                || user.getEmail().toLowerCase(Locale.ROOT).contains(keyword);
    }

    private String normalize(String keyword) {
        return keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
    }
}
