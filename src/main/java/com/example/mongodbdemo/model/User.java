package com.example.mongodbdemo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;

/**
 * Created by jianjian on 2019/2/24.
 */
@Data
@AllArgsConstructor
public class User {

    @Id
    private Long id;

    private String username;
    private Integer age;

}
