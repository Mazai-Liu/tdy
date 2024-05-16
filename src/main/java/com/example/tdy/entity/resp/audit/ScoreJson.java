package com.example.tdy.entity.resp.audit;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/5/9
 */

@Data
public class ScoreJson{
    Double minPulp;
    Double maxPulp;

    Double minTerror;
    Double maxTerror;

    Double minPolitician;
    Double maxPolitician;

    Integer auditStatus;

}