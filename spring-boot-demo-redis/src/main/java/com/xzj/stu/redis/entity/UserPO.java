package com.xzj.stu.redis.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author zhijunxie
 * @date 2019/10/23 15:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserPO {
    private String guid;
    private String name;
    private Integer age;
    private String address;
    private Date createTime;
}
