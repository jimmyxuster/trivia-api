package com.dummy.trivia.config.mongo;

import com.mongodb.Mongo;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;

@Configuration
public class MongoConfig {
    @Bean
    public MongoOperations mongoTemplate(Mongo mongo) {
        //OrdersDB就是Mongo的数据库
        return new MongoTemplate(mongo, "trivia");
    }
}
