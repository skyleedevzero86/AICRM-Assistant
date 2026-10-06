package com.aicrm.core.auth.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.aicrm.core.attendance.application.AgentAttendanceService;
import com.aicrm.core.auth.domain.AgentAccount;
import com.aicrm.core.auth.domain.AgentAccountStatus;
import com.aicrm.core.auth.domain.UserAccount;
import com.aicrm.core.auth.domain.UserRole;
import com.aicrm.core.auth.dto.LoginRequest;
import com.aicrm.core.auth.infrastructure.AgentAccountRepository;
import com.aicrm.core.auth.infrastructure.UserAccountRepository;
import com.aicrm.core.auth.security.JwtTokenProvider;
import com.aicrm.core.customer.domain.CustomerRepository;
import com.aicrm.core.global.exception.BusinessException;
import com.aicrm.core.global.exception.ErrorCode;
import com.aicrm.core.support.MessagesInitializer;
import java.util.Optional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceLoginTest {

    @Mock
    private UserAccountRepository userAccountRepository;
    @Mock
    private AgentAccountRepository agentAccountRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @Mock
    private com.aicrm.core.auth.security.CurrentUserProvider currentUserProvider;
    @Mock
    private AgentAttendanceService agentAttendanceService;

    @InjectMocks
    private AuthService authService;

    @BeforeAll
    static void setUpMessages() {
        MessagesInitializer.init();
    }

    @Test
    void loginRecordsAgentAttendanceInWritableTransaction() {
        UserAccount account = UserAccount.create("agent1@aicrm.local", "hash", "Agent", UserRole.AGENT);
        AgentAccount agent = AgentAccount.createPending(1L, "Agent", "2026060207120101");
        agent.approve();

        when(userAccountRepository.findByEmail("agent1@aicrm.local")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);
        when(agentAccountRepository.findByUserId(any())).thenReturn(Optional.of(agent));
        when(jwtTokenProvider.generateToken(any())).thenReturn("token");

        authService.login(new LoginRequest("agent1@aicrm.local", "password"));

        verify(agentAttendanceService).markAgentLogin(any());
    }

    @Test
    void loginRejectsSuspendedAccount() {
        UserAccount account = UserAccount.create("user@test.com", "hash", "User", UserRole.CUSTOMER);
        account.setSuspendedYn("Y");

        when(userAccountRepository.findByEmail("user@test.com")).thenReturn(Optional.of(account));
        when(passwordEncoder.matches("password", "hash")).thenReturn(true);

        BusinessException exception = assertThrows(
                BusinessException.class,
                () -> authService.login(new LoginRequest("user@test.com", "password")));

        assertEquals(ErrorCode.ACCOUNT_SUSPENDED, exception.getErrorCode());
    }
}
