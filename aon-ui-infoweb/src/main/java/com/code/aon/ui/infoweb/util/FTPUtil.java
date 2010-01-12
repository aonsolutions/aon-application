package com.code.aon.ui.infoweb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.code.aon.ui.util.AonUtil;

public class FTPUtil {

	private static final Logger LOGGER = Logger.getLogger(FTPUtil.class.getName());
	
	private static final String PATH_SEPARATOR = "/";
	
	private static final String SERVER = "192.168.3.47";
	private static final String USER = "ftpcms";
	private static final String PASSWORD = "cms2001";
	
	public static void uploadFTP(File source, String destination) throws IOException {
		FTPClient ftp = new FTPClient();
		LOGGER.fine("Connecting to: " + SERVER );
		ftp.connect(SERVER);
		ftp.login(USER, PASSWORD);
		ftp.enterLocalPassiveMode();
		ftp.changeWorkingDirectory(destination);
		LOGGER.fine("Connected.");
		LOGGER.fine("Reply String: " + ftp.getReplyString());
		LOGGER.fine("System Name: " + ftp.getSystemName());
		LOGGER.fine("Working Directory: " + ftp.printWorkingDirectory());
		LOGGER.fine("File Type: " + ftp.setFileType(FTPClient.BINARY_FILE_TYPE));
		FTPFile files[] = ftp.listFiles();
		if (! ArrayUtils.isEmpty(files)) {
			if (! hasWritePermission(files[0]) ) {
				LOGGER.severe("Write permission denied for " + files[0]);
				AonUtil.addErrorMessage("FTP ERROR: Error intentando escribir en el servidor.");
			}
		}
		ftpDelete(ftp, destination);
		ftpDir(source, ftp, destination);

		ftp.logout();
		ftp.disconnect();
	}
	
	private static boolean hasWritePermission( FTPFile file ) {
		return file.hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ||
			file.hasPermission(FTPFile.GROUP_ACCESS, FTPFile.WRITE_PERMISSION) ||
			file.hasPermission(FTPFile.WORLD_ACCESS, FTPFile.WRITE_PERMISSION);
	}
	
	private static void deleteFile(FTPClient ftp, String pathname) {
		try {
			if (! ftp.deleteFile(pathname) ) {
				LOGGER.severe("File no deleted " + pathname);
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "Error deleting file " + pathname, th);
		}
	}

	private static void deleteDirectory(FTPClient ftp, String pathname) {
		try {
			if (! ftp.removeDirectory(pathname) ) {
				LOGGER.severe("Directory no deleted " + pathname);
			}
		} catch (Throwable th) {
			LOGGER.log(Level.SEVERE, "Error deleting directory " + pathname, th);
		}
	}
	
	private static void ftpDelete(FTPClient ftp, String destination) throws IOException {
		if ( ftp.changeWorkingDirectory(destination) ) {
			FTPFile files[] = ftp.listFiles(destination);
			if (! ArrayUtils.isEmpty(files)) {
				for( FTPFile file : files ) {
					if ( file != null ) {
						String name = destination + PATH_SEPARATOR + file.getName();
						if ( file.isFile() ) {
							deleteFile(ftp, name);
						} else if ( file.isDirectory() ) {
							ftpDelete(ftp, name);
							deleteDirectory(ftp, name);	
						}
					}
				}
			}
		} else {
			LOGGER.severe( "Error in change of working directory: " + destination );
		}
	}

	private static void ftpDir(File ftpDir, FTPClient fc, String breadCrum) throws IOException {
		String[] dirList = ftpDir.list();
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(ftpDir, dirList[i]);
			if (f.isDirectory()) {
				String directory = breadCrum + PATH_SEPARATOR + f.getName();
				LOGGER.fine("Creating directory: " + directory);
				if ( fc.makeDirectory(directory) ) {
					ftpDir(f, fc, directory);
				} else {
					AonUtil.addErrorMessage("FTP ERROR: No se ha podido crear el directorio " + directory);
					LOGGER.severe("Can not create directory " + directory);
				}
			} else {
				FileInputStream fis = new FileInputStream(f);
				String name = breadCrum + PATH_SEPARATOR + f.getName();
				LOGGER.fine("Creating file: " + name);
				if (!fc.storeFile(name, fis)) {
					AonUtil.addErrorMessage("FTP ERROR: No se ha podido escribir el fichero " + name);
					LOGGER.severe("Can not write " + name);
				}
				fis.close();
			}
		}
	}
	
}
