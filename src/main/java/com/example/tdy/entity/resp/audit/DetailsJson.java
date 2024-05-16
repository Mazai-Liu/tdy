package com.example.tdy.entity.resp.audit;

import lombok.Data;
import lombok.ToString;

/**
 * @author Mazai-Liu
 * @time 2024/5/9
 */
@Data
public class DetailsJson{
    Double score;
    String suggestion;
    String label;
    String group;
}