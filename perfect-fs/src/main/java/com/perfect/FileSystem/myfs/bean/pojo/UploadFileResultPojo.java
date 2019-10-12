package com.perfect.filesystem.myfs.bean.pojo;

import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * @author zhangxh
 */
@Slf4j
@ToString
@Data
public class UploadFileResultPojo
{
    /**
     * 文件ID
     */
    private String fileUuid;
    /**
     * 文件名
     */
    private String fileName;
    /**
     * 文件大小(B)
     */
    private Long file_size;
    /**
     * uri Disk
     */
    private String uriDisk;

    /**
     * 除了disk，其他的类型
     * mongodb,qiniu,fastdfs,alioss
     */
    private String fsType;

    /**
     * url fs
     */
    private String uriFs;

    /**
     * 除了disk，其他的类型
     * mongodb,qiniu,fastdfs,alioss所对应的url
     */
    private String fsType2Url;

//    public String toString()
//    {
//        StringBuffer result = new StringBuffer().append(getClass().getName()).append(": fileUuid = '").append(this.fileUuid).append("'").append(", fileName = '").append(this.fileName).append("'").append(", fileSize = ").append(this.fileSize).append("'");
//
//        return result.toString();
//    }
}
