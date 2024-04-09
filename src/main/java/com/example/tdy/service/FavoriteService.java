package com.example.tdy.service;

import com.example.tdy.dto.UpdateFavoriteDto;
import com.example.tdy.entity.Favorite;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */
public interface FavoriteService {

    List<Favorite> getFavoritesByUserId();

    Favorite getFavoriteById(Integer id);

    void updateById(UpdateFavoriteDto updateFavoriteDto);

    void deleteByIds(List<Integer> ids);
}
