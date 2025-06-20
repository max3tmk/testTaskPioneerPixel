package com.max.pioneer_pixel.scheduler;

import com.max.pioneer_pixel.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AccountBalanceScheduler {

    private final AccountService accountService;

    @Scheduled(fixedRate = 30000) // каждые 30 секунд
    public void increaseBalance() {
        log.info("Increasing balances with cap 207%");
        accountService.increaseBalanceWithCap();
    }
}
