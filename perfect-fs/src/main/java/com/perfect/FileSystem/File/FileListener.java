package com.perfect.filesystem.File;

public interface FileListener {
	
	UploadResult Store(UploadFileExt ufe);
	void Download(String fileKeyorName);
	
}
