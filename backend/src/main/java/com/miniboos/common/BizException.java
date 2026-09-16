package com.miniboos.common;

import lombok.Getter;

/**
 * 业务异常：带HTTP状态码语义的code（400参数/401未登录/403越权/404不存在/409冲突）
 */
@Getter
public class BizException extends RuntimeException {

    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public static BizException badRequest(String msg) {
        return new BizException(400, msg);
    }

    public static BizException unauthorized(String msg) {
        return new BizException(401, msg);
    }

    public static BizException forbidden(String msg) {
        return new BizException(403, msg);
    }

    public static BizException notFound(String msg) {
        return new BizException(404, msg);
    }

    public static BizException conflict(String msg) {
        return new BizException(409, msg);
    }
}
