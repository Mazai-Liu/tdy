package com.example.tdy.result;

import lombok.Data;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Data
public class PageResult<T> {

    private List<T> records;
    private Integer total;
}
