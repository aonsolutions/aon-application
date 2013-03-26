package com.code.aon.common.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

/**
 * The Class ZipUtil.
 */
public class ZipUtil {
	
	/**
	 * Uncompress zip data.
	 * 
	 * @param in the in
	 * @param destinationFolder the destination folder
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void uncompressZipData(InputStream in, File destinationFolder) throws IOException {
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(in));
		ZipEntry entry = zis.getNextEntry();
		while ( entry != null) {
			String filename = entry.getName();
			File newfile = new File(destinationFolder, filename);
			if (entry.isDirectory()) {
				newfile.mkdirs();
			} else {
				if (!newfile.getParentFile().exists()) {
					newfile.getParentFile().mkdirs();
				}
				OutputStream out = new BufferedOutputStream(new FileOutputStream(newfile));
				IOUtils.copy(zis, out);
				out.flush();
				IOUtils.closeQuietly(out);
			}
			entry = zis.getNextEntry();
		}
	}
	
	/**
	 * Gets the relative path.
	 * 
	 * @param path the path
	 * @param file the file
	 * @return the relative path
	 */
	public static String getRelativePath( File path, File file ) {
		String fullPath = FilenameUtils.normalizeNoEndSeparator(file.getAbsolutePath());
		String basePath = FilenameUtils.normalizeNoEndSeparator(path.getAbsolutePath());
		return StringUtils.substring(fullPath, basePath.length());
	}	

	/**
	 * Creates the zip.
	 * 
	 * @param zipFile the zip file
	 * @param directory the directory
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void createZip(File zipFile, File directory) throws IOException {
		OutputStream os = new BufferedOutputStream(new FileOutputStream(zipFile));
		ZipOutputStream out = new ZipOutputStream(os);
		addDirectory(out, zipFile, directory);
		IOUtils.closeQuietly(out);
	}
	
	private static void addDirectory( ZipOutputStream out, File zipFile, File directory ) throws IOException {
		for( File file : directory.listFiles() ) {
			if ( file.canRead() ) {
				if ( file.isDirectory() ) {
					addDirectory(out, zipFile, file);
				} else if ( file.isFile() && (!zipFile.equals(file)) ) {
					InputStream in = new BufferedInputStream(new FileInputStream(file));
					String name = getRelativePath(zipFile.getParentFile(), file);
					out.putNextEntry(new ZipEntry(name));
					IOUtils.copy(in, out);
					out.closeEntry();
					IOUtils.closeQuietly(in);
				}
			}
		}
	}	
	
}
