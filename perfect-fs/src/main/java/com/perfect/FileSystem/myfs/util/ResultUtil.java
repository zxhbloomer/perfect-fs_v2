package com.perfect.filesystem.myfs.util;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.perfect.filesystem.myfs.bean.JSONResult;

/**
 * json返回值工具类
 */
public class ResultUtil {

    public static <T> JSONResult<T> success(T data, String message) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return JSONResult.<T>builder()
            .timestamp(DateTimeUtil.getTime())
            .http_status(HttpStatus.OK.value())
            .code(HttpStatus.OK.value())
            .message(message)
            .path(request.getRequestURL().toString())
            .method(request.getMethod())
            .success(true)
            .data(data)
            .build();
    }

    public static <T>JSONResult<T> success(T data) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        return JSONResult.<T>builder()
                .timestamp(DateTimeUtil.getTime())
                .http_status(HttpStatus.OK.value())
                .code(0)
                .message("调用成功")
                .path(request.getRequestURL().toString())
                .method(request.getMethod())
                .success(true)
                .data(data)
                .build();
    }

    public static <T>JSONResult<T> error(Integer status, Exception exception, String message, HttpServletRequest request) {

        return JSONResult.<T>builder()
                .timestamp(DateTimeUtil.getTime())
                .http_status(status)
                .code(-1)
                .message(message)
                .path(request.getRequestURL().toString())
                .method(request.getMethod())
                .success(false)
                .data((T) ExceptionUtil.getException(exception))
                .build();
    }
}
