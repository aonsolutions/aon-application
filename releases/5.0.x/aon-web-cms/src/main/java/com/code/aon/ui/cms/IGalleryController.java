package com.code.aon.ui.cms;

import java.io.File;
import java.io.FilenameFilter;


public interface IGalleryController {

	public File recoverFilesPath();
	
	public FilenameFilter getFilenameFilter();
}
