package com.example.tdy.utils;

import com.example.tdy.service.impl.UserServiceImpl;
import io.jsonwebtoken.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/3/22
 */

public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);
    private static final String SECRET = "Mazai-Liu";
    private static final long EXPIRATION = 1000 * 60 * 60 * 24 * 7;

    /**
     * 生成jwt
     * 使用Hs256算法, 私匙使用固定秘钥
     *
     * @param claims    设置的信息
     * @return
     */
    public static String createJWT(Map<String, Object> claims) {
        // 指定签名的时候使用的签名算法，也就是header那部分
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

        // 生成JWT的时间
        long expMillis = System.currentTimeMillis() + EXPIRATION;
        Date exp = new Date(expMillis);

        // 设置jwt的body
        JwtBuilder builder = Jwts.builder()
                // 如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                .setClaims(claims)
                // 设置签名使用的签名算法和签名使用的秘钥
                .signWith(signatureAlgorithm, SECRET.getBytes(StandardCharsets.UTF_8))
                // 设置过期时间
                .setExpiration(exp);

        return builder.compact();
    }

    /**
     * Token解密
     *
     * @param token     加密后的token
     * @return
     */
    public static Claims parseJWT(String token) {
        Claims claims;
        try{
            // 得到DefaultJwtParser
            claims = Jwts.parser()
                    // 设置签名的秘钥
                    .setSigningKey(SECRET.getBytes(StandardCharsets.UTF_8))
                    // 设置需要解析的jwt
                    .parseClaimsJws(token).getBody();
        } catch (Exception e) {
            logger.error("token解析失败", e);
            claims = null;
        }

        return claims;
    }

}
