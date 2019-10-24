package com.xzj.stu.redis;

import com.alibaba.fastjson.JSONObject;
import com.xzj.stu.redis.common.utils.RedisUtil;
import com.xzj.stu.redis.entity.UserPO;
import com.xzj.stu.redis.service.RedisSender;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author zhijunxie
 * @date 2019/10/23 15:28
 */
public class RedisTest extends SpringBootDemoRedisApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private RedisSender redisSender;

    @Test
    @Ignore
    public void test_01_redis() {
        LOGGER.info("redis test starting.");
        UserPO userPO = UserPO.builder().guid("123456").name("xzj").age(29).address("bj china").build();
        Assert.assertTrue(redisUtil.set("user_test", userPO));
        LOGGER.info("user = {}", JSONObject.toJSONString((UserPO)redisUtil.get("user_test")));
        LOGGER.info("redis test end.");
    }

    @Test
    @Ignore
    public void test_03_pipeline() {

        HashMap<String, String> pipeMap = new HashMap<>();
        HashMap<String, String> singleMap = new HashMap<>();
        for (int i = 0; i < 1000; i++) {
            pipeMap.put("pipe_test"+i,"pipe"+i);
            singleMap.put("single_test"+i,"single"+i);
        }
        long start = System.currentTimeMillis();
        for (Map.Entry<String, String> entry : singleMap.entrySet()) {
            redisUtil.set(entry.getKey(), entry.getValue());
        }
        LOGGER.info("一次发送一个请求耗时，{} ms", (System.currentTimeMillis() - start));

        start = System.currentTimeMillis();
        List<Object> list = redisUtil.pipelinedSet(pipeMap);
        LOGGER.info("管道发送请求耗时，{} ms", (System.currentTimeMillis() - start));

        list.forEach((result) -> {
            Assert.assertTrue((Boolean) result);
        });
    }

    @Test
    public void test_05_pubAndSub() {
        for (int i = 0; i < 10; i++) {
            redisSender.sendMsgToChannel("pubSubChannel", "发布消息测试"+i);
        }
        try {
            Thread.sleep(10000L);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
