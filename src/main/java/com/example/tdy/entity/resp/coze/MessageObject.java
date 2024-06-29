package com.example.tdy.entity.resp.coze;

import lombok.Data;

/**
 * @author Mazai-Liu
 * @time 2024/6/29
 */
@Data
public class MessageObject {
    /**
     * 发送这条消息的实体。取值：
     * user：代表该条消息内容是用户发送的。
     * assistant：代表该条消息内容是 Bot 发送的。
     */
    private String role;
    /**
     * 当 role=assistant 时，用于标识 Bot 的消息类型，取值：
     * answer：Bot 最终返回给用户的消息内容。
     * function_call：Bot 对话过程中调用函数 (function call) 的中间结果。
     * tool_response：调用工具 (function call) 后返回的结果。
     * follow_up：如果 Bot 配置打开了用户问题建议开关，则返回配置的推荐问题。
     */
    private String type;
    // 消息内容。
    private String content;
    // text。type = answer 时，消息内容格式为 Markdown
    private String content_type;
}
