package com.max.pioneer_pixel.dao;

import com.max.pioneer_pixel.model.EmailData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmailDataDao extends JpaRepository<EmailData, Long> {
    Optional<EmailData> findByEmail(String email);

    List<EmailData> findByUserId(Long userId);

    Optional<EmailData> findByUserIdAndEmail(Long userId, String email);

    Optional<EmailData> findFirstByUserId(Long userId);
}
