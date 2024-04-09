package com.example.tdy.result;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */
@Data
public class BasePage {
    private Integer limit = 10;
    private Integer page = 1;

}
