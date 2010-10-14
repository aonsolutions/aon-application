package com.code.aon.ui.webinfo.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.code.aon.ui.util.AonUtil;


public class ZipUtil {
	
	static final int BUFFER = 2048;

	static final int GEN_INFO = 1;

	static final int GEN_ERROR = 2;

	static final int GEN_WARN = 3;

	public static boolean uncompressZipFile(String source_zip_file, String destination_folder, String file_content) {
		File szf = new File(source_zip_file); 
		File df = new File(destination_folder);
		boolean found = false;
		if (!df.exists()) df.mkdirs();
		if (szf.exists()) {
			try {
				InputStream is = new FileInputStream(szf);
				BufferedOutputStream dest = null;
				ZipInputStream zis = new ZipInputStream(is);
				ZipEntry entry;
				while ((entry = zis.getNextEntry()) != null) {
					if (!entry.isDirectory()) {
						//addMessage(" - Extracting " + entry.getName() + ".", GEN_INFO);
						int count;
						byte data[] = new byte[BUFFER];
						File newfile = new File(df.getAbsolutePath() + "/" +  entry.getName());
						if (file_content != null && newfile.getName().equals(file_content)) found = true;
						if (!newfile.getParentFile().exists()) newfile.getParentFile().mkdirs();
						FileOutputStream fos = new FileOutputStream(newfile.getAbsolutePath());
						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = zis.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
					}
				}
				zis.close();
				//addMessage(" ", GEN_INFO);
				//addMessage("<STRONG> Plantilla '" + szf.getName() + "' instalada con exito. </STRONG>", GEN_INFO);
				if (file_content != null) {
					if (!found) addMessage("<STRONG> Archivo '" + file_content + "' no encontrado.</STRONG>", GEN_ERROR);
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				addMessage("<STRONG> Error al leer el fichero zip.</STRONG>", GEN_ERROR);
				return false;
			}
		}
		return false;
	}

	public void createZip(String path, String name) {
		boolean error = false;
		System.setProperty("platform.file.encoding", "ISO-8859-1");
		System.setProperty("file.encoding", "ISO-8859-1");
		try {
			if (path != null) {
				// create a ZipOutputStream to zip the data to
				File f = new File(path);
				FileOutputStream fos = new FileOutputStream(new File(f.getPath(), name + ".zip"));
				ZipOutputStream zos = new ZipOutputStream(fos);
				// assuming that there is a directory named inFolder (If there
				// isn't create one) in the same directory as the one the code
				// runs from,
				// call the zipDir method
				zipDir(path, zos, "", name + ".zip");
				// close the stream
				zos.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			error = true;
		} catch (IOException e) {
			e.printStackTrace();
			error = true;
		}
		if (error)
			addMessage("Se produjo un error al intentar generar el fichero '" + name + ".zip'", GEN_ERROR);
		//else
			//addMessage("El fichero '" + name + ".zip' se ha generado con exito.", GEN_INFO);
	}

	public void zipDir(String dir2zip, ZipOutputStream zos, String breadCrum, String original_name) throws IOException, FileNotFoundException {
		// create a new File object based on the directory we have to zip
		File zipDir = new File(dir2zip);
		// get a listing of the directory content
		String[] dirList = zipDir.list();
		byte[] readBuffer = new byte[2156];
		int bytesIn = 0;
		// loop through dirList, and zip the files
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(zipDir, dirList[i]);
			if (!f.getName().equals(original_name)) {
				if (f.isDirectory()) {
					// if the File object is a directory, call this
					// function again to add its content recursively
					String filePath = f.getPath();
					zipDir(filePath, zos, breadCrum + "/" + f.getName(), original_name);
					// loop again
					continue;
				}
				// if we reached here, the File object f was not a directory
				// create a FileInputStream on top of f
				FileInputStream fis = new FileInputStream(f);
				// create a new zip entry
				ZipEntry anEntry = new ZipEntry(breadCrum + "/" + f.getName());
				// place the zip entry in the ZipOutputStream object
				zos.putNextEntry(anEntry);
				// now write the content of the file to the ZipOutputStream
				while ((bytesIn = fis.read(readBuffer)) != -1) {
					zos.write(readBuffer, 0, bytesIn);
				}
				// close the Stream
				fis.close();
			}
		}
	}

	private static void addMessage(String msg, int type) {
		if (type == GEN_INFO) {
			AonUtil.addInfoMessage(msg);
		} else if (type == GEN_ERROR) {
			AonUtil.addErrorMessage(msg);
		} else if (type == GEN_WARN) {
			AonUtil.addWarningMessage(msg);
		} else {
			AonUtil.addFatalMessage(msg);
		}
	}


}
