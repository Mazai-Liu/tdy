package com.example.tdy.dto;

import com.sun.istack.internal.NotNull;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

/**
 * @author Mazai-Liu
 * @time 2024/3/19
 */

@Data
public class EmailDto {

    @NotBlank
    private String uuid;

    @NotBlank
    private String code;

    @NotBlank
    @Email
    private String email;
}
