package com.example.tdy.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */
@Data
public class RegisterDto {
    @Email
    private String email;
    @NotBlank
    private String password;
    @NotBlank
    private String code;
    @NotBlank
    private String uuid;
    @NotBlank
    private String nickName;
}
