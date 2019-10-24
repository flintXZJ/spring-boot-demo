package com.xzj.stu.redis.service.impl;

import com.google.common.collect.Maps;
import com.xzj.stu.redis.entity.UserPO;
import com.xzj.stu.redis.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.concurrent.ConcurrentMap;

/**
 * redis缓存测试
 *
 * @author zhijunxie
 * @date 2019/10/23 15:53
 */
@Service
@Slf4j
public class UserServiceImpl implements UserService {

    /**
     * 模拟数据库
     */
    private static ConcurrentMap<String, UserPO> dataBase = Maps.newConcurrentMap();

    /**
     * 模拟数据库中已存在数据
     */
    static {
        dataBase.put("guid1", new UserPO("guid1", "xzj-1",20,"china",new Date()));
        dataBase.put("guid2", new UserPO("guid2", "xzj-2",21,"china",new Date()));
        dataBase.put("guid3", new UserPO("guid3", "xzj-3",22,"china",new Date()));
    }

    @Cacheable(value = "user", key = "#guid")
    @Override
    public UserPO get(String guid) {
        //模拟从数据库获取数据
        log.info("获取用户，id={}", guid);
        return dataBase.get(guid);
    }

    /**
     * 注意方法返回类型
     * redis缓存好像缓存return内容
     * 确认？？？
     *
     * @param userPO
     * @return
     */
    @CachePut(value = "user", key = "#userPO.guid")
    @Override
    public UserPO insert(UserPO userPO) {
        dataBase.put(userPO.getGuid(), userPO);
        log.info("插入用户，id={}", userPO.getGuid());
        return userPO;
    }

    @CachePut(value = "user", key = "#userPO.guid")
    @Override
    public UserPO update(UserPO userPO) {
        dataBase.put(userPO.getGuid(), userPO);
        log.info("更新用户，id={}", userPO.getGuid());
        return userPO;
    }

    @CacheEvict(value = "user", key = "#guid")
    @Override
    public void delete(String guid) {
        dataBase.remove(guid);
        log.info("删除用户id={}", guid);
    }
}
