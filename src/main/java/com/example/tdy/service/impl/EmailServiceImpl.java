package com.example.tdy.service.impl;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.exception.RegisterException;
import com.example.tdy.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Date;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String username;

    @Override
    public void sendMessage(String email, String code) throws RegisterException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        String content = "<h1>" +  code + "</h1>, 3分钟内有效";
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
            helper.setSubject("【TDY】验证码"); // 邮件的标题
            helper.setFrom(username); // 发送者
            helper.setTo(email); // 接收者
            helper.setSentDate(new Date()); // 时间
            helper.setText(content, true); // 第二个参数true表示这是一个html文本
        } catch (Exception e) {
            throw new RegisterException(ExceptionConstant.EMAIL_SEND_FAIL);
        }
        javaMailSender.send(mimeMessage);
    }
}
