package com.code.aon.ui.cms.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.ImageUtil;
import com.code.aon.common.util.MimeResolver;

public class ImageUtil2 {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageUtil2.class);

	private static String DEF_DIR = "thumb";

	private static String DEF_NAME = "thumb_";

	private BufferedImage imageFile;
	
	private MimeType type;

	private BufferedImage resizedImageFile;

	public ImageUtil2(File file, int maxDim) throws IOException {
		byte[] data = FileUtils.readFileToByteArray(file);
		this.type = MimeResolver.getMimeTypeByExtension(file.getName());
		if ( this.type == null ) {
			this.type = MimeResolver.getMimeType(data);
		}
		this.imageFile = ImageUtil.getBufferedImage(data);
		double scale = (double) maxDim / (double) imageFile.getHeight();
		if (imageFile.getWidth() > imageFile.getHeight()) {
			scale = (double) maxDim / (double) imageFile.getWidth();
		}
		if (scale > 1)
			scale = 1;

		int scaledW = (int) (scale * imageFile.getWidth());
		int scaledH = (int) (scale * imageFile.getHeight());

		resize(scaledW, scaledH);
	}

	private void resize(int scaledW, int scaledH) {
		this.resizedImageFile = ImageUtil.scale(imageFile, scaledW, scaledH);
	}

	public void writeResizedImage(OutputStream out) {
		ImageUtil.writeBufferedImage(resizedImageFile, out, type);
	}
	
	public void writeResizedImage(File file) {
		ImageUtil.writeBufferedImage(resizedImageFile, file);
	}	

	public static File resize(File file, int maxDim) {
		if (file.isFile()){
			File newFile = null;
			try {
				File dir = new File( file.getParentFile(), DEF_DIR );
				if (!dir.exists()) {
					dir.mkdirs();
				}
				newFile = new File( dir, DEF_NAME + file.getName() );
				ImageUtil2 util = new ImageUtil2(file, maxDim);
				util.writeResizedImage(newFile);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			}
			return newFile;
		}
		return null;
	}

	public static void resize(File file, OutputStream os, int maxDim) {
		if (file.isFile()) {
			try {
				ImageUtil2 util = new ImageUtil2(file, maxDim);
				util.writeResizedImage(os);
			} catch (Throwable th) {
				LOGGER.error( "Error resizing " + file + ". " + th.getMessage(), th);
			}
		}
	}

}
