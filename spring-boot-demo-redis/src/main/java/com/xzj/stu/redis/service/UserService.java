package com.xzj.stu.redis.service;

import com.xzj.stu.redis.entity.UserPO;

/**
 * @author zhijunxie
 * @date 2019/10/23 15:52
 */
public interface UserService {

    UserPO get(String guid);

    UserPO insert(UserPO userPO);

    UserPO update(UserPO userPO);

    void delete(String guid);
}
