package com.example.tdy.service.impl;

import com.example.tdy.entity.Type;
import com.example.tdy.mapper.TypeMapper;
import com.example.tdy.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/4/13
 */

@Service
public class TypeServiceImpl implements TypeService {

    @Autowired
    private TypeMapper typeMapper;

    @Override
    public List<Type> getTypes() {
        return typeMapper.selectAll();
    }
}
