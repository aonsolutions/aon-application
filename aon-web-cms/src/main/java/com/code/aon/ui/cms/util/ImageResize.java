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

public class ImageResize {

	private static final Logger LOGGER = Logger.getLogger(ImageResize.class
			.getName());

	public static String resize(String file,
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
				name2 += File.separator + "resize_" + f.getName();
				name2 = name2.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				
				try {
					image = image.getScaledInstance(width,height,Image.SCALE_SMOOTH);
					JimiRasterImage raster = Jimi.createRasterImage(image.getSource());
					os = new FileOutputStream(name2);
					Jimi.putImage(MimeType.MIME_JPEG.getName(), raster, os);
					os.flush();
				} catch (JimiException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				try{os.close();}catch (Exception e) {}
				f = null;
			}
			return name;
		}
		return null;
	}

	public static int getMaxWidth(String file){
		File f = new File(file);
		if (f.isFile()){
			String name = null;
			try {
				name = f.getParentFile().getPath();
				name += File.separator + f.getName();
				name = name.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				return  image.getWidth(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static int getMaxHeight(String file){
		File f = new File(file);
		if (f.isFile()){
			String name = null;
			try {
				name = f.getParentFile().getPath();
				name += File.separator + f.getName();
				name = name.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				return  image.getHeight(null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	public static int getWidth(String file, int height){
		File f = new File(file);
		if (f.isFile()){
			String name = null;
			try {
				name = f.getParentFile().getPath();
				name += File.separator + f.getName();
				name = name.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				int image_width = image.getWidth(null);
				int image_height = image.getHeight(null);
				double scale = (double)image_height / (double)height;
				return (int)((double)image_width / scale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

	public static int getHeight(String file, int width){
		File f = new File(file);
		if (f.isFile()){
			String name = null;
			try {
				name = f.getParentFile().getPath();
				name += File.separator + f.getName();
				name = name.replace('\\', '/');
				Image image = new ImageIcon(name).getImage();
				int image_width = image.getWidth(null);
				int image_height = image.getHeight(null);
				double scale = (double)image_width / (double)width;
				return (int)((double)image_height / scale);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return 0;
	}

}
