package com.max.pioneer_pixel.service.elastic.impl;

import com.max.pioneer_pixel.dao.elastic.ElasticUserRepository;
import com.max.pioneer_pixel.model.EmailData;
import com.max.pioneer_pixel.model.PhoneData;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.model.elastic.ElasticUser;
import com.max.pioneer_pixel.service.EmailDataService;
import com.max.pioneer_pixel.service.PhoneDataService;
import com.max.pioneer_pixel.service.elastic.ElasticUserSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class ElasticUserSearchServiceImpl implements ElasticUserSearchService {

    private final ElasticUserRepository elasticUserRepository;
    private final EmailDataService emailDataService;
    private final PhoneDataService phoneDataService;

    @Override
    public void indexUser(User user) {
        String email = emailDataService.getEmailsByUserId(user.getId()).stream()
                .findFirst()
                .map(EmailData::getEmail)
                .orElse(null);

        String phone = phoneDataService.getPhonesByUserId(user.getId()).stream()
                .findFirst()
                .map(PhoneData::getPhone)
                .orElse(null);

        ElasticUser elasticUser = ElasticUser.builder()
                .id(user.getId())
                .name(user.getName())
                .dateOfBirth(user.getDateOfBirth())
                .email(email)
                .phone(phone)
                .build();

        elasticUserRepository.save(elasticUser);
    }

    @Override
    public Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        List<ElasticUser> all = StreamSupport
                .stream(elasticUserRepository.findAll().spliterator(), false)
                .collect(Collectors.toList());

        List<ElasticUser> filtered = all.stream()
                .filter(u -> name == null || (u.getName() != null && u.getName().startsWith(name)))
                .filter(u -> email == null || (u.getEmail() != null && u.getEmail().equals(email)))
                .filter(u -> phone == null || (u.getPhone() != null && u.getPhone().equals(phone)))
                .filter(u -> dateOfBirth == null || (u.getDateOfBirth() != null && u.getDateOfBirth().isAfter(dateOfBirth)))
                .toList();

        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), filtered.size());
        List<User> pageContent = filtered.subList(start, end).stream()
                .map(this::convertToUser)
                .collect(Collectors.toList());

        return new PageImpl<>(pageContent, pageable, filtered.size());
    }

    private User convertToUser(ElasticUser eu) {
        User user = new User();
        user.setId(eu.getId());
        user.setName(eu.getName());
        user.setDateOfBirth(eu.getDateOfBirth());
        return user;
    }
}
