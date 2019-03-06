package com.example.mongodbdemo.repository;

import com.example.mongodbdemo.model.User;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Created by jianjian on 2019/2/24.
 */
public interface UserRepository extends MongoRepository<User,Long> {
}
