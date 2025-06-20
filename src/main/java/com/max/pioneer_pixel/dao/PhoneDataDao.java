package com.max.pioneer_pixel.dao;

import com.max.pioneer_pixel.model.PhoneData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhoneDataDao extends JpaRepository<PhoneData, Long> {
    Optional<PhoneData> findByPhone(String phone);

    List<PhoneData> findByUserId(Long userId);

    Optional<PhoneData> findByUserIdAndPhone(Long userId, String phone);

    Optional<PhoneData> findFirstByUserId(Long userId);
}
