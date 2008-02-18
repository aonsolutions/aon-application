package com.code.aon.ui.cms.util;

import java.io.File;


public class FileUtil {
	
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

}
