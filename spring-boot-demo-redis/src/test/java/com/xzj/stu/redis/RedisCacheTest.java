package com.xzj.stu.redis;

import com.alibaba.fastjson.JSONObject;
import com.xzj.stu.redis.entity.UserPO;
import com.xzj.stu.redis.service.UserService;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

/**
 * 测试redis缓存
 * @author zhijunxie
 * @date 2019/10/23 15:59
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING) //测试方法执行顺序指定：按方法名。无法从父类继承
public class RedisCacheTest extends SpringBootDemoRedisApplicationTests {
    @Autowired
    private UserService userService;

    @Test
    public void test_01_get() {
        //第一次查询打印日志
        UserPO userPO = userService.get("guid1");
        LOGGER.info("userPO = {}", userPO);
        //第二次查询不打印日志，说明从缓存中获取数据
        UserPO userPO2 = userService.get("guid1");
        LOGGER.info("userPO2 = {}", userPO2);
    }

    @Test
    public void test_03_insert() {
        //先插入数据
        UserPO save = userService.insert(new UserPO("guid_test1", "xiezj1", 28, "china", new Date()));
        //查询，未打印日志，说明插入数据时已经缓存至redis
        UserPO userPO = userService.get(save.getGuid());
        LOGGER.info("user = {}", JSONObject.toJSONString(userPO));
    }

    @Test
    public void test_05_delete() {
        //第一次查询时，缓存数据
        UserPO userPO = userService.get("guid3");
        LOGGER.info("user = {}", JSONObject.toJSONString(userPO));
        //删除数据，并删除缓存中数据
        userService.delete("guid3");
        //查看redis，不存在数据
        UserPO userPO2 = userService.get("guid3");
        LOGGER.info("user = {}", JSONObject.toJSONString(userPO2));
    }
}
