package com.heibuddy.xiaohuoban.http;

public interface ResponseCode {
    public static final int HTTP_OK = 200;// OK
    public static final int HTTP_BAD_REQUEST = 400;// Bad Request
    public static final int HTTP_UNAUTHORIZED = 401;// Not Authorized
    public static final int HTTP_FORBIDDEN = 403;// Forbidden
    public static final int HTTP_NOT_FOUND = 404;// Not Found
    public static final int HTTP_SERVER_ERROR = 500;// Internal Server

    public static final int ERROR_NORMAL = 0;
    public static final int ERROR_IO_EXCEPTION = -1;
    public static final int ERROR_NULL_TOKEN = -2;
    public static final int ERROR_AUTH_FAILED = -3;
    public static final int ERROR_AUTH_EMPTY = -4;
    public static final int ERROR_JSON_EXCEPTION = -5;
    public static final int ERROR_DUPLICATE = -6;

    // 200 OK: 成功
    // 202 Accepted: 发�?消息时未提供source的请求会暂时放到队列中，并返回状态码202
    // 400 Bad Request�?无效的请求，返回值中可以看到错误的详细信�?    // 401 Unauthorized�?用户�?��登录或�?认证失败
    // 403 Forbidden�?用户无访问权限，例如访问了设置隐私的用户、消息等
    // 404 Not Found�?请求的资源已经不存在，例如访问了不存在的用户、消息等

}
