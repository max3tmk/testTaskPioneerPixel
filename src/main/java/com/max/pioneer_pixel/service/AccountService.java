package com.max.pioneer_pixel.service;

import com.max.pioneer_pixel.model.User;

import java.math.BigDecimal;

public interface AccountService {

    void createInitialAccount(User user, BigDecimal initialBalance);

    void updateBalance(Long userId, java.math.BigDecimal newBalance);

    void transfer(Long fromUserId, Long toUserId, java.math.BigDecimal amount);

    /**
     * Увеличивает баланс каждого аккаунта на 10% раз в 30 секунд,
     * при этом баланс не должен превышать 207% от начального депозита.
     */
    void increaseBalanceWithCap();
}
