package com.perfect.filesystem.myfs.util;

import org.apache.commons.io.FilenameUtils;
import org.springframework.web.multipart.MultipartFile;

import com.perfect.filesystem.myfs.util.file.MimeTypeUtils;

/**
 * 共通类
 *
 * @author zxh
 * @date 2019年 07月20日 00:14:10
 */
public class CommonUtil {
    /**
     * 获取文件名
     */
    public static final String getFilename(MultipartFile file)
    {
        String filename = file.getOriginalFilename();
        String extension = getExtension(file);
        filename = DateTimeUtil.datePath() + "/" + filename + "." + extension;
        return filename;
    }

    /**
     * 获取文件名的后缀
     *
     * @param file 表单文件
     * @return 后缀名
     */
    public static final String getExtension(MultipartFile file)
    {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (StringUtils.isEmpty(extension))
        {
            extension = MimeTypeUtils.getExtension(file.getContentType());
        }
        return extension;
    }

}
