package com.example.mongodbdemo;

import com.example.mongodbdemo.repository.UserRepository;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.query.Criteria;

import java.util.List;
import java.util.Map;

/**
 * @author jianjian
 */
@DataMongoTest
public class AggregateTests extends MongodbDemoApplicationTests{

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 文档内容过滤
     */
    @Test
    public void matchTest(){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("username").is("狗剩"))
        );
        AggregationResults<Map> user = mongoTemplate.aggregate(aggregation, "user", Map.class);
        List<Map> mappedResults = user.getMappedResults();
        System.out.println(mappedResults);
    }


    @Test
    public void projectTest(){
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.project("username","age")
        );
        AggregationResults<Map> user = mongoTemplate.aggregate(aggregation, "user", Map.class);
        List<Map> mappedResults = user.getMappedResults();
        System.out.println(mappedResults);
    }

}

    
    