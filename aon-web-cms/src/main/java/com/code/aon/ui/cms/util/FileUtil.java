package com.code.aon.ui.cms.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;


public class FileUtil {
	
	private static final Logger LOGGER = Logger.getLogger(FileUtil.class.getName());
	
	public static void delete(String file) {
		File f = new File(file);
		if (f.exists() && f.canWrite()) {
			if (f.isDirectory()) {
				deleteDir(f);
				f.delete();
			}
			else f.delete();
		}
	}

	private static void deleteDir(File dir) {
		for (File f : dir.listFiles()) {
			if (f.exists() && f.canWrite()) {
				if (f.isDirectory()) {
					deleteDir(f);
					f.delete();
				}
				else f.delete();
			}
		}
	}

	public static void copyDir(String sourcePath, String destinationPath) {
		File sf = new File(sourcePath);
		File df = new File(destinationPath + "/" + sf.getName()); 
		if (sf.exists() && sf.canRead()) {
			if (sf.isDirectory()) {
				copyDir(sf, df);
			}
			else copyFile(sf, df);
		}
	}

	private static void copyDir(File sf, File df) {
		//Creamos el directorio destino
		if (!df.exists()) df.mkdirs();
		for (File f : sf.listFiles()) {
			if (f.exists() && f.canRead()) {
				File f2 = new File(df.getAbsolutePath() + "/" + f.getName());
				if (f.isDirectory()) {
					copyDir(f, f2);
				}
				else copyFile(f, f2);
			}
		}
	}

	private static void copyFile(File sf, File df) {
		InputStream is = null;
		BufferedInputStream bis = null;
        OutputStream os = null;
		BufferedOutputStream bos = null;
		try {
			is = new FileInputStream(sf);
			bis = new BufferedInputStream(is);
	        os = new FileOutputStream(df);
			bos = new BufferedOutputStream(os);
			byte[] input = new byte[1024];
			boolean eof = false;
			while (!eof) {
				int length = bis.read(input);
				if (length == -1) {
					eof = true;
				}
				else {
					bos.write(input, 0, length);
				}
			}
			bos.flush();
			bis.close();
		} catch (FileNotFoundException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}finally{
			IOUtils.closeQuietly(bis);
			IOUtils.closeQuietly(is);
			IOUtils.closeQuietly(bos);
			IOUtils.closeQuietly(os);
			is = null;
			bis = null;
	        os = null;
			bos = null;
		}
	}


}
