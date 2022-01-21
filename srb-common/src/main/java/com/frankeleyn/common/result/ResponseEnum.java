package com.frankeleyn.common.result;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

/**
 * @author Frankeleyn
 * @date 2022/1/21 15:38
 */
@Getter
@AllArgsConstructor
@ToString
public enum ResponseEnum {

    SUCCESS(0, "成功"),
    ERROR(-1, "服务器内部错误");

    // 响应状态码
    private Integer code;
    // 响应信息
    private String message;
}
