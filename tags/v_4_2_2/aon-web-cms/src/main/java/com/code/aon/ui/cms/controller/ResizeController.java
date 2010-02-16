package com.code.aon.ui.cms.controller;

import java.io.File;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageResize;
import com.code.aon.ui.util.AonUtil;

public class ResizeController implements ICMSConstants {

	private Image resizeImage = null;

	private boolean ratio = true;
	private Integer width;
	private Integer maxWidth;
	private Integer maxHeight;
	private Integer height;
	
	public Image getResizeImage() {
		return resizeImage;
	}

	public void setResizeImage(Image resizeImage) {
		this.resizeImage = resizeImage;
	}

	public Integer getWidth() {
		return width;
	}

	public void setWidth(Integer width) {
		this.width = width;
	}

	public Integer getHeight() {
		return height;
	}

	public void setHeight(Integer height) {
		this.height = height;
	}
	
	
	public boolean isRatio() {
		return ratio;
	}

	public void setRatio(boolean ratio) {
		this.ratio = ratio;
	}

	public Integer getMaxWidth() {
		return maxWidth;
	}

	public void setMaxWidth(Integer maxWidth) {
		this.maxWidth = maxWidth;
	}

	public Integer getMaxHeight() {
		return maxHeight;
	}

	public void setMaxHeight(Integer maxHeight) {
		this.maxHeight = maxHeight;
	}

	private void onInit(){
		File file = ControllerUtil.getImagePath(resizeImage.getRelativePath());
		width = ImageResize.getMaxWidth(file);
		maxWidth = width;
		height = ImageResize.getMaxHeight(file);
		maxHeight = height;
		ratio = true;
	}
	
	public void onResizeFile(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		resizeImage = (Image)controller.getModel().getRowData();
		onInit();
	}

	public void onCancel(ActionEvent event) {
		resizeImage = null;
	}
	
	public void onAccept(ActionEvent event) {
		File file = ControllerUtil.getImagePath(resizeImage.getRelativePath());
		File newFile = ImageResize.resize(file, width, height);
		if ( file.delete() ) {
			newFile.renameTo(file);
		}
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		controller.chargeImageList();
		resizeImage = null;
	}

	public void onChangeWidth(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			File file = ControllerUtil.getImagePath(resizeImage.getRelativePath());
			height = ImageResize.getHeight(file, width);
		}
	}

	public void onChangeHeight(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			File file = ControllerUtil.getImagePath(resizeImage.getRelativePath());
			width = ImageResize.getWidth(file, height);
		}
	}
	
}
