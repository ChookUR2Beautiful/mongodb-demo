package com.example.mongodbdemo;

import com.mongodb.client.result.UpdateResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.time.LocalDate;
import java.util.*;

/**
 * @author jianjian on 2019/3/3.
 */
@DataMongoTest
public class InsertUpdateDeleteTests extends MongodbDemoApplicationTests{


    @Autowired
    MongoTemplate mongoTemplate;

    /**
     * 批量插入
     * 当某一条插入失败,后面的就不继续插入,可以配置continueOnError选项
     */
    @Test
    public void batchInsertTest(){
        List<Map<String,Object>> list = new ArrayList<>();
        HashMap<String, Object> n0 = new HashMap<>();
        n0.put("_id",0);
        HashMap<String, Object> n1 = new HashMap<>();
        n1.put("_id",1);
        HashMap<String, Object> n2 = new HashMap<>();
        n2.put("_id",2);
        list.add(n0);
        list.add(n1);
        list.add(n2);
        mongoTemplate.insert(list,"foo");
    }


    /**
     * 条件删除
     */
    @Test
    public void removeTest(){
        Criteria criteria = new Criteria();
        criteria.where("opt-out").is(true);
        mongoTemplate.remove(new Query(criteria),"mailing");
    }


    /**
     * 删除集合
     * 速度比remove快
     */
    @Test
    public void dropTest(){
        mongoTemplate.dropCollection("tester");
    }

    /**
     * $inc 计数器修改器
     */
    @Test
    public void incTest(){
        Query query = new Query(new Criteria().where("url").is("www.example.com"));
        Update update = new Update().inc("pageviews", 1);
        mongoTemplate.updateFirst(query,update,"analytics");
    }


    /**
     * $set 修改器
     * 修改字段,不存在则添加字段
     */
    @Test
    public void setTest(){
        Query query = new Query(new Criteria().where("_id").is("5c7bee1880bac4a4da725f57"));
        Update set = new Update().set("favorite book", "War and Peace");
        mongoTemplate.updateFirst(query,set,"users");
        //修改字段类型
        Update update = new Update().set("favorite book", Arrays.asList("book1","book2","book3"));
        mongoTemplate.updateFirst(query,update,"users");

        //修改内嵌文档
        Query joe = new Query(new Criteria().where("author.name").is("joe"));
        Update joeSchmoe = new Update().set("author.name","joe schmoe");
        mongoTemplate.updateFirst(joe,joeSchmoe,"posts");
    }


    /**
     * $unset 删除键
     */
    @Test
    public void unsetTest(){
        Query query = new Query(new Criteria().where("_id").is("5c7bee1880bac4a4da725f57"));
        Update set = new Update().unset("favorite book");
        mongoTemplate.updateFirst(query,set,"users");
    }

    /**
     * $push 数组修改器
     * 添加数组字段,向数组里添加值
     */
    @Test
    public void pushTest(){
        Query query = new Query(new Criteria().where("title").is("A Blog Post"));
        HashMap<String, Object> object = new HashMap<>();
        object.put("name","joe");
        object.put("email","joe@example.com");
        object.put("content","nice post.");
        Update push = new Update().push("comments",object);
        mongoTemplate.updateFirst(query,push,"posts");
    }


    /**
     * $each 配合 $push 数组添加多个值
     */
    @Test
    public void eachTest(){
        Query query = new Query(new Criteria().where("_id").is("GOOG"));
        Update update = new Update().push("hourly").each(562.776, 562.790, 559.123);
        mongoTemplate.updateFirst(query,update,"stock.ticker");
    }


    /**
     * $slice 数组数量大小,只保留最后的值
     * $sort 清理前排序
     */
    @Test
    public void sliceTest(){
        Query query = new Query(new Criteria().where("genre").is("horror"));
        HashMap<String, Object> o1 = new HashMap<>();
        o1.put("name","n1");
        o1.put("rating",6.6);
        HashMap<String, Object> o2 = new HashMap<>();
        o2.put("name","Saw");
        o2.put("rating",4.3);
        Update update = new Update().push("top10")
                .slice(-10).sort(new Sort(Sort.Direction.ASC, "rating"))
                .each(o1, o2);
        mongoTemplate.updateFirst(query,update,"movies");
    }


    /**
     * $ne查询列表不存在的值  配合$push元素不重复
     */
    @Test
    public void neTest(){
        Query query = new Query(new Criteria().where("authors cited").ne("Richie"));
        Update update = new Update().push("authors cited","Richie");
        mongoTemplate.updateFirst(query,update,"papers");
    }


    /**
     * $addToSet 插入值,避免重复插入
     */
    @Test
    public void addToSetTest(){
        Query query = new Query(new Criteria().where("_id").is("5c7bee1880bac4a4da725f57"));
        Update update = new Update().addToSet("emails","joe@gmail.com");
        mongoTemplate.updateFirst(query,update,"users");
    }


    /**
     * $addToSet配合$each添加多个值
     */
    @Test
    public void addToSetEachTest(){
        Query query = new Query(new Criteria().where("_id").is("5c7bee1880bac4a4da725f57"));
        Update update = new Update().addToSet("emails").each("joe@example.com","joe@yahoo.com");
        mongoTemplate.updateFirst(query,update,"users");
    }


    /**
     * $pop 删除数组头尾元素
     * 1:从尾删除 -1:从头删除
     */
    @Test
    public void popTest(){
        Query query = new Query(new Criteria().where("_id").is("5c7bee1880bac4a4da725f57"));
        //Position.LAST 尾部
        Update update = new Update().pop("emails", Update.Position.LAST);
        mongoTemplate.updateFirst(query,update,"users");
    }


    /**
     * $pull 删除数组匹配的元素
     */
    @Test
    public void pullTest(){
        Query query = new Query(new Criteria().where("_id").is("GOOG"));
        Update update = new Update().pull("hourly",562.79);
        mongoTemplate.updateFirst(query,update,"stock.ticker");
    }


    /**
     * $ 数组定位符
     */
    @Test
    public void indexTest(){
        Query query = new Query(new Criteria().where("comments.author").is("John"));
        //修改第一条为John的值为Jim
        Update update = new Update().set("comments.$.author","Jim");
        mongoTemplate.updateFirst(query,update,"blog.post");

    }


    /**
     * upsert 条件修改文档,不存在则新增 (原子性)
     */
    @Test
    public void upsertTest(){
        //指定值是25 不存在会创建并相加
        Query query = new Query(Criteria.where("pageviews").is(25).and("url").is("/blog"));
        Update update = new Update().inc("pageviews",1);
        mongoTemplate.upsert(query,update,"analytics");
    }


    /**
     * setOnInsert 在创建文档时添加字段和值 (只在集合没数据时有效)
     */
    @Test
    public void setOnInsertTest(){
        Query query = new Query();
        Update update = new Update().setOnInsert("createdAt", new Date());
        mongoTemplate.upsert(query,update,"setOnInsert");
    }


    /**
     * save函数 有_id是修改,没有_id时新增
     */
    @Test
    public void saveTest(){

        HashMap<String, Object> map = new HashMap<>();
        map.put("_id",0);
        map.put("type","save");
        mongoTemplate.save(map,"foo");

        HashMap<String, Object> insert = new HashMap<>();
        insert.put("type","insert");
        mongoTemplate.save(insert,"foo");

    }


    /**
     * 更新多个文档
     */
    @Test
    public void  batchUpdateTest(){
        Query query = new Query(Criteria.where("age").is(1));
        Update update = new Update().set("gift","Happy Birthday1");
        UpdateResult user = mongoTemplate.updateMulti(query, update, "user");
        //修改数量
        System.out.println(user.getModifiedCount());
    }


    /**
     * 返回被更新的文档,一条
     */
    @Test
    public void findAndModifyTest(){
        Query query = new Query(Criteria.where("age").is(1));
        Update update = new Update().set("gift","Happy Birthday3");
        Map user = mongoTemplate.findAndModify(query, update, Map.class, "user");
        System.out.println(user);
    }



}
