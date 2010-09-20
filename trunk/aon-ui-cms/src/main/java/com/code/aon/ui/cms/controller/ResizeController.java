package com.code.aon.ui.cms.controller;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.faces.event.ActionEvent;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.common.util.MimeResolver;
import com.code.aon.ui.cms.util.ControllerUtil;
import com.code.aon.ui.publisher.util.ImageUtilEx;
import com.code.aon.ui.util.AonUtil;

public class ResizeController implements ICMSConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ResizeController.class);

	private Image resizeImage = null;

	private boolean ratio = true;
	private Integer width;
	private Integer height;
	private File file;
	private BufferedImage image;
	private MimeType type;
	
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
		return image.getWidth();
	}

	public Integer getMaxHeight() {
		return image.getHeight();
	}

	private void onInit(){
		this.file = ControllerUtil.getImagePath(resizeImage.getRelativePath());
		try {
			byte[] data = FileUtils.readFileToByteArray(file);
			this.type = MimeResolver.getMimeType(data);
			this.image = ImageUtilEx.getBufferedImage(data, type);
			width = image.getWidth();
			height = image.getHeight();
		} catch (IOException e) {
			LOGGER.error("Error loading image " + file, e);
		}
		ratio = true;
	}
	
	public void onResizeFile(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		resizeImage = (Image)controller.getModel().getRowData();
		onInit();
	}

	private void reset() {
		this.resizeImage = null;
		this.file = null;
		this.image = null;
		this.type = null;
	}
	
	public void onCancel(ActionEvent event) {
		reset();
	}
	
	public void onAccept(ActionEvent event) {
		if ( (image.getWidth() != width) || (image.getHeight() != height) ) {
			BufferedImage newImage = ImageUtil.scale(image, width, height);
			File newFile = new File( file.getParentFile(), "resize_" + file.getName() );
			ImageUtilEx.writeBufferedImage(newImage, type, newFile);
			if ( file.delete() ) {
				newFile.renameTo(file);
			}
			GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
			controller.chargeImageList();
			reset();
		}
	}

	public void onChangeWidth(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			height = ImageUtil.getProportionalHeight(image, width);
		}
	}

	public void onChangeHeight(ActionEvent event) throws ManagerBeanException {
		if (ratio){
			width = ImageUtil.getProportionalWidth(image, height);
		}
	}
	
}
