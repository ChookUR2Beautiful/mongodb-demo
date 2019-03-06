package com.example.mongodbdemo;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.ScriptOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.script.ExecutableMongoScript;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author jianjian on 2019/3/3.
 */
@DataMongoTest
public class ShellTests extends MongodbDemoApplicationTests{

    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 用js脚本添加一条数据
     */
    @Test
    public void scriptTest(){
        ScriptOperations scriptOps = mongoTemplate.scriptOps();

        ExecutableMongoScript echoScript = new ExecutableMongoScript("var post={\"title\":\"My Blog Post\",\"content\":\"Here's my blog post\",\"data\":new Date()};db.blog.insert(post);");
        scriptOps.execute(echoScript, "");

    }


    /**
     * 查询表中所有数据
     */
    @Test
    public void findTest(){
        List<Map> blog = mongoTemplate.find(new Query(), Map.class, "blog");
        //返回条数就是所有条数
        Assert.assertTrue(blog.size()>1);
        System.out.println(blog);
    }


    /**
     * 只查一条文档
     */
    @Test
    public void findOneTest(){
        Map blog = mongoTemplate.findOne(new Query(), Map.class, "blog");
        System.out.println(blog);
        Assert.assertEquals("5c7b874280bac4a4da72429e",blog.get("_id").toString());
    }


    /**
     * 旧文档添加新的键
     */
    @Test
    public void updateTest(){
        ScriptOperations scriptOps = mongoTemplate.scriptOps();
        //修改title为My Blog Post的文档添加comments字段
        ExecutableMongoScript echoScript = new ExecutableMongoScript(
                "var post={\"title\":\"My Blog Post\",\"content\":\"Here's my blog post\",\"data\":new Date(),\"comments\":[]};" +
                "db.blog.update({title:\"My Blog Post\"},post);");
        scriptOps.execute(echoScript, "");
    }


    /**
     * 删除
     */
    @Test
    public void removeTest(){
        //删除title字段为My Blog Post的文档
        Query query = new Query();
        query.addCriteria(new Criteria().where("title").is("My Blog Post"));
        mongoTemplate.remove(query,"blog");
    }





}
