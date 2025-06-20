package com.max.pioneer_pixel.dao;

import com.max.pioneer_pixel.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountDao extends JpaRepository<Account, Long> {
    Optional<Account> findByUserId(Long userId);
}
