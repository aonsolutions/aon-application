package com.code.aon.ui.publisher.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;

/**
 * The Class FTPUtil.
 */
public class FTPUtil {

	/** The Constant FTP_SERVER. */
	public static final String FTP_SERVER = "ftp.server";
	
	/** The Constant FTP_USER. */
	public static final String FTP_USER = "ftp.user";
	
	/** The Constant FTP_PASSWORD. */
	public static final String FTP_PASSWORD = "ftp.password";
	
	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FTPUtil.class.getName());
		
	/** The Constant PATH_SEPARATOR. */
	private static final String PATH_SEPARATOR = "/";
	
	public static Properties getProperties( File file, String resource ) {
		Properties properties = new Properties();
		try {
			if ( file.exists() && file.canRead() ) {
				properties.load( new FileInputStream(file) );
			} else {
				properties.load(FTPUtil.class.getResourceAsStream(resource));
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage(), e );
		}
		return properties;
	}
	
	/**
	 * Gets the fTP client.
	 * 
	 * @param destination the destination
	 * @param properties the properties
	 * @return the fTP client
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static FTPClient getFTPClient( String destination, Properties properties ) throws IOException {
		FTPClient ftp = new FTPClient();
		String server = properties.getProperty(FTP_SERVER);
		LOGGER.debug("Connecting to: {}", server );
		ftp.connect(server);
		String user = properties.getProperty(FTP_USER);
		String password = properties.getProperty(FTP_PASSWORD);
		ftp.login(user, password);
		ftp.enterLocalPassiveMode();
		ftp.changeWorkingDirectory(destination);
		ftp.setListHiddenFiles(true);
		return ftp;
	}
	
	/**
	 * Upload ftp.
	 * 
	 * @param source the source
	 * @param destination the destination
	 * @param properties the properties
	 * @throws AonException the aon exception
	 */
	public static void uploadFTP(File source, String destination, Properties properties) throws AonException {
		FTPClient ftp = null;
		try {
			ftp = getFTPClient( destination, properties );
			LOGGER.debug("Connected.");
			LOGGER.debug("Reply String: {}", ftp.getReplyString());
			LOGGER.debug("System Name: {}", ftp.getSystemName());
			LOGGER.debug("Working Directory: {}", ftp.printWorkingDirectory());
			LOGGER.debug("File Type: {}", ftp.setFileType(FTPClient.BINARY_FILE_TYPE));

			ftpDelete(ftp, destination);
			ftpDir(source, ftp, destination);
		} catch (IOException e) {
			LOGGER.error("Error uploading to " + destination, e);
			throw new AonException( e.getMessage(), e);
		} finally {
			if ( ftp != null ) {
				try {
					ftp.logout();
					if ( ftp.isConnected() ) {
						ftp.disconnect();
					}
				} catch (IOException e) {
					LOGGER.error("Error closing ftp connection", e);
					throw new AonException( e.getMessage(), e);
				}
			}
		}
	}
	
	/**
	 * Delete file.
	 * 
	 * @param ftp the ftp
	 * @param pathname the pathname
	 */
	private static void deleteFile(FTPClient ftp, String pathname) {
		try {
			if (! ftp.deleteFile(pathname) ) {
				LOGGER.error("File no deleted {}", pathname);
			}
		} catch (Throwable th) {
			LOGGER.error("Error deleting file {}", pathname, th);
		}
	}

	/**
	 * Delete directory.
	 * 
	 * @param ftp the ftp
	 * @param pathname the pathname
	 */
	private static void deleteDirectory(FTPClient ftp, String pathname) {
		try {
			if (! ftp.removeDirectory(pathname) ) {
				LOGGER.error("Directory no deleted {}", pathname);
			}
		} catch (Throwable th) {
			LOGGER.error("Error deleting directory {}", pathname, th);
		}
	}
	
	/**
	 * Ftp delete.
	 * 
	 * @param ftp the ftp
	 * @param destination the destination
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void ftpDelete(FTPClient ftp, String destination) throws IOException {
		if ( ftp.changeWorkingDirectory(destination) ) {
			FTPFile files[] = ftp.listFiles(destination);
			if (! ArrayUtils.isEmpty(files)) {
				for( FTPFile file : files ) {
					if ( file != null ) {
						String name = file.getName();
						if (! (StringUtils.equals(name, ".") || StringUtils.equals(name, "..")) ) {
							name = destination + PATH_SEPARATOR + name;
							if ( file.isFile() ) {
								deleteFile(ftp, name);
							} else if ( file.isDirectory() ) {
								ftpDelete(ftp, name);
								deleteDirectory(ftp, name);	
							}							
						}
					}
				}
			}
		} else {
			LOGGER.error( "Error in change of working directory: {}", destination );
		}
	}

	/**
	 * Ftp dir.
	 * 
	 * @param ftpDir the ftp dir
	 * @param fc the fc
	 * @param breadCrum the bread crum
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private static void ftpDir(File ftpDir, FTPClient fc, String breadCrum) throws IOException {
		String[] dirList = ftpDir.list();
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(ftpDir, dirList[i]);
			if (f.isDirectory()) {
				String directory = breadCrum + PATH_SEPARATOR + f.getName();
				LOGGER.debug("Creating directory: {}", directory);
				if ( fc.makeDirectory(directory) ) {
					ftpDir(f, fc, directory);
				} else {
					// AonUtil.addErrorMessage("FTP ERROR: No se ha podido crear el directorio " + directory);
					LOGGER.error("Can not create directory {}", directory);
				}
			} else {
				FileInputStream fis = new FileInputStream(f);
				String name = breadCrum + PATH_SEPARATOR + f.getName();
				LOGGER.debug("Creating file: {}", name);
				if (!fc.storeFile(name, fis)) {
					// AonUtil.addErrorMessage("FTP ERROR: No se ha podido escribir el fichero " + name);
					LOGGER.error("Can not write {}", name);
				}
				fis.close();
			}
		}
	}
	
}
