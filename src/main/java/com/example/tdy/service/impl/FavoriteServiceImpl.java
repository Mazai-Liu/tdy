package com.example.tdy.service.impl;

import com.example.tdy.constant.RedisConstant;
import com.example.tdy.constant.SystemConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.dto.UpdateFavoriteDto;
import com.example.tdy.entity.Favorite;
import com.example.tdy.entity.FavoriteVideo;
import com.example.tdy.mapper.FavoriteMapper;
import com.example.tdy.service.FavoriteService;
import com.example.tdy.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Service
public class FavoriteServiceImpl implements FavoriteService {

    @Autowired
    private FavoriteMapper favoriteMapper;

    @Autowired
    private InterestPushServiceImpl interestPushService;

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
        Integer id = updateFavoriteDto.getId();
        Integer currentIt = BaseContext.getCurrentId();
        if(id == null) {
            // 新增
            add(currentIt);
        } else {
            // 修改
            BeanUtils.copyProperties(updateFavoriteDto, favorite);
            favoriteMapper.update(favorite);
        }
    }

    @Override
    public void deleteByIds(List<Integer> ids) {
        favoriteMapper.deleteByIds(ids);
    }

    @Override
    public boolean judgeFavoriteVideoState(Integer fid, Integer vid) {
        FavoriteVideo result = favoriteMapper.getFavoriteVideoById(fid, vid);
        if(result==null){
            return false;
        }
        return true;
    }

    @Override
    public void addFavoriteVideo(Integer fid, Integer vid) {
        FavoriteVideo record = new FavoriteVideo();
        record.setVideoId(vid);
        record.setFavoriteId(fid);
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        favoriteMapper.insertFavoriteVideo(record);

        // 更新用户模型
        String key = RedisConstant.USER_MODEL + BaseContext.getCurrentId();
        interestPushService.updateModel(key, vid, SystemConstant.FAVORITE_PLUS_MODEL);
    }

    @Override
    public void cancelFavoriteVideo(Integer fid, Integer vid) {
        favoriteMapper.deleteFavoriteVideoById(fid,vid);
    }

    @Override
    public Favorite add(Integer userId, String defaultFavoriteName, String defaultFavoriteDescription) {
        Favorite favorite = new Favorite();
        favorite.setName(SystemConstant.DEFAULT_FAVORITE_NAME);
        favorite.setOpen(SystemConstant.FAVORITE_PUBLIC);
        favorite.setUserId(userId);
        favorite.setSize(0);
        favorite.setDescription(SystemConstant.DEFAULT_FAVORITE_DESCRIPTION);
        favoriteMapper.insert(favorite);

        return  favorite;
    }

    public Favorite add(Integer userId) {
        return add(userId, SystemConstant.DEFAULT_FAVORITE_NAME, SystemConstant.DEFAULT_FAVORITE_DESCRIPTION);
    }

}
