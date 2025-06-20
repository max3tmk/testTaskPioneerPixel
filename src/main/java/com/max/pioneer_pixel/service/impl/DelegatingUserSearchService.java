package com.max.pioneer_pixel.service.impl;

import com.max.pioneer_pixel.config.SearchProperties;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.UserSearchService;
import com.max.pioneer_pixel.service.elastic.impl.ElasticUserSearchServiceImpl;
import com.max.pioneer_pixel.service.jpa.impl.JpaUserSearchServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
@Primary
@RequiredArgsConstructor
public class DelegatingUserSearchService implements UserSearchService {

    private final SearchProperties searchProperties;
    private final JpaUserSearchServiceImpl jpaService;
    private final ElasticUserSearchServiceImpl elasticService;

    @Override
    public Page<User> searchUsers(String name, String email, String phone, LocalDate dateOfBirth, Pageable pageable) {
        String type = searchProperties.getType();
        if ("elastic".equalsIgnoreCase(type)) {
            return elasticService.searchUsers(name, email, phone, dateOfBirth, pageable);
        } else {
            return jpaService.searchUsers(name, email, phone, dateOfBirth, pageable);
        }
    }
}
