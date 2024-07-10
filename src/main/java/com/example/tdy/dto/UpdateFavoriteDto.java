package com.example.tdy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author Mazai-Liu
 * @time 2024/3/26
 */
@Data
public class UpdateFavoriteDto {

    private Integer id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;

}
