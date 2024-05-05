package com.example.tdy.service.impl;

import com.example.tdy.entity.Type;
import com.example.tdy.mapper.TypeMapper;
import com.example.tdy.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
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

    @Override
    public List<String> random10Labels() {
        final List<Type> types = getTypes();
        Collections.shuffle(types);
        final ArrayList<String> labels = new ArrayList<>();
        for (Type type : types) {
            for (String label : type.buildLabel()) {
                if (labels.size() == 10){
                    return labels;
                }
                labels.add(label);
            }
        }
        return labels;
    }
}
