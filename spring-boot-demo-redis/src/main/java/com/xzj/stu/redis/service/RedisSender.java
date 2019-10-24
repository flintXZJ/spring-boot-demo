package com.xzj.stu.redis.service;

import com.xzj.stu.redis.common.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * redis消息生产者
 *
 * @author zhijunxie
 * @date 2019/10/24 17:38
 */
@Service
@Slf4j
public class RedisSender {
    @Autowired
    private RedisUtil redisUtil;

    public void sendMsgToChannel(String channel, String message) {
        log.info("向通道【{}】发送消息【{}】", channel, message);
        redisUtil.convertAndSend(channel, message);
    }
}
