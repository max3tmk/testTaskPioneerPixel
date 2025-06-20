package com.max.pioneer_pixel.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "search")
@Getter
@Setter
public class SearchProperties {
    /**
     * Тип поиска: "jpa" или "elastic"
     */
    private String type = "jpa";
}
