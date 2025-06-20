package com.max.pioneer_pixel.init;

import com.max.pioneer_pixel.dao.UserDao;
import com.max.pioneer_pixel.model.User;
import com.max.pioneer_pixel.service.elastic.ElasticUserSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ElasticIndexInitializer {

    private final UserDao userDao;
    private final ElasticUserSearchService elasticUserSearchService;

    @EventListener(ApplicationReadyEvent.class)
    public void indexAllUsers() {
        log.info("Starting Elasticsearch indexing of users on application startup...");

        List<User> users = userDao.findAll();

        users.forEach(user -> {
            try {
                elasticUserSearchService.indexUser(user);
            } catch (Exception e) {
                log.error("Error indexing user with id {}: {}", user.getId(), e.getMessage());
            }
        });

        log.info("Finished indexing {} users.", users.size());
    }
}
