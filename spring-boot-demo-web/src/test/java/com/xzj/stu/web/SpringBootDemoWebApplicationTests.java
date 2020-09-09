package com.xzj.stu.web;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class SpringBootDemoWebApplicationTests {
    @Autowired
    private RestTemplate restTemplate;

    @Test
    public void contextLoads() {
        log.info("start...");
        String forObject = restTemplate.getForObject("https://translate.google.cn/#view=home&op=translate&sl=auto&tl=zh-CN&text=allocated", String.class);
        log.info("result={}", forObject);
        log.info("end.");
    }

}
