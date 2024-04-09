package com.example.tdy.enums;

/**
 * @author Mazai-Liu
 * @time 2024/4/2
 */
public enum AuditStatus {
    ING(0,"审核中"),
    PASS(1,"审核通过"),
    FAIL(2,"审核不通过"),
    MANUAL(3,"待人工审核");


    private Integer code;
    private String msg;

    AuditStatus(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }
}
