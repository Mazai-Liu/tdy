package com.example.tdy.service;

import com.example.tdy.dto.LoginDto;
import com.example.tdy.dto.RegisterDto;
import com.example.tdy.exception.LoginException;
import com.example.tdy.exception.RegisterException;

import java.util.Map;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
public interface LoginService {
    void register(RegisterDto registerDto) throws RegisterException;

    Map<String, String> login(LoginDto registerDto) throws LoginException;
}
