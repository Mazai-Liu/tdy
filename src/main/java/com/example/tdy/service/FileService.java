package com.example.tdy.service;

import com.example.tdy.entity.File;
import com.example.tdy.entity.Video;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.PageResult;

import java.util.List;

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
