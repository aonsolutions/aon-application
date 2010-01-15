package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FilenameFilter;

import com.code.aon.ui.cms.util.ControllerUtil;

public class DocumentGalleryController extends GalleryController {

	public File recoverFilesPath() {
		return ControllerUtil.getDocumentsPath();
	}

	public FilenameFilter getFilenameFilter() {
		return new DocumentsFileFilter();
	}

	private class DocumentsFileFilter implements FilenameFilter {

		public boolean accept(File f, String s) {
	        return true;
	    }
	}
	
	public String getPreviewCurrentUrl() {
		String url = ControllerUtil.getPreviewURL() + "/" + ControllerUtil.DOCUMENTS_PATH + getCurrentRelativePath();
		return url;
	}



}
