package com.max.pioneer_pixel.controller;

import com.max.pioneer_pixel.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/API/transfer")
@RequiredArgsConstructor
public class TransferController {

    private static final Logger log = LoggerFactory.getLogger(TransferController.class);
    private final AccountService accountService;

    @PostMapping
    public ResponseEntity<String> transfer(@RequestParam Long fromUserId,
                                           @RequestParam Long toUserId,
                                           @RequestParam BigDecimal amount) {
        LocalDateTime now = LocalDateTime.now();

        log.info("[{}] Operation: transfer | Params: fromUserId={}, toUserId={}, amount={}", now, fromUserId, toUserId, amount);

        try {
            accountService.transfer(fromUserId, toUserId, amount);
            log.info("[{}] Result: transfer successful", now);
            return ResponseEntity.ok("Transfer successful");
        } catch (SecurityException se) {
            log.warn("[{}] Error: SECURITY VIOLATION — {}", now, se.getMessage());
            return ResponseEntity.status(403).body(se.getMessage());
        } catch (IllegalArgumentException e) {
            log.warn("[{}] Error: INVALID INPUT — {}", now, e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception ex) {
            log.error("[{}] Error: UNEXPECTED ERROR — {}", now, ex.getMessage(), ex);
            return ResponseEntity.internalServerError().body("Unexpected error: " + ex.getMessage());
        }
    }
}
