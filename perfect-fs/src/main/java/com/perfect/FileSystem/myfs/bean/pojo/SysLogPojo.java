package com.perfect.filesystem.myfs.bean.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统日志
 * @author zhangxh
 */
@Data
@Builder
@AllArgsConstructor
public class SysLogPojo implements Serializable {

    private static final long serialVersionUID = 3217907220556047829L;

    private String class_name;

    private String http_method;

    private String class_method;

    private String params;

    private Long execTime;

    private String remark;

    private LocalDateTime createDate;

    private String url;

    private String ip;

}
