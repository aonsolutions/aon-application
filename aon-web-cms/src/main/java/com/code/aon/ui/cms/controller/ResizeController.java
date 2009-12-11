package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageResize;
import com.code.aon.ui.util.AonUtil;

public class ResizeController {

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
		width = ImageResize.getMaxWidth(ControllerUtil.getImagesPath()+resizeImage.getRelativePath());
		maxWidth = ImageResize.getMaxWidth(ControllerUtil.getImagesPath()+resizeImage.getRelativePath());
		height = ImageResize.getMaxHeight(ControllerUtil.getImagesPath()+resizeImage.getRelativePath());
		maxHeight = ImageResize.getMaxHeight(ControllerUtil.getImagesPath()+resizeImage.getRelativePath());
		ratio = true;
	}
	
	public void onResizeFile(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		resizeImage = (Image)controller.getModel().getRowData();
		onInit();
	}

	public void onCancel(ActionEvent event) {
		resizeImage = null;
	}
	
	public void onAccept(ActionEvent event) {
		ImageResize.resize(ControllerUtil.getImagesPath()+resizeImage.getRelativePath(), width, height);
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		controller.chargeImageList();
		resizeImage = null;
	}

	public void onChangeWidth(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			height = ImageResize.getHeight(ControllerUtil.getImagesPath()+resizeImage.getRelativePath(), width);
		}
	}

	public void onChangeHeight(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			width = ImageResize.getWidth(ControllerUtil.getImagesPath()+resizeImage.getRelativePath(), height);
		}
	}
	
}
