package com.example.tdy.service.impl;

import com.example.tdy.context.BaseContext;
import com.example.tdy.dto.UpdateFavoriteDto;
import com.example.tdy.entity.Favorite;
import com.example.tdy.mapper.FavoriteMapper;
import com.example.tdy.service.FavoriteService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Override
    public List<Favorite> getFavoritesByUserId() {
        Integer currentId = BaseContext.getCurrentId();

        return favoriteMapper.selectByUserId(currentId);
    }

    @Override
    public Favorite getFavoriteById(Integer id) {
        return favoriteMapper.selectById(id);
    }

    @Override
    public void updateById(UpdateFavoriteDto updateFavoriteDto) {
        Favorite favorite = new Favorite();
        BeanUtils.copyProperties(updateFavoriteDto, favorite);
        favoriteMapper.update(favorite);
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        favoriteMapper.deleteByIds(ids);
    }
}
