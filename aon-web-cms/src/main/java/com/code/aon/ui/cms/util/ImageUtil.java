package com.code.aon.ui.cms.util;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;

public class ImageUtil {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageUtil.class);

	private static String DEF_DIR = "thumb";

	private static String DEF_NAME = "thumb_";

	private Image imageFile;

	private Image resizedImageFile;

	public ImageUtil(Image imageFile, int scaledW, int scaledH) {
		this.imageFile = imageFile;
		resize(scaledW, scaledH);
	}

	public ImageUtil(Image imageFile, int maxDim) {
		this.imageFile = imageFile;
		double scale = (double) maxDim / (double) imageFile.getHeight(null);
		if (imageFile.getWidth(null) > imageFile.getHeight(null)) {
			scale = (double) maxDim / (double) imageFile.getWidth(null);
		}
		if (scale > 1)
			scale = 1;

		int scaledW = (int) (scale * imageFile.getWidth(null));
		int scaledH = (int) (scale * imageFile.getHeight(null));

		resize(scaledW, scaledH);
	}

	public Image getResizedImageFile() {
		return resizedImageFile;
	}

	private void resize(int scaledW, int scaledH) {
		this.resizedImageFile = imageFile.getScaledInstance(scaledW, scaledH,
				Image.SCALE_SMOOTH);
	}

	public void writeResizedImage(OutputStream os) {
		try {
			JimiRasterImage raster = Jimi.createRasterImage(resizedImageFile
					.getSource());
			Jimi.putImage(MimeType.MIME_JPEG.getName(), raster, os);
			os.flush();
		} catch (JimiException e) {
			LOGGER.error(e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}

	public static File resize(File file, int maxDim) {
		if (file.isFile()){
			File newFile = null;
			OutputStream os = null;
			try {
				Image image = getImage(file);
				
				File dir = new File( file.getParentFile(), DEF_DIR );
				if (!dir.exists()) {
					dir.mkdirs();
				}
				newFile = new File( dir, DEF_NAME + file.getName() );
				ImageUtil util = new ImageUtil(image, maxDim);
				os = new FileOutputStream(newFile);
				util.writeResizedImage(os);
			} catch (Throwable th) {
				LOGGER.error(th.getMessage(), th);
			} finally {
				IOUtils.closeQuietly(os);
			}
			return newFile;
		}
		return null;
	}

	public static void resize(File file, OutputStream os, int maxDim) {
		if (file.isFile()) {
			try {
				Image image = getImage(file);
				ImageUtil util = new ImageUtil(image, maxDim);
				util.writeResizedImage(os);
			} catch (Throwable th) {
				LOGGER.error( "Error resizing " + file + ". " + th.getMessage(), th);
			}
		}
	}
	
	public static Image getImage( File file ) {
		Image image = null;
		try {
			image = ImageIO.read(file);
		} catch ( Throwable th ) {
			LOGGER.error( "ImageIO error reading image " + file + ". " + th.getMessage(), th);
		}
		if ( image == null ) {
			image = new ImageIcon(file.getAbsolutePath()).getImage();
		}
		return image;
	}

}
