package com.wzp.usercenter.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

//@RestControllerAdvice作用：被 @ExceptionHandler、@InitBinder、@ModelAttribute 注解的方法，都会作用在 被 @RequestMapping 注解的方法上
@RestControllerAdvice
@Slf4j
public class GlobalExceptionErrorHandler
{
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<ErrorBody> error(SecurityException e)
    {
        log.info("发生SecurityException异常",e);
        return new ResponseEntity<>(
                ErrorBody.builder()
                        .body("Token不合法,用户不允许访问")
                        .status(HttpStatus.UNAUTHORIZED.value())
                        .build(),
                HttpStatus.UNAUTHORIZED
        );
    }
}

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
class ErrorBody
{
    private String body;
    private Integer status;
}
