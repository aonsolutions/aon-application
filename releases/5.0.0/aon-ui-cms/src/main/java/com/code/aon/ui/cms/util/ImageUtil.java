package com.code.aon.ui.cms.util;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.logging.Logger;

import javax.swing.ImageIcon;

import com.code.aon.common.enumeration.MimeType;
import com.sun.jimi.core.Jimi;
import com.sun.jimi.core.JimiException;
import com.sun.jimi.core.raster.JimiRasterImage;


public class ImageUtil {

	private static final Logger LOGGER = Logger.getLogger(ImageUtil.class.getName());

	public static int DEF_MAX_SIZE = 100;
	
	private static String DEF_NAME = "thumb_";
	
	private Image imageFile;

	private Image resizedImageFile;

	public ImageUtil(Image imageFile,int scaledW,int scaledH){
		this.imageFile = imageFile;
		resize(scaledW, scaledH);
	}
	
	public ImageUtil(Image imageFile,int maxDim){
		this.imageFile = imageFile;
		double scale = (double) maxDim / (double) imageFile.getHeight(null);
		if (imageFile.getWidth(null) > imageFile.getHeight(null)) {
			scale = (double) maxDim / (double) imageFile.getWidth(null);
		}
		if (scale > 1) scale = 1;

		int scaledW = (int) (scale * imageFile.getWidth(null));
		int scaledH = (int) (scale * imageFile.getHeight(null));
			
		resize(scaledW, scaledH);
	}
	
	public Image getResizedImageFile() {
		return resizedImageFile;
	}

	private void resize(int scaledW,int scaledH){
		this.resizedImageFile = imageFile.getScaledInstance(scaledW , scaledH, Image.SCALE_SMOOTH);
	}
	
	public void writeResizedImage(OutputStream os){
		try{
			JimiRasterImage raster = Jimi.createRasterImage(resizedImageFile.getSource());
			Jimi.putImage(MimeType.MIME_JPEG.getName(), raster, os);
			os.flush();
		}catch (JimiException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static String resize(String file, int maxDim) {
		String name = null;
		try{
			Image image = new ImageIcon(file).getImage();
			ImageUtil util = new ImageUtil(image,maxDim);
			File f = new File(file);
			name = f.getParentFile().getPath();
			(new File(name)).mkdirs();
			name += File.separator + DEF_NAME+f.getName();
			OutputStream os = new FileOutputStream(name);
			util.writeResizedImage(os);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return name;
	}
	
	
	public static void resize(String file, OutputStream os, int maxDim) {
		Image image = new ImageIcon(file).getImage();
		ImageUtil util = new ImageUtil(image,maxDim);
		util.writeResizedImage(os);
	}
	
	public static void resize(String file, OutputStream os, int scaledW, int scaledH) {
		Image image = new ImageIcon(file).getImage();
		ImageUtil util = new ImageUtil(image,scaledW,scaledH);
		util.writeResizedImage(os);
	}
	
	public static void main(String[] args){
		System.out.println(ImageUtil.resize("c:/tmp/05.jpg", 18));
	}
	
}
