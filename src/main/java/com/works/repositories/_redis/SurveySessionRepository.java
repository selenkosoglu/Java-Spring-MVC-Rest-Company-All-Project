package com.works.repositories._redis;

import com.works.models._redis.SurveySession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

@EnableRedisRepositories
public interface SurveySessionRepository extends CrudRepository<SurveySession, String> {

    List<SurveySession> findByCompanynameEquals(String companyname, Pageable pageable);
    List<SurveySession> findByCompanynameEquals(String companyname);

}