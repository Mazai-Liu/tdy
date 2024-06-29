package com.example.tdy.service.impl;

import com.example.tdy.constant.ExceptionConstant;
import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.dto.LoginDto;
import com.example.tdy.dto.RegisterDto;
import com.example.tdy.entity.Favorite;
import com.example.tdy.entity.File;
import com.example.tdy.entity.User;
import com.example.tdy.exception.LoginException;
import com.example.tdy.exception.RegisterException;
import com.example.tdy.mapper.FavoriteMapper;
import com.example.tdy.mapper.FileMapper;
import com.example.tdy.mapper.UserMapper;
import com.example.tdy.service.LoginService;
import com.example.tdy.utils.CozeUtil;
import com.example.tdy.utils.JwtUtil;
import com.example.tdy.utils.ThreadPoolUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
@Service
public class LoginServiceImpl implements LoginService {
    Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private CozeUtil cozeUtil;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private ThreadPoolUtil threadPoolUtil;

    @Override
    @Transactional
    public void register(RegisterDto registerDto) throws RegisterException {
        // 判断邮箱重复
        if (userMapper.selectByEmail(registerDto.getEmail()) != null) {
            throw new RegisterException(ExceptionConstant.EMAIL_EXIST);
        }

        User user = new User();
        user.setEmail(registerDto.getEmail());
        user.setPassword(registerDto.getPassword());
        user.setNickname(registerDto.getNickName());
        user.setDescription(SystemConstant.DEFAULT_DESCRIPTION);
        user.setState(SystemConstant.USER_NORMAL);
        getAvatarFileId(user);
        userMapper.insert(user);

        Favorite favorite = new Favorite();
        favorite.setName(SystemConstant.DEFAULT_FAVORITE_NAME);
        favorite.setOpen(SystemConstant.FAVORITE_PUBLIC);
        favorite.setUserId(user.getId());
        favorite.setSize(0);
        favorite.setDescription(SystemConstant.DEFAULT_FAVORITE_DESCRIPTION);
        favoriteMapper.insert(favorite);

        logger.info("用户注册成功：{}", user);

        user.setDefaultFavorite(favorite.getId());
        userMapper.setDefaultFavorite(user);
    }

    @Override
    public Map<String, String> login(LoginDto loginDto) throws LoginException {
        // 判断用户是否存在
        User user = userMapper.selectByEmail(loginDto.getEmail());

        // TODO 密码加密

        if(user == null || !user.getPassword().equals(loginDto.getPassword())) {
            throw new LoginException(ExceptionConstant.EMAIL_PWD_ERROR);
        }

        // 生成token
        Map<String, Object> claim = new HashMap<String, Object>(){{
            put("id", user.getId());
        }};
        String token = JwtUtil.createJWT(claim);

        // token放入redis
        String key = RedisConstant.TOKEN_PREFIX + token;
        stringRedisTemplate.opsForValue().set(key, String.valueOf(user.getId()), RedisConstant.TOKEN_TIMEOUT, RedisConstant.TOKEN_TIMEOUT_UNIT);

        // 封装返回结果
        Map<String, String> data = new HashMap<>();
        data.put("name", user.getNickname());
        data.put("token", token);
        return data;
    }

    public void getAvatarFileId(User user) {
        threadPoolUtil.submit(()->{
            String prompt = "请生成一张可爱、性感、悠闲风格的头像";
            File file = new File();

            String avatar_path = cozeUtil.promptToAvatar(prompt);

            file.setFileKey(avatar_path);
            file.setFormat("png");
            file.setType("图片");
            file.setUserId(BaseContext.getCurrentId());

            fileMapper.insert(file);

            user.setAvatar(file.getId());
        });

    }
}
