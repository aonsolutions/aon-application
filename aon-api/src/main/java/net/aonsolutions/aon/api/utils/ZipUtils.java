package net.aonsolutions.aon.api.utils;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class ZipUtils {

	private ZipUtils() {
	    throw new IllegalStateException("Utility class");
	}
	
	public static byte[] compress(File ...files) throws IOException {
	    byte[] result = null;

		try (ByteArrayOutputStream fos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(fos)){
			for (File file : files) {
				zos.putNextEntry(new ZipEntry(file.getName()));
				copy(zos, file);
				zos.closeEntry();
			}
			result = fos.toByteArray();
		} 
		
		return result; 
	}

	public static byte[] compress(byte[] ...list) throws IOException {
		byte[] result = null;
		try (ByteArrayOutputStream fos = new ByteArrayOutputStream(); ZipOutputStream zos = new ZipOutputStream(fos)){
			for (byte[] b : list) {
				zos.putNextEntry(new ZipEntry("file"));
				copy(zos, b);
				zos.closeEntry();
			}
			result = fos.toByteArray();
		} 
		
		return result; 
	}
	
	public static Map<String, byte[]> uncompress(byte[] bytes) throws IOException {
		HashMap<String, byte[]>map = new HashMap<>();
		try (ZipInputStream zipIn = new ZipInputStream(new ByteArrayInputStream(bytes))){
	         ZipEntry entry = zipIn.getNextEntry();
			  while (entry != null) {
				  try(ByteArrayOutputStream fos = new ByteArrayOutputStream()){
		              byte[] bytesIn = new byte[1024];
		              int read = 0;
		              while ((read = zipIn.read(bytesIn)) != -1) {
		            	  fos.write(bytesIn, 0, read);
		              }
		              map.put(entry.getName(), fos.toByteArray());
					  entry = zipIn.getNextEntry();
				  }
			  }
			  zipIn.closeEntry();
		}
		return map;
	}

	
	private static void copy(OutputStream out, File file) throws IOException {
	    try (InputStream in = new FileInputStream(file)){
	    	copy(out, in);
	    } 
	}
	
	private static void copy(OutputStream out, byte[] b) throws IOException {
	    try (InputStream in = new ByteArrayInputStream(b)){
	    	copy(out, in);
	    } 
	}

	private static void copy(OutputStream out, InputStream in) throws IOException {
		byte[] buffer = new byte[1024];
	    while (true) {
	    	int readCount = in.read(buffer);
	    	if (readCount < 0) {
	    		break;
	    	}
	    	out.write(buffer, 0, readCount);
	    }
	}
}
