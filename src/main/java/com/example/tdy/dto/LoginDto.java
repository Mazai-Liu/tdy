package com.example.tdy.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author Mazai-Liu
 * @time 2024/3/22
 */
@Data
public class LoginDto {
    @NotBlank
    private String email;
    @NotBlank
    private String password;
}
