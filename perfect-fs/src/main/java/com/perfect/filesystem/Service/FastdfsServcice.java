package com.perfect.filesystem.Service;


import com.perfect.filesystem.File.FileListener;
import com.perfect.filesystem.File.UploadFileExt;
import com.perfect.filesystem.File.UploadResult;
import org.springframework.stereotype.Service;

@Service
public class FastdfsServcice implements FileListener {

	public void store(String filePath, String finalFilename) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UploadResult Store(UploadFileExt ufe) {
		//TODO 上传回调
		return null;
	}

	@Override
	public void Download(String fileKeyorName) {
		// TODO 下载
		
	}

}
