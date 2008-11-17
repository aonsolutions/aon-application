package com.code.aon.ui.infoweb.util;

import java.awt.Image;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.ImageIcon;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.registry.RegistryAttachment;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;


public class ImageUtil {

	public static boolean copyRegistryBlobToFile(RegistryAttachment ra, String path, String name) {
		return copyRegistryBlobToFile(ra, path, 0, 0, name); 
	}

	public static boolean copyRegistryBlobToFile(RegistryAttachment ra, String path, int maxW, int maxH, String name) {
		try {

			String filename = "";
			if (name == null) filename = path + "/" + ra.getDescription() + "." + ra.getMimeType().getExtension();
			else filename = path + "/" + name;
			File f = new File(filename);
			FileOutputStream fos = new FileOutputStream(f);
			fos.write(ra.getData());
			fos.flush();
			fos.close();
			if (maxW > 0 || maxH > 0) resize(f, path, maxW, maxH);
			return true;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static void resize(File f, String path, int maxDimW, int maxDimH) {
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
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
