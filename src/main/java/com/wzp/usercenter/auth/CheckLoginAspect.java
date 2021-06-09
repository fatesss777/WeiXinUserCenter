package com.wzp.usercenter.auth;

import com.wzp.usercenter.util.JwtOperator;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

@Aspect
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class CheckLoginAspect
{
    private final JwtOperator jwtOperator;
    @Around("@annotation(com.wzp.usercenter.auth.CheckLogin)")
    public Object checkLogin(ProceedingJoinPoint point)
    {
        try
        {
            //获取token
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
            HttpServletRequest request = attributes.getRequest();
            String token = request.getHeader("X-Token");

            //检验token是否合法&&是否过期
            Boolean isValid = jwtOperator.validateToken(token);
            if (!isValid)
            {
                return new SecurityException("token不合法");
            }

            //如果校验成功就将用户信息设置到request的Attribute里面
            Claims claims = jwtOperator.getClaimsFromToken(token);
            request.setAttribute("id",claims.get("id"));
            request.setAttribute("wxNickname", claims.get("wxNickname"));
            request.setAttribute("role",claims.get("role"));

            return point.proceed();
        }
        catch (Throwable throwable)
        {
            throw new SecurityException("Token不合法");
        }

    }
}
