package com.example.tdy.service;

import com.example.tdy.exception.RegisterException;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
public interface EmailService {
    void sendMessage(String email, String code) throws RegisterException;
}
