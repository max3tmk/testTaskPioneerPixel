package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.dao.AccountDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.Account;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.security.AuthUtils;
import com.max.pioneer_pixel.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountDao accountDao;
    private final UserDao userDao;

    @Override
    @Transactional
    public void createInitialAccount(User user, BigDecimal initialBalance) {
        Account account = new Account();
        account.setUser(user);
        account.setBalance(initialBalance);
        account.setInitialBalance(initialBalance);
        accountDao.save(account);
    }

    @Override
    @Transactional
    public void updateBalance(Long userId, BigDecimal newBalance) {
        Account account = accountDao.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Account not found for userId: " + userId));
        account.setBalance(newBalance);
        accountDao.save(account);
    }

    @Override
    @Transactional
    public void transfer(Long fromUserId, Long toUserId, BigDecimal amount) {
        if (fromUserId.equals(toUserId)) {
            throw new IllegalArgumentException("Cannot transfer to the same user.");
        }

        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Transfer amount must be positive.");
        }

        // Получаем текущего авторизованного пользователя
        String currentUsername = AuthUtils.getCurrentUsername();
        User currentUser = userDao.findByName(currentUsername)
                .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

        if (!currentUser.getId().equals(fromUserId)) {
            throw new SecurityException("You can only transfer from your own account.");
        }

        Account fromAccount = accountDao.findByUserId(fromUserId)
                .orElseThrow(() -> new IllegalArgumentException("Sender account not found"));

        Account toAccount = accountDao.findByUserId(toUserId)
                .orElseThrow(() -> new IllegalArgumentException("Receiver account not found"));

        if (fromAccount.getBalance().compareTo(amount) < 0) {
            throw new IllegalArgumentException("Insufficient funds.");
        }

        fromAccount.setBalance(fromAccount.getBalance().subtract(amount));
        toAccount.setBalance(toAccount.getBalance().add(amount));

        // Сохраняем изменения
        accountDao.save(fromAccount);
        accountDao.save(toAccount);
    }

    @Override
    @Transactional
    public void increaseBalanceWithCap() {
        List<Account> accounts = accountDao.findAll();

        for (Account account : accounts) {
            BigDecimal currentBalance = account.getBalance();
            BigDecimal initialBalance = account.getInitialBalance();

            BigDecimal maxBalance = initialBalance.multiply(BigDecimal.valueOf(2.07));
            if (currentBalance.compareTo(maxBalance) >= 0) {
                continue; // уже достигли лимита
            }

            BigDecimal increasedBalance = currentBalance.multiply(BigDecimal.valueOf(1.10)); // +10%

            if (increasedBalance.compareTo(maxBalance) > 0) {
                increasedBalance = maxBalance;
            }

            account.setBalance(increasedBalance);
            accountDao.save(account);
        }
    }
}
