package com.works.repositories._redis;

import com.works.models._redis.LikeSession;
import com.works.models._redis.SurveySession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
@EnableRedisRepositories
public interface LikeSessionRepository extends CrudRepository<LikeSession,String> {

    //@Query("select s from sessionlike s order by s.id")
    //List<LikeSession> findByOrderByIdAsc(Pageable pageable);

    List<LikeSession> findByCompanynameEquals(String companyname, Pageable pageable);


}
