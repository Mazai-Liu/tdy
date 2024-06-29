package com.example.tdy.controller;

import com.example.tdy.annotation.AccessLimit;
import com.example.tdy.context.LocalCache;
import com.example.tdy.entity.File;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.R;
import com.example.tdy.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;


/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */

@RestController
@RequestMapping("/file")
@Validated
public class FileController {
    Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileService fileService;

    @GetMapping("/getToken")
    public R<String> getToken() {
        String token = fileService.getFileToken();
        return R.ok(token);
    }


    /**
     * 七牛云返回的文件key，需要保存到资源表
     * @param fileKey
     * @return
     */
    @PostMapping("")
    public R<Integer> fileKey(String fileKey) {
        return R.ok(fileService.save(fileKey));
    }

    /**
     * 回源鉴权
     * @param uuid
     * @param response
     * @throws IOException
     */
    @PostMapping("/auth")
    public void auth(@RequestParam(required = false) String uuid, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.info("path: {}", request.getRequestURI());
        if (uuid == null || !LocalCache.containsKey(uuid)){
            logger.info("status: {}", 401);
            response.sendError(401);
        }else {
            logger.info("status: {}", 200);
            LocalCache.remove(uuid);
            response.sendError(200);
        }
    }

    /**
     * 获取系统资源首先要获取授权，用于回源鉴权
     * 返回资源url：https://xxxx/filekey?uuid=xxxxxxxxxxxx
     * @param fileId
     * @param response
     * @throws IOException
     */
    @GetMapping("/{fileId}")
    @AccessLimit(count = 20, time = 60)
    public void getFileUrl(@PathVariable @NotNull Integer fileId, HttpServletResponse response) throws Exception {
        File file = fileService.getFileById(fileId);

        response.setContentType(file.getType());
        response.sendRedirect(file.getFileKey());
        logger.info("获取资源：{}", file.getFileKey());
    }
}
