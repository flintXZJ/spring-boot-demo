package com.xzj.stu.redis;

import com.alibaba.fastjson.JSONObject;
import com.xzj.stu.redis.common.utils.RedisUtil;
import com.xzj.stu.redis.entity.UserPO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 * @author zhijunxie
 * @date 2019/10/23 15:28
 */
public class RedisTest extends SpringBootDemoRedisApplicationTests {
    @Autowired
    private RedisUtil redisUtil;

    @Test
    public void setToRedisTest() {
        LOGGER.info("redis test starting.");
        UserPO userPO = UserPO.builder().guid("123456").name("xzj").age(29).address("bj china").build();
        Assert.assertTrue(redisUtil.set("user_test", userPO));
        LOGGER.info("user = {}", JSONObject.toJSONString((UserPO)redisUtil.get("user_test")));
        LOGGER.info("redis test end.");
    }
}
