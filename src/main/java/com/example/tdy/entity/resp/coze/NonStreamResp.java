package com.example.tdy.entity.resp.coze;

import lombok.Data;

import java.util.List;

/**
 * @author Mazai-Liu
 * @time 2024/6/29
 */
@Data
public class NonStreamResp {
    // 会话 ID。
    private String conversation_id;
    // 全部消息都处理完成后，以 JSON 数组形式返回。
    private List<MessageObject> messages;
    // 状态码。0表示成功
    private Integer code;
    // 状态信息
    private String msg;
}
