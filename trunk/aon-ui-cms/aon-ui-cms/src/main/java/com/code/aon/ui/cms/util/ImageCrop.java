package com.code.aon.ui.cms.util;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.ImageIcon;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.enumeration.MimeType;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;

public class ImageCrop {

	private final static Logger LOGGER = LoggerFactory.getLogger(ImageCrop.class);

	public static String crop(String file,
			int x1,
			int y1,
			int x2,
			int y2,
			int width,
			int height) {
		File f = new File(file);
		if (f.isFile()){
			String name = null;
			String name2 = null;
			OutputStream os = null;
			try {
				name = f.getParentFile().getPath();
				name += File.separator + f.getName();
				name = name.replace('\\', '/');
				name2 = f.getParentFile().getPath();
				name2 += File.separator + "crop_" + f.getName();
				name2 = name2.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				
				try {
					JimiRasterImage raster = Jimi.createRasterImage(image.getSource());
		            JimiRasterImage raster2 = Jimi.createRasterImage(raster.getCroppedImageProducer(x1,y1,width, height));
					os = new FileOutputStream(name2);
					Jimi.putImage(MimeType.MIME_JPEG.getName(), raster2, os);
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
				f = null;
			}
			return name;
		}
		return null;
	}
	
}
