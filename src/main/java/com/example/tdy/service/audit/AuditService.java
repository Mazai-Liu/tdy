package com.example.tdy.service.audit;

/**
 * @author Mazai-Liu
 * @time 2024/5/7
 */
public interface AuditService<T, R> {
    R audit(T t);
}
