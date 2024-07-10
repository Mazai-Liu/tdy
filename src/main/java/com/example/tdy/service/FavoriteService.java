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

    boolean judgeFavoriteVideoState(Integer fid, Integer vid);
    void addFavoriteVideo(Integer fid,Integer vid);

    void cancelFavoriteVideo(Integer fid, Integer vid);

    Favorite add(Integer id, String defaultFavoriteName, String defaultFavoriteDescription);
}
