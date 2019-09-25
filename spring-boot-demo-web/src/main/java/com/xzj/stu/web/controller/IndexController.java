package com.xzj.stu.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @author zhijunxie
 * @date 2019/9/25 15:38
 */
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index(HttpServletRequest request) {
        request.setAttribute("name", "xzj");
        return "index";
    }

    @GetMapping("test")
    @ResponseBody
    public Map<String, String> test() {
        Map<String, String> hashMap = new HashMap<>();
        hashMap.put("test", "test");
        hashMap.put("name", "xzj");
        hashMap.put("age", "29");
        return hashMap;
    }
}
