package com.code.aon.ui.infoweb.util;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RegistryAttachment;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;


public class ImageUtil {

	private static final Logger LOGGER = Logger.getLogger(ImageUtil.class.getName());
	
	public static boolean copyRegistryBlobToFile(RegistryAttachment ra, File path, String name) {
		return copyRegistryBlobToFile(ra, path, 0, 0, name); 
	}

	public static boolean copyRegistryBlobToFile(RegistryAttachment ra, File path, int maxW, int maxH, String name) {
		try {
			File file = null;
			if ( StringUtils.isEmpty(name) ) {
				file = new File( path, ra.getDescription() + "." + ra.getMimeType().getExtension() );
			} else {
				file = new File( path, name );
			}
			FileUtils.writeByteArrayToFile(file, ra.getData());
			if (maxW > 0 || maxH > 0) {
				resize(file, maxW, maxH);
			}
			return true;
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		return false;
	}

	public static void resize(File f, int maxDimW, int maxDimH) {
		try {
			String imgname = "tn_" + f.getName();
			
			Image inImage = new ImageIcon(f.getAbsolutePath()).getImage();

			double scale = (double) maxDimW / (double) inImage.getHeight(null);
			
			if (inImage.getWidth(null) > inImage.getHeight(null)) {
				scale = (double) maxDimW / (double) inImage.getWidth(null);
			}

			int scaledW = (int) (scale * inImage.getWidth(null));
			int scaledH = (int) (scale * inImage.getHeight(null));

			if (scaledH > maxDimH) {
				scale = (double) maxDimH / (double) inImage.getHeight(null);
				scaledW = (int) (scale * inImage.getWidth(null));
				scaledH = (int) (scale * inImage.getHeight(null));
			}
			
			Image img = inImage.getScaledInstance(scaledW, scaledH, Image.SCALE_SMOOTH);
			File outputFile = new File(f.getParentFile().getAbsolutePath() + "/" + imgname + "");
			outputFile.delete();
			JimiRasterImage raster = Jimi.createRasterImage(img.getSource());
			FileOutputStream fos = new FileOutputStream(outputFile);
			Jimi.putImage(MimeType.MIME_JPEG.getName(), raster, fos);
			fos.flush();
			fos.close();
		} catch (JimiException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}

}
