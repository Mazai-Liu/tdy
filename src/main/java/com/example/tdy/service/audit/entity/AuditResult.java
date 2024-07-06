package com.example.tdy.service.audit.entity;

import com.example.tdy.enums.AuditStatus;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */
public class AuditResult {
    private String message;

    private AuditStatus status;

    private Integer offset;

    public AuditResult(String message, AuditStatus status) {
        this.message = message;
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public AuditStatus getStatus() {
        return status;
    }

    public void setStatus(AuditStatus status) {
        this.status = status;
    }
}
