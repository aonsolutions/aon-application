package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.cms.util.ImageCrop;
import com.code.aon.ui.util.AonUtil;

public class CropperController implements ICMSConstants {

	private String cropImage = null;
	
	private Integer x1;
	private Integer x2;
	private Integer y1;
	private Integer y2;
	private Integer width;
	private Integer height;
	
	public void setCropImage(String cropImage) {
		this.cropImage = cropImage;
	}

	public String getCropImage() {
		return cropImage;
	}

	public String getCropImagePreview() {
		return "/"+Constants.IMAGES_PATH+cropImage;
	}

	public Integer getX1() {
		return x1;
	}

	public void setX1(Integer x1) {
		this.x1 = x1;
	}
	
	public Integer getX2() {
		return x2;
	}

	public void setX2(Integer x2) {
		this.x2 = x2;
	}

	public Integer getY1() {
		return y1;
	}

	public void setY1(Integer y1) {
		this.y1 = y1;
	}

	public Integer getY2() {
		return y2;
	}

	public void setY2(Integer y2) {
		this.y2 = y2;
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

	private void onInit(){
		x1 = null;
		x2 = null;
		y1 = null;
		y2 = null;
		width = null;
		height = null;
	}
	
	public void onCropFile(ActionEvent event) throws ManagerBeanException {
		onInit();
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		cropImage = ((Image)controller.getModel().getRowData()).getRelativePath();
	}

	public void onCancel(ActionEvent event) {
		onInit();
		cropImage = null;
	}
	
	public void onAccept(ActionEvent event) {
		ImageCrop.crop(ControllerUtil.getImagesPath()+cropImage, x1, y1, x2, y2, width, height);
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		controller.chargeImageList();
		onInit();
		cropImage = null;
	}
	
}
