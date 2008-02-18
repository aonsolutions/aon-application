package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FilenameFilter;

import com.code.aon.ui.cms.util.ControllerUtil;

public class ImageGalleryController extends GalleryController {

	public String revoverFilesPath() {
		return ControllerUtil.getImagesPath();
	}

	public FilenameFilter getFilenameFilter() {
		return new ImageFileFilter();
	}

	private class ImageFileFilter implements FilenameFilter {

		protected String extensions = ".jpg|.jpeg|.gif|.png";

		public boolean accept(File f, String s) {
			boolean found = false;
			if (s.lastIndexOf(".") >= 0) {
				String ext = s.substring(s.lastIndexOf("."));
				ext = ext.toLowerCase();
				found = (extensions.indexOf(ext) >= 0);
			}
	        return found;
	    }
	}


}
