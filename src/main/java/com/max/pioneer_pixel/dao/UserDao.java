package com.max.pioneer_pixel.dao;

import com.max.pioneer_pixel.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u " +
            "WHERE EXISTS (SELECT e FROM EmailData e WHERE e.user.id = u.id AND e.email = :email)")
    Optional<User> findByEmail(@Param("email") String email);

    @Query("SELECT u FROM User u " +
            "WHERE EXISTS (SELECT p FROM PhoneData p WHERE p.user.id = u.id AND p.phone = :phone)")
    Optional<User> findByPhone(@Param("phone") String phone);

    @Query("""
            SELECT u FROM User u
            LEFT JOIN EmailData e ON e.user.id = u.id
            LEFT JOIN PhoneData p ON p.user.id = u.id
            WHERE (:name IS NULL OR u.name LIKE CONCAT(:name, '%'))
              AND (:email IS NULL OR e.email = :email)
              AND (:phone IS NULL OR p.phone = :phone)
              AND (:dateOfBirth IS NULL OR u.dateOfBirth > :dateOfBirth)
            GROUP BY u
            """)
    Page<User> searchUsers(
            @Param("name") String name,
            @Param("email") String email,
            @Param("phone") String phone,
            @Param("dateOfBirth") LocalDate dateOfBirth,
            Pageable pageable
    );

    Optional<User> findByName(String name);
}
