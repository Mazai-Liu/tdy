package com.example.tdy.controller;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.dto.EmailDto;
import com.example.tdy.dto.LoginDto;
import com.example.tdy.dto.RegisterDto;
import com.example.tdy.exception.LoginException;
import com.example.tdy.exception.RegisterException;
import com.example.tdy.result.R;
import com.example.tdy.service.EmailService;
import com.example.tdy.service.LoginService;
import com.wf.captcha.SpecCaptcha;
import com.wf.captcha.utils.CaptchaUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Map;
import java.util.concurrent.TimeUnit;


/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@RestController
@RequestMapping("/login")
@Validated
public class LoginController {
    Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private EmailService emailService;

    @Autowired
    private LoginService loginService;



    @PostMapping("")
    public R<Map<String, String>> login(@RequestBody @Validated LoginDto registerDto) throws LoginException {
        Map<String, String> data = loginService.login(registerDto);

        return R.ok(data);
    }

    @PostMapping("/register")
    public R register(@RequestBody @Validated RegisterDto registerDto) throws RegisterException {
        loginService.register(registerDto);
        return R.ok();
    }


    @PostMapping("/check")
    public R check(@NotBlank String email, @NotNull String code){
        logger.info("验证邮箱验证码");
        // 验证邮箱验证码
        String key = RedisConstant.EMAIL_PREFIX + email;
        String value = stringRedisTemplate.opsForValue().get(key);

        return (value != null && value.equals(code)) ? R.ok() : R.error(ExceptionConstant.CODE_NOT_MATCH);
    }


    @RequestMapping("/captcha.jpg/{uuid}")
    public void getCaptcha(@PathVariable("uuid") @NotBlank String uuid, HttpServletRequest request, HttpServletResponse response) throws Exception {
        logger.info("获取图形验证码");
        // 生成图形验证码
        SpecCaptcha specCaptcha = new SpecCaptcha(130, 48, 4);

        // 放入redis
        String key = RedisConstant.VERIFY_PREFIX +uuid;
        String value = specCaptcha.text().toLowerCase();
        stringRedisTemplate.opsForValue().set(key, value, RedisConstant.VERIFY_TIMEOUT, TimeUnit.MINUTES);

        // 写回
        CaptchaUtil.setHeader(response);
        specCaptcha.out(response.getOutputStream());


    }

    @PostMapping("/getCode")
    public R getEmailCode(@RequestBody @Validated EmailDto emailDto) throws Exception {
        logger.info("获取邮件验证码");
        // 查看图像验证码是否过期
        String key = RedisConstant.VERIFY_PREFIX + emailDto.getUuid();
        String value = stringRedisTemplate.opsForValue().get(key);
        if(StringUtils.isEmpty(value)) {
            throw new RegisterException(ExceptionConstant.CODE_TIMEOUT);
        }

        // 查看是否已存在code
        key = RedisConstant.EMAIL_PREFIX + emailDto.getEmail();
        value = stringRedisTemplate.opsForValue().get(key);
        if(!StringUtils.isEmpty(value)) {
            throw new RegisterException(ExceptionConstant.EMAIL_CODE_EXIST);
        }

        // 发送邮件
        int random = (int) ((Math.random()*9+1) * 100000);
        String code = String.valueOf(random);
        logger.info("邮箱验证码: {}", code);
        emailService.sendMessage(emailDto.getEmail(), code);

        stringRedisTemplate.opsForValue().set(key, code, RedisConstant.MAIL_TIMEOUT, TimeUnit.MINUTES);

        return R.ok();
    }
}
