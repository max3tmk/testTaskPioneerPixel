package com.max.pioneer_pixel.dao.elastic;

import com.max.pioneer_pixel.model.elastic.ElasticUser;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticUserRepository extends ElasticsearchRepository<ElasticUser, Long> {
}
