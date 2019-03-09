package com.example.mongodbdemo;

import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.BasicQuery;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author jianjian on 2019/3/8.
 */
@DataMongoTest
public class FindTests extends MongodbDemoApplicationTests {

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 指定返回什么键
     */
    @Test
    public void returnFieldTest(){
        Document dbObject = new Document();
        Document fieldsObject=new Document();
        fieldsObject.put("username", true);
        fieldsObject.put("age", true);
        Query query = new BasicQuery(dbObject, fieldsObject);
        List<Map> user = mongoTemplate.find(query, Map.class,"user");
        System.out.println(user);
        for (Map m:user
             ) {
            Assert.assertNotNull(m.get("username"));
            Assert.assertNotNull(m.get("age"));
        }

    }

    /**
     * 指定不返回什么键
     */
    @Test
    public void unReturnFieldTest(){
        Document dbObject = new Document();
        Document fieldsObject=new Document();
        fieldsObject.put("_id", false);
        Query query = new BasicQuery(dbObject, fieldsObject);
        List<Map> user = mongoTemplate.find(query, Map.class,"user");
        System.out.println(user);
        for (Map m:user
                ) {
            Assert.assertNull(m.get("_id"));
        }
    }


    /**
     * <,<=,>,>=  符号
     * $gte >=
     * $lte <=
     * $gt >
     * $lt <
     * $ne !=
     */
    @Test
    public void gteLteTest(){
        Query query = new Query(Criteria.where("age").gte(18).lte(30));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);
        for (Map m:user
                ) {
            Number age = (Number) m.get("age");

            Assert.assertTrue(age.intValue()>=18);
            Assert.assertTrue(age.intValue()<=30);
        }
    }


    /**
     * $in 在..中where xx in xx
     * $or 或者
     */
    @Test
    public void inOrTest(){
        Query query = new Query(new Criteria().orOperator(Criteria.where("age").is(30),Criteria.where("username").in("狗娃",123)));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);
        for (Map m:user
                ) {
            Object username = m.get("username");
            Number age = (Number) m.get("age");
            Assert.assertTrue("狗娃".equals(username)||age.intValue()==30);
        }

    }


    /**
     * $mod 取模运算
     */
    @Test
    public void modTest(){
        // age 除于18余0的值
        Query query = new Query(Criteria.where("age").mod(18,0));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);
        for (Map m:user
                ) {
            Number age = (Number) m.get("age");
            Assert.assertTrue(age.intValue()%18==0);
        }

    }


    /**
     * $not 取反
     */
    @Test
    public void notTest(){
        // age 除于18不余0的值
        Query query = new Query(Criteria.where("age").not().mod(18,0));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);
        for (Map m:user
                ) {
            Number age = (Number) m.get("age");
            Assert.assertTrue(age.intValue()%18!=0);
        }
    }


    /**
     * $exist 查询字段是null,但文档必须有此字段
     */
    @Test
    public void nullExistsTest(){
        Query query = new Query(Criteria.where("is_null").is(null).and("is_null").exists(true));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);
    }


    /**
     * 正则表达式
     */
    @Test
    public void regexTest(){
        Query query = new Query(Criteria.where("username").regex(Pattern.compile("^.*狗.*$", Pattern.CASE_INSENSITIVE)));
        List<Map> user = mongoTemplate.find(query, Map.class, "user");
        System.out.println(user);

    }


    /**
     * 查询数组 包含某个值
     */
    @Test
    public void findListTest(){
        Query query = new Query(Criteria.where("hourly").is(562.776));
        List<Map> stock = mongoTemplate.find(query, Map.class, "stock.ticker");
        System.out.println(stock);
    }



    /**
     * 查询数组 包含多个值
     */
    @Test
    public void findListAllTest() {
        Query query = new Query(Criteria.where("hourly").all(562.776, 559.123));
        List<Map> stock = mongoTemplate.find(query, Map.class, "stock.ticker");
        System.out.println(stock);
    }


    /**
     * 查询数组 根据下标值匹配
     */
    @Test
    public void findListIndexTest() {
        Query query = new Query(Criteria.where("hourly.1").is(559.123));
        List<Map> stock = mongoTemplate.find(query, Map.class, "stock.ticker");
        Assert.assertTrue(stock.size()>0);

        Query query1 = new Query(Criteria.where("hourly.1").is(559.124));
        List<Map> stock1 = mongoTemplate.find(query1, Map.class, "stock.ticker");
        Assert.assertTrue(stock1.size()==0);
    }


    /**
     * 查询数组 根据长度
     */
    @Test
    public void findListSTest() {
        Query query = new Query(Criteria.where("hourly").size(4));
        List<Map> stock = mongoTemplate.find(query, Map.class, "stock.ticker");
        System.out.println(stock);
    }


    /**
     * 查询数组 $slice
     * 10 返回前10条
     * -10 返回后10条
     * 10,10 从第10条开始返回10条
     */
    @Test
    public void findListSliceTest() {
        Query query = new Query();
        query.fields().slice("hourly",2,2);
        List<Map> stock = mongoTemplate.find(query, Map.class, "stock.ticker");
        System.out.println(stock);
    }


}
