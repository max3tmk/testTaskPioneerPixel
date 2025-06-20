package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.dao.AccountDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.Account;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.security.AuthUtils;
import com.max.pioneer_pixel.service.impl.AccountServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

public class AccountServiceImplTest {

    private AccountDao accountDao;
    private UserDao userDao;
    private AccountServiceImpl accountService;

    private static final Long SENDER_ID = 1L;
    private static final Long RECEIVER_ID = 2L;
    private static final BigDecimal AMOUNT = new BigDecimal("100.00");

    private User senderUser;
    private User receiverUser;
    private Account senderAccount;
    private Account receiverAccount;

    @BeforeEach
    void setUp() {
        accountDao = mock(AccountDao.class);
        userDao = mock(UserDao.class);
        accountService = new AccountServiceImpl(accountDao, userDao);

        senderUser = User.builder().id(SENDER_ID).build();
        receiverUser = User.builder().id(RECEIVER_ID).build();

        senderAccount = new Account();
        senderAccount.setId(1L);
        senderAccount.setUser(senderUser);
        senderAccount.setBalance(new BigDecimal("500.00"));
        senderAccount.setInitialBalance(new BigDecimal("500.00"));

        receiverAccount = new Account();
        receiverAccount.setId(2L);
        receiverAccount.setUser(receiverUser);
        receiverAccount.setBalance(new BigDecimal("200.00"));
        receiverAccount.setInitialBalance(new BigDecimal("200.00"));
    }

    @Test
    void transfer_shouldTransferMoneyBetweenAccounts() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("sender");

            when(userDao.findByName("sender")).thenReturn(Optional.of(senderUser));
            when(accountDao.findByUserId(SENDER_ID)).thenReturn(Optional.of(senderAccount));
            when(accountDao.findByUserId(RECEIVER_ID)).thenReturn(Optional.of(receiverAccount));

            accountService.transfer(SENDER_ID, RECEIVER_ID, AMOUNT);

            // Проверяем изменение баланса
            assertEquals(new BigDecimal("400.00"), senderAccount.getBalance());
            assertEquals(new BigDecimal("300.00"), receiverAccount.getBalance());

            // Проверяем вызовы save
            verify(accountDao).save(senderAccount);
            verify(accountDao).save(receiverAccount);
        }
    }

    @Test
    void transfer_shouldThrowExceptionIfSenderNotFound() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("unknown");

            when(userDao.findByName("unknown")).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.transfer(SENDER_ID, RECEIVER_ID, AMOUNT));
            assertEquals("Current user not found", ex.getMessage());
        }
    }

    @Test
    void transfer_shouldThrowExceptionIfReceiverNotFound() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("sender");

            when(userDao.findByName("sender")).thenReturn(Optional.of(senderUser));
            when(accountDao.findByUserId(SENDER_ID)).thenReturn(Optional.of(senderAccount));
            when(accountDao.findByUserId(RECEIVER_ID)).thenReturn(Optional.empty());

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.transfer(SENDER_ID, RECEIVER_ID, AMOUNT));
            assertEquals("Receiver account not found", ex.getMessage());
        }
    }

    @Test
    void transfer_shouldThrowExceptionIfInsufficientFunds() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("sender");

            when(userDao.findByName("sender")).thenReturn(Optional.of(senderUser));
            when(accountDao.findByUserId(SENDER_ID)).thenReturn(Optional.of(senderAccount));
            when(accountDao.findByUserId(RECEIVER_ID)).thenReturn(Optional.of(receiverAccount));

            BigDecimal bigAmount = new BigDecimal("1000.00"); // Больше, чем баланс

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.transfer(SENDER_ID, RECEIVER_ID, bigAmount));
            assertEquals("Insufficient funds.", ex.getMessage());
        }
    }

    @Test
    void transfer_shouldThrowSecurityExceptionIfTransferFromNotCurrentUser() {
        try (MockedStatic<AuthUtils> authUtilsMock = Mockito.mockStatic(AuthUtils.class)) {
            authUtilsMock.when(AuthUtils::getCurrentUsername).thenReturn("senderUser");

            User currentUser = User.builder().id(999L).name("senderUser").build(); // текущий пользователь с id=999
            User fromUser = User.builder().id(SENDER_ID).name("senderUser").build(); // fromUser с другим id
            User toUser = User.builder().id(RECEIVER_ID).name("receiverUser").build();

            when(userDao.findByName("senderUser")).thenReturn(Optional.of(currentUser));
            when(userDao.findById(SENDER_ID)).thenReturn(Optional.of(fromUser));
            when(userDao.findById(RECEIVER_ID)).thenReturn(Optional.of(toUser));

            Account fromAccount = Account.builder()
                    .id(1L)
                    .user(fromUser)
                    .balance(new BigDecimal("500.00"))
                    .initialBalance(new BigDecimal("500.00"))
                    .build();

            Account toAccount = Account.builder()
                    .id(2L)
                    .user(toUser)
                    .balance(new BigDecimal("200.00"))
                    .initialBalance(new BigDecimal("200.00"))
                    .build();

            when(accountDao.findByUserId(SENDER_ID)).thenReturn(Optional.of(fromAccount));
            when(accountDao.findByUserId(RECEIVER_ID)).thenReturn(Optional.of(toAccount));

            SecurityException exception = assertThrows(SecurityException.class, () ->
                    accountService.transfer(SENDER_ID, RECEIVER_ID, AMOUNT)
            );

            assertEquals("You can only transfer from your own account.", exception.getMessage());

            verify(accountDao, never()).save(any());
        }
    }

    @Test
    void transfer_shouldThrowExceptionIfTransferToSelf() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("sender");

            when(userDao.findByName("sender")).thenReturn(Optional.of(senderUser));

            IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                    () -> accountService.transfer(SENDER_ID, SENDER_ID, new BigDecimal("100.00")));
            assertEquals("Cannot transfer to the same user.", ex.getMessage());
        }
    }

    @Test
    void transfer_shouldThrowExceptionIfAmountIsNonPositive() {
        try (MockedStatic<AuthUtils> mockedAuthUtils = mockStatic(AuthUtils.class)) {
            mockedAuthUtils.when(AuthUtils::getCurrentUsername).thenReturn("sender");

            when(userDao.findByName("sender")).thenReturn(Optional.of(senderUser));

            BigDecimal[] invalidAmounts = {BigDecimal.ZERO, new BigDecimal("-100.00")};

            for (BigDecimal amount : invalidAmounts) {
                IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                        () -> accountService.transfer(SENDER_ID, RECEIVER_ID, amount));
                assertEquals("Transfer amount must be positive.", ex.getMessage());
            }
        }
    }
}
