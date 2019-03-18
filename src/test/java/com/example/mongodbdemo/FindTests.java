package com.example.mongodbdemo;

import com.alibaba.fastjson.JSONObject;
import org.bson.Document;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.data.domain.Sort;
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

    /**
     * .$ 返回匹配的任意其中一个
     */
    @Test
    public void findListOneTest() {
        Query query = new Query(Criteria.where("comments.comment").is("good post"));
        query.fields().include("comments.$");
        List<Map> stock = mongoTemplate.find(query, Map.class, "blog.post");
        System.out.println(stock);
    }


    /**
     * $elemMatch 限制数组范围
     */
    @Test
    public void elemMatchTest(){

        //普通范围查询数组,只要数组的元素分别匹配条件,都会返回
        Query query = new Query(Criteria.where("x").gt(10).lt(20));
        List<JSONObject> elemMatch = mongoTemplate.find(query, JSONObject.class, "elemMatch");
        Integer warnNum = 0;
        for (JSONObject j:elemMatch
             ) {
            Object x = j.get("x");
            if(x instanceof Integer){
                int i = (Integer)x;
                Assert.assertTrue(i>10&&i<20);
            }else if(x instanceof List){
                List l = (List)x;
                for (Object lx:l
                        ) {
                    int i = (Integer)lx;
                    //查询数组返回 [5,25] ,与结果不匹配
                    if(i<10||i>20){
                        warnNum++;
                    }
                }

            }
        }

        //不会所有值都匹配
        Assert.assertTrue(warnNum>0);


    }


    /**
     * $elemMatch 限制数组范围
     * 只返回数组
     */
    @Test
    public void eleMathc1Test(){
        Query query = new Query(Criteria.where("x").elemMatch(new Criteria().gt(10).lt(20)));
        List<JSONObject> eleMathc = mongoTemplate.find(query, JSONObject.class, "eleMathc");
        eleMathc.forEach(j ->{
            List l = (List) j.get("x");
            for (Object lx:l
                    ) {
                int i = (Integer)lx;
                Assert.assertTrue(i>10&&i<20);{
                }
            }
        });

    }


    /**
     * $eleMatch 模糊条件查询内嵌文档
     */
    @Test
    public void eleMatch2(){
        Query query = new Query(Criteria.where("comments").
                elemMatch(new Criteria().where("author").is("Jim").and("votes").is(1.0)));
        List<JSONObject> jsonObjects = mongoTemplate.find(query, JSONObject.class, "blog.post");
        System.out.println(jsonObjects);
    }

    /**
     * limit 只返回指定数量结果
     */
    @Test
    public void limitTest(){
        List<JSONObject> user = mongoTemplate.find(new Query().limit(1), JSONObject.class, "user");
        Assert.assertTrue(user.size()==1);
    }


    /**
     * skip 跳过前x个文档,返回后面的文档
     */
    @Test
    public void skipTest(){
        List<JSONObject> user = mongoTemplate.find(new Query().skip(100000), JSONObject.class, "user");
        Assert.assertTrue(user.size()==0);
    }


    /**
     * sort 字段排序
     */
    @Test
    public void sortTest(){
        Query query = new Query().with(new Sort(Sort.Direction.DESC, "age", "username"));
        List<JSONObject> user = mongoTemplate.find(query, JSONObject.class, "user");
        System.out.println(user);
    }


    /**
     * $maxscan 本次查询扫描文档数量上限
     */
    @Test
    public void maxscanTest(){
        Query query = new Query().maxScan(1);
        List<JSONObject> user = mongoTemplate.find(query, JSONObject.class, "user");
        Assert.assertTrue(user.size()==1);
    }


    /**
     * $min 从索引比较,字段必须有索引
     * $max
     */
    @Test
    public void minMaxTest(){

    }


    /**
     * 查询快照,在快照上查询
     * 查询在_id索引上遍历执行
     * 保证每个文档只返回一次
     */
    @Test
    public void snapshotTest(){
        Query query = new Query().useSnapshot();


    }








}
