package com.example.tdy.service;

import com.example.tdy.entity.Type;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */
public interface TypeService {
    List<Type> getTypes();

    List<String> random10Labels();
}
