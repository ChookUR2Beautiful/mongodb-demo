package com.example.mongodbdemo;

import com.example.mongodbdemo.model.User;
import com.example.mongodbdemo.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by jianjian on 2019/2/24.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@DataMongoTest
public class ClientTests {


    @Autowired
    private UserRepository userRepository;


    @Test
    public void saveTest(){
        userRepository.save(new User(1L, "didi", 30));
    }



}
