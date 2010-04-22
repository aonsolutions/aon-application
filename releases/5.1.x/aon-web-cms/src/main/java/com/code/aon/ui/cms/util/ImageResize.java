package com.code.aon.ui.cms.util;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;

public class ImageResize {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageResize.class);

	public static File resize(File file, int width, int height) {
		if (file.isFile()){
			OutputStream os = null;
			File file2 = null;
			try {
				Image image = ImageUtil.getImage(file);
				file2 = new File( file.getParentFile(), "resize_" + file.getName() );
				
				try {
					Image newImage = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
					JimiRasterImage raster = Jimi.createRasterImage(newImage.getSource());
					os = new FileOutputStream(file2);
					Jimi.putImage(MimeType.MIME_JPEG.getName(), raster, os);
					os.flush();
				} catch (JimiException e) {
					LOGGER.error(e.getMessage(), e);
				} catch (IOException e) {
					LOGGER.error(e.getMessage(), e);
				}
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			} finally {
				IOUtils.closeQuietly(os);
			}
			return file2;
		}
		return null;
	}

	public static int getMaxWidth(File file){
		if (file.isFile()){
			try {
				Image image = ImageUtil.getImage(file);
				return  image.getWidth(null);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
		}
		return 0;
	}
	
	public static int getMaxHeight(File file){
		if (file.isFile()){
			try {
				Image image = ImageUtil.getImage(file);
				return  image.getHeight(null);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
		}
		return 0;
	}
	
	public static int getWidth(File file, int height){
		if (file.isFile()){
			try {
				Image image = ImageUtil.getImage(file);
				int image_width = image.getWidth(null);
				int image_height = image.getHeight(null);
				double scale = (double)image_height / (double)height;
				return (int)((double)image_width / scale);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
		}
		return 0;
	}

	public static int getHeight(File file, int width){
		if (file.isFile()){
			try {
				Image image = ImageUtil.getImage(file);
				int image_width = image.getWidth(null);
				int image_height = image.getHeight(null);
				double scale = (double)image_width / (double)width;
				return (int)((double)image_height / scale);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
		}
		return 0;
	}

}
