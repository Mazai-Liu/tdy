package com.example.tdy.service;

import com.example.tdy.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * @author Mazai-Liu
 * @time 2024/4/29
 */
public interface InterestPushService {

    Collection<Integer> listByUserModel(User user);

    Collection<Integer> listByLabels(List<String> labels);
}
