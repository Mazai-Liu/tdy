package com.example.tdy.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class R<T> {
    private Integer code;
    private String message;
    private T data;
    private Boolean state;


    public static <T> R<T> ok(T data) {
        return new R<T>(0, "操作成功", data, true);
    }

    public static R ok() {
        return new R<>(0, "操作成功", null, true);
    }
    public static R okWithMessage(String message){
        return new R<>(0, message, null, true);
    }
    public static R error(String msg) {
        return new R<>(201, msg, null, false);
    }
}
