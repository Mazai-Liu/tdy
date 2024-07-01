package com.example.tdy.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PageResult<T> {

    private List<T> records;
    private Integer total;
}
