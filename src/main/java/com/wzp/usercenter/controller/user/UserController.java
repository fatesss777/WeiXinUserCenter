package com.wzp.usercenter.controller.user;

import cn.binarywang.wx.miniapp.api.WxMaService;
import cn.binarywang.wx.miniapp.bean.WxMaJscode2SessionResult;
import com.wzp.usercenter.auth.CheckLogin;
import com.wzp.usercenter.domain.dto.user.JwtTokenRespDTO;
import com.wzp.usercenter.domain.dto.user.LoginRespDTO;
import com.wzp.usercenter.domain.dto.user.UserLoginDTO;
import com.wzp.usercenter.domain.dto.user.UserRespDTO;
import com.wzp.usercenter.domain.entity.user.User;
import com.wzp.usercenter.service.user.UserService;
import com.wzp.usercenter.util.JwtOperator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.chanjar.weixin.common.error.WxErrorException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping("/users")
public class UserController
{
    private final UserService userService;
    private final WxMaService wxMaService;
    private final JwtOperator jwtOperator;

    @GetMapping("/{id}")
    @CheckLogin
    public User findById(@PathVariable Integer id)
    {
        log.info("我被请求了...");
        return userService.findById(id);
    }

    /**
     * 模拟生成token(假的登录)
     */
    @GetMapping("/gen-token")
    public String genToken() {
        Map<String, Object> userInfo = new HashMap<>(3);
        userInfo.put("id", 2);
        userInfo.put("wxNickname", "bird");
        userInfo.put("role", "admin");
        return this.jwtOperator.generateToken(userInfo);
    }

    @PostMapping("/login")
    public LoginRespDTO login(@RequestBody UserLoginDTO userLoginDTO) throws WxErrorException
    {
        //微信小程序服务端校验是否已经登录的结果
        WxMaJscode2SessionResult result = this.wxMaService.getUserService()
                .getSessionInfo(userLoginDTO.getCode());

        //用户在微信的唯一标识
        String openId = result.getOpenid();

        User user = this.userService.login(userLoginDTO, openId);

        //颁发token
        Map<String, Object> userInfo = new HashMap<>(3);
        userInfo.put("id",user.getId());
        userInfo.put("wxNickname",user.getWxNickname());
        userInfo.put("role",user.getRoles());
        String token = this.jwtOperator.generateToken(userInfo);
        log.info(
            "用户{}登录成功，生成的token = {}, 有效期到:{}",
            userLoginDTO.getWxNickname(),
            token,
            jwtOperator.getExpirationTime()
        );
        //构建响应
        return LoginRespDTO.builder()
            .user(
                UserRespDTO.builder()
                    .id(user.getId())
                    .avatarUrl(user.getAvatarUrl())
                    .bonus(user.getBonus())
                    .wxNickname(user.getWxNickname())
                    .build()
            )
            .token(
                JwtTokenRespDTO.builder()
                    .expirationTime(jwtOperator.getExpirationTime().getTime())
                    .token(token)
                    .build()
            )
            .build();
    }
}
