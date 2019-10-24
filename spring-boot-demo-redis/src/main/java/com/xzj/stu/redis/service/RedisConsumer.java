package com.xzj.stu.redis.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * redis 消息消费者
 *
 * @author zhijunxie
 * @date 2019/10/24 17:38
 */
@Service
@Slf4j
public class RedisConsumer {
    public void receiveMessage(String message) {
       log.info("接收redis通道消息【{}】", message);
    }
}
