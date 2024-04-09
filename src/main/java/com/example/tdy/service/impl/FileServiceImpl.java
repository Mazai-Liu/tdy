package com.example.tdy.service.impl;

import com.example.tdy.constant.VideoConstant;
import com.example.tdy.context.BaseContext;
import com.example.tdy.context.LocalCache;
import com.example.tdy.entity.File;
import com.example.tdy.exception.BaseException;
import com.example.tdy.mapper.FileMapper;
import com.example.tdy.service.FileService;
import com.example.tdy.utils.QiniuUtil;
import com.qiniu.storage.model.FileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */

@Service
public class FileServiceImpl implements FileService {
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
    public Integer setDefaultCover(Integer url, Integer userId) {
        // TODO

        return VideoConstant.DEFAULT_COVER;
    }

    @Override
    public File getFileUrlById(Integer fileId) throws BaseException {
        File file = fileMapper.selectById(fileId);
        if(file == null) {
            throw new BaseException(VideoConstant.FILE_NOT_EXIST);
        }

        // 通过id生成url，可控制用于回源鉴权
        String fileKey = generateFileKey(file.getFileKey());
        file.setFileKey(fileKey);

        return file;
    }

    private String generateFileKey(String key) {

        return QiniuUtil.PROTOCOL + "://" + QiniuUtil.CNAME + "/" + key;
        // 回源鉴权
//        String uuid = UUID.randomUUID().toString();
//        LocalCache.set(uuid, true);
//        return QiniuUtil.PROTOCOL + "://" + QiniuUtil.CNAME + "/" + key + "?uuid=" + uuid;
    }
}