package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.dao.EmailDataDao;
import com.max.pioneer_pixel.dao.PhoneDataDao;
import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.EmailData;
import com.max.pioneer_pixel.model.PhoneData;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.AccountService;
import com.max.pioneer_pixel.service.UserService;
import com.max.pioneer_pixel.service.elastic.ElasticUserSearchService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    private final UserDao userDao;
    private final EmailDataDao emailDataDao;
    private final PhoneDataDao phoneDataDao;
    private final AccountService accountService;
    private final PasswordEncoder passwordEncoder;
    private final ElasticUserSearchService elasticUserSearchService;


    @Value("${search.type:jpa}")
    private String searchType;

    @Override
    public User createUserWithAccount(User user, BigDecimal initialBalance) {
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password must not be empty");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userDao.save(user);
        accountService.createInitialAccount(savedUser, initialBalance);
        elasticUserSearchService.indexUser(savedUser);
        return savedUser;
    }

    @Cacheable(
            value = "usersSearchCache",
            keyGenerator = "customKeyGenerator"
    )
    @Override
    public Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        try {
            if ("elastic".equalsIgnoreCase(searchType)) {
                log.info("[SEARCH] Using Elasticsearch for user search.");
                return elasticUserSearchService.searchUsers(name, email, phone, dateOfBirth, pageable);
            } else {
                log.info("[SEARCH] Using JPA for user search.");
                return userDao.searchUsers(name, email, phone, dateOfBirth, pageable);
            }
        } catch (Exception e) {
            log.error("Error searching users", e);
            return new PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    @Override
    @Transactional
    public void addEmail(Long userId, String email) {
        if (emailDataDao.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email is already taken by another user");
        }
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        EmailData newEmail = new EmailData();
        newEmail.setUser(user);
        newEmail.setEmail(email);
        emailDataDao.save(newEmail);

        elasticUserSearchService.indexUser(user);
    }

    @Override
    @Transactional
    public void deleteEmail(Long userId, String email) {
        emailDataDao.findByUserIdAndEmail(userId, email)
                .ifPresent(emailDataDao::delete);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        elasticUserSearchService.indexUser(user);
    }

    @Override
    @Transactional
    public void addPhone(Long userId, String phone) {
        if (phoneDataDao.findByPhone(phone).isPresent()) {
            throw new IllegalArgumentException("Phone is already taken by another user");
        }
        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User with id " + userId + " not found"));
        PhoneData newPhone = new PhoneData();
        newPhone.setUser(user);
        newPhone.setPhone(phone);
        phoneDataDao.save(newPhone);
        elasticUserSearchService.indexUser(user);
    }

    @Override
    @Transactional
    public void deletePhone(Long userId, String phone) {
        phoneDataDao.findByUserIdAndPhone(userId, phone)
                .ifPresent(phoneDataDao::delete);

        User user = userDao.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        elasticUserSearchService.indexUser(user);
    }

    @Override
    public List<String> getEmails(Long userId) {
        List<EmailData> emails = emailDataDao.findByUserId(userId);
        if (emails == null || emails.isEmpty()) {
            return Collections.emptyList();
        }
        return emails.stream()
                .map(EmailData::getEmail)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getPhones(Long userId) {
        List<PhoneData> phones = phoneDataDao.findByUserId(userId);
        if (phones == null || phones.isEmpty()) {
            return Collections.emptyList();
        }
        return phones.stream()
                .map(PhoneData::getPhone)
                .collect(Collectors.toList());
    }
}
