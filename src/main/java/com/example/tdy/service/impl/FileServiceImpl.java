package com.example.tdy.service.impl;

import com.example.tdy.constant.VideoConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.context.LocalCache;
import com.example.tdy.entity.File;
import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.FileMapper;
import com.example.tdy.result.PageResult;
import com.example.tdy.service.FileService;
import com.example.tdy.utils.QiniuUtil;
import com.github.pagehelper.Page;
import com.qiniu.storage.model.FileInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */

@Service
public class FileServiceImpl implements FileService {
    Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Autowired
    private QiniuUtil qiniuUtil;

    @Autowired
    private FileMapper fileMapper;

    @Override
    public String getFileToken() {

        return qiniuUtil.getFileToken();
    }

    @Override
    public Integer save(String fileKey) {
        // 保存当前用户上传的视频在资源表的映射中
        Integer currentId = BaseContext.getCurrentId();

        // 获取文件信息
        FileInfo fileInfo = qiniuUtil.getFileInfo(fileKey);

        File file = new File();
        String type = fileInfo.mimeType;
        file.setFileKey(fileKey);
        file.setFormat(type);
        file.setSize(fileInfo.fsize);
        file.setType(type.contains("video") ? "视频" : "图片");
        file.setUserId(currentId);

        fileMapper.insert(file);

        return file.getId();
    }

    @Override
    public String setDefaultCover(Integer url, Integer userId) {
        // TODO

        return VideoConstant.DEFAULT_COVER;
    }

    @Override
    public File getFileById(Integer fileId) throws BaseException {
        File file = fileMapper.selectById(fileId);
        if(file == null) {
            throw new BaseException(VideoConstant.FILE_NOT_EXIST);
        }

        // 通过id生成url。可能是Coze的资源
        // 七牛云需要控制用于回源鉴权

        String fileKey;
        // Coze
        if((fileKey = file.getFileKey()).startsWith("http")) {
            file.setFileKey(fileKey);
        } else {
            fileKey = generateFileKey(file.getFileKey());
            file.setFileKey(fileKey);
        }

        return file;
    }

    @Override
    public String getRealUrl(Integer fileId) {
        try {
            String realUrl = getFileById(fileId).getFileKey();
            logger.info("real url:{}", realUrl);

            return realUrl;
        } catch (BaseException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public PageResult<Video> getSearchVideo(String searchName ,Integer page,Integer limit) {
       PageResult<Video> pageResult = new PageResult<>();
       List<Video> videoList = fileMapper.getSearchVideo(searchName,(page-1)*limit,limit);
       pageResult.setRecords(videoList);
       pageResult.setTotal(videoList.size());
       return pageResult;
    }

    public String generateFileKey(String key) {
//        return QiniuUtil.PROTOCOL + "://" + QiniuUtil.CNAME + "/" + key;
        // 回源鉴权
        String uuid = UUID.randomUUID().toString();
        LocalCache.set(uuid, true);
        return QiniuUtil.PROTOCOL + "://" + QiniuUtil.CNAME + "/" + key + "?uuid=" + uuid;
    }

}
