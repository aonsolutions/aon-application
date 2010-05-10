package com.code.aon.ui.cms.controller;

import java.io.File;
import java.io.FilenameFilter;

import com.code.aon.ui.cms.util.ControllerUtil;

public class ImageGalleryController extends GalleryController {
	
	private boolean thumbnail;
	
	private String reRender;
	
	private Object listenerBean;
	
	private boolean showWindow;

	public File recoverFilesPath() {
		return ControllerUtil.getImagesPath();
	}

	public FilenameFilter getFilenameFilter() {
		return new ImageFileFilter();
	}
	
	public boolean isShowWindow() {
		return showWindow;
	}

	public void setShowWindow(boolean showWindow) {
		this.showWindow = showWindow;
	}

	public boolean isThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(boolean thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getReRender() {
		return reRender;
	}

	public void setReRender(String reRender) {
		this.reRender = reRender;
	}

	public Object getListenerBean() {
		return listenerBean;
	}

	public void setListenerBean(Object listenerBean) {
		this.listenerBean = listenerBean;
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
