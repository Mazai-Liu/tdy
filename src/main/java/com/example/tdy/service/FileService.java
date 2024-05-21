package com.example.tdy.service;

import com.example.tdy.entity.File;
import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;

/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */


public interface FileService {
    String getFileToken();

    Integer save(String fileKey);

    String setDefaultCover(Integer url, Integer userId);

    File getFileById(Integer fileId) throws BaseException;

    String getRealUrl(Integer fileId);
}
