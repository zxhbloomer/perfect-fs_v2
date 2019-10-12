package com.perfect.filesystem.myfs.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

/**
 * @author zxh
 */
@EnableConfigurationProperties({PerfectFsProperties.class})
@ConfigurationProperties(prefix = "perfect.fs")
@Data
public class PerfectFsProperties {
    /**
     * 是否物理删除
     */
    private boolean realDelete;

    /**
     * 文件上传路径
     */
    private String uploadDataPath;

    /**
     * 简单日志
     */
    private boolean simpleLogModel;

}
