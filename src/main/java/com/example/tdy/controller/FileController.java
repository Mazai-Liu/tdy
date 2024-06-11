package com.example.tdy.controller;

import com.example.tdy.context.LocalCache;
import com.example.tdy.entity.File;
import com.example.tdy.exception.BaseException;
import com.example.tdy.result.R;
import com.example.tdy.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mazai-Liu
 * @time 2024/3/27
 */

@RestController
@RequestMapping("/file")
public class FileController {

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
        System.out.println(request.getRequestURI());
        if (uuid == null || LocalCache.containsKey(uuid)){
            response.sendError(401);
        }else {
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
    public void getFileUrl(@PathVariable Integer fileId, HttpServletResponse response) throws Exception {
        File file = fileService.getFileById(fileId);

        response.setContentType(file.getType());
        response.sendRedirect(file.getFileKey());
    }
}
