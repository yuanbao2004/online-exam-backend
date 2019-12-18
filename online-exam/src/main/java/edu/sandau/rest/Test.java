package edu.sandau.rest;

import com.alibaba.fastjson.JSON;
import edu.sandau.service.EmailService;
import edu.sandau.model.EmailVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Slf4j
@Path("test")
public class Test {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private EmailService emailService;

    @GET
    @Path("mail")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String sendMail() throws Exception {
        EmailVo emailVo = new EmailVo();
        emailVo.setTos("test@test.com");
        emailVo.setContent("你好，测试");
        emailVo.setSubject("test");
        emailService.send(emailVo);
        return null;
    }

    @GET
    @Path("show")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String topicShow() throws Exception {
        String sql = "SELECT * FROM topics";
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(ConnectionManager.getDataSource());
        List list = new ArrayList();
        for ( int i = 0; i < 100000; i++ ) {
            list = jdbcTemplate.queryForList(sql);
        }
        return JSON.toJSONString(list);
//        topticsService.read();
//        return null;
    }

    @POST
    @Path("post")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })    //使用 application/x-www-form-urlencoded
    @Produces({ MediaType.APPLICATION_JSON })   //使用@FormParam 可接收单个name，如果传的是list可在postman里设多个相同name的参数
    public String post(@FormParam("key") String value, @FormParam("key1") String value1, @FormParam("key2") List<String> value2 ) throws Exception {
        log.info("key = " + value + "\tkey1 = " + value1 + "\tkey2 = " + value2);
        return null;
    }

    @POST
    @Path("postlist")
    @Consumes({ MediaType.APPLICATION_FORM_URLENCODED })
    @Produces({ MediaType.APPLICATION_JSON })   //使用MultivaluedMap可以接受复杂类型数据，会将所有key的值作为list接收，类似于 Map<List> 类型
    public String postList(MultivaluedMap map) throws Exception {
        log.info(map.toString());
        List l = (List)map.get("key");
        String s = (String) l.get(0);
        log.info(s);
        return null;
    }

    @GET
    @Path("redis")
    @Consumes({ MediaType.APPLICATION_JSON })
    @Produces({ MediaType.APPLICATION_JSON })
    public String redis() throws Exception {
        SetOperations setOperations = redisTemplate.opsForSet();

        /**
         * 添加，类似于
         *
         *  Map<Object, Set<Object>> map = new HashMap<Object, Set<Object>>();
         *  Set<Object> set = new HashSet<Object>();
         *  map.put(key,set);
         *  Set<Object> set = map.get(key);
         *  set.add(value);
         */
        setOperations.add("springData","redis");
        //获取
        Set springData = setOperations.members("springData");
        System.out.println(springData);
        //获取两个key的value(Set)交集
        setOperations.intersect("springData","SpringData1");

        //获取两个key的value(Set)补集
        setOperations.difference("springData","SpringData1");

        //获取两个key的value(Set)并集
        setOperations.union("springData","SpringData1");

        String key = "springData";
        BoundSetOperations boundSetOperations = redisTemplate.boundSetOps(key);
        return null;
    }

}
