package com.wzp.usercenter;

import com.wzp.usercenter.dao.user.UserMapper;
import com.wzp.usercenter.domain.entity.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController
{

    private final UserMapper userMapper;

    @GetMapping("/test")
    public User testInsert()
    {
        User user = new User();
        user.setAvatarUrl("xxxx");
        user.setBonus(100);
        user.setCreateTime(new Date());
        user.setUpdateTime(new Date());
        userMapper.insertSelective(user);
        return user;
    }
}
