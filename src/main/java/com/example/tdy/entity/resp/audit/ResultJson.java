package com.example.tdy.entity.resp.audit;

import lombok.Data;
import lombok.ToString;

import java.io.Serializable;

/**
 * @author Mazai-Liu
 * @time 2024/5/9
 */
@Data
@ToString
public class ResultJson implements Serializable {
    Integer code;
    String message;
    QiniuAuditResult result;
}
