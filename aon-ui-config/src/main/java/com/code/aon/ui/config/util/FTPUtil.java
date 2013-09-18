package com.code.aon.ui.config.util;

import static com.code.aon.ui.common.ICommonMessages.CONFIG_BUNDLE;
import static com.code.aon.ui.common.ICommonMessages.FTP_CONNECTED;
import static com.code.aon.ui.common.ICommonMessages.FTP_CONNECTING;
import static com.code.aon.ui.common.ICommonMessages.FTP_DISCONNECTED;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_CHANGE_DIRECTORY;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_CONNECTION;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_CREATE_DIRECTORY;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_CREATE_FILE;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_DELETE_DIRECTORY;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_DELETE_FILE;
import static com.code.aon.ui.common.ICommonMessages.FTP_ERROR_LOGIN;
import static com.code.aon.ui.common.ICommonMessages.FTP_LOGIN;
import static com.code.aon.ui.common.ICommonMessages.FTP_LOGOUT;
import static com.code.aon.ui.common.ICommonMessages.FTP_UPLOAD_FILE;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.AonException;
import com.code.aon.common.ILogger;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class FTPUtil.
 */
public class FTPUtil {

	/** The Constant LOGGER. */
	private static final Logger LOGGER = LoggerFactory.getLogger(FTPUtil.class.getName());
			
	/** The Constant FTP_SERVER. */
	public static final String FTP_SERVER = "ftp.server";
	
	/** The Constant FTP_USER. */
	public static final String FTP_USER = "ftp.user";
	
	/** The Constant FTP_PASSWORD. */
	public static final String FTP_PASSWORD = "ftp.password";
	
	/** The Constant PATH_SEPARATOR. */
	private static final String PATH_SEPARATOR = "/";
	
	private FTPClient ftp;
	
	private ILogger logger;
	
	public FTPUtil(ILogger logger) {
		this.logger = logger;
	}

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
	
	private boolean isReplyOk() {
		int code = ftp.getReplyCode();
		if ( (code >= 200) && (code <300) ) {
			return true;
		}
		LOGGER.error( ftp.getReplyString() );
		return false;
	}
	
	private boolean connect(String server) {
		this.ftp = new FTPClient();
		LOGGER.debug("Connecting to: {}", server );
		logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_CONNECTING) );
		try {
			ftp.connect(server);
			if ( isReplyOk() ) {
				return true;
			}
		} catch (Throwable e) {
			LOGGER.error("Error connecting " + server, e);
		}		
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_CONNECTION) );
		return false;
	}

	private void login( String user, String password ) {
		try {
			logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_LOGIN) );
			LOGGER.debug("Login user: {}", user );
			ftp.login(user, password);
			if ( isReplyOk() ) {
				ftp.enterLocalPassiveMode();
				ftp.setListHiddenFiles(true);
				LOGGER.debug("Reply String: {}", ftp.getReplyString());
				LOGGER.debug("System Name: {}", ftp.getSystemName());
				LOGGER.debug("Working Directory: {}", ftp.printWorkingDirectory());
				LOGGER.debug("File Type: {}", ftp.setFileType(FTPClient.BINARY_FILE_TYPE));
				logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_CONNECTED) );
				return;
			}
		} catch (Throwable e) {
			LOGGER.error("Error login user " + user, e);
		}		
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_LOGIN) );
		close();
	}
	
	/**
	 * Upload ftp.
	 * 
	 * @param properties the properties
	 * @throws IOException 
	 * @throws AonException the aon exception
	 */
	public void connect(Properties properties) throws IOException {
		String server = properties.getProperty(FTP_SERVER);
		if ( connect(server) ) {
			String user = properties.getProperty(FTP_USER);
			String password = properties.getProperty(FTP_PASSWORD);
			login(user, password);
		}
	}	
	
	public boolean isConnected() {
		return ( this.ftp != null ) && ( ftp.isConnected() );
	}
	
	public void close() {
		if ( ftp != null ) {
			try {
				logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_LOGOUT) );
				ftp.logout();
				if ( ftp.isConnected() ) {
					ftp.disconnect();
				}
				logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_DISCONNECTED) );
				ftp = null;
			} catch (IOException e) {
				LOGGER.error("Error closing ftp connection", e);
			}
		}
	}		
	
	public String getFTPPath( String destination, String name ) {
		return destination + PATH_SEPARATOR + name;
	}
	
	/**
	 * Delete file.
	 * 
	 * @param ftp the ftp
	 * @param pathname the pathname
	 */
	private void deleteFile(String pathname) {
		try {
			if ( ftp.deleteFile(pathname) ) {
				return;
			}
			LOGGER.error("File no deleted {}", pathname);
		} catch (Throwable th) {
			LOGGER.error("Error deleting file {}", pathname, th);
		}
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_DELETE_FILE, pathname) );
	}

	/**
	 * Delete directory.
	 * 
	 * @param ftp the ftp
	 * @param pathname the pathname
	 */
	private void deleteDirectory(String pathname) {
		try {
			if ( ftp.removeDirectory(pathname) ) {
				return;
			}
			LOGGER.error("Directory no deleted {}", pathname);
		} catch (Throwable th) {
			LOGGER.error("Error deleting directory {}", pathname, th);
		}
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_DELETE_DIRECTORY, pathname) );
	}

	private void delete( String destination, FTPFile file ) throws IOException {
		String name = getFTPPath(destination, file.getName() );
		if ( file.isFile() ) {
			deleteFile(name);
		} else if ( file.isDirectory() ) {
			delete(name);
			deleteDirectory(name);	
		}							
	}
	
	private boolean changeDirectory(String destination) {
		try {
			if ( ftp.changeWorkingDirectory(destination) ) {
				return true;
			}
		} catch (Throwable th) {
			LOGGER.error( "Error in change of working directory: " + destination, th );
		}
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_CHANGE_DIRECTORY, destination) );
		return false;
	}
	
	/**
	 * Delete.
	 * 
	 * @param destination the destination
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void delete( String destination ) throws IOException {
		if ( changeDirectory(destination) ) {
			for( FTPFile file: getFTPFiles(destination) ) {
				delete(destination, file);
			}
		}
	}
	
	private void uploadDirectory(File directory, String destination) {
		LOGGER.debug("Creating directory: {}", destination);
		try {
			if ( ftp.makeDirectory(destination) ) {
				upload(directory, destination);
				return;
			}
		} catch (IOException e) {
			LOGGER.error( "Error in make directory: " + destination, e );
		}
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_CREATE_DIRECTORY, destination) );
	}	

	private void uploadFile(File file, String destination) {
		LOGGER.debug("Creating file: {}", destination);
		InputStream in = null;
		try {
			in = new BufferedInputStream( new FileInputStream(file) );
			double kbs = file.length() /1024.0;
			logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_UPLOAD_FILE, file.getName(), kbs) );
			if ( ftp.storeFile(destination, in) ) {
				return;
			}
		} catch (IOException e) {
			LOGGER.error( "Error creating file: " + destination, e );
		} finally {
			IOUtils.closeQuietly(in);
		}
		logger.error( AonUtil.getMessage(CONFIG_BUNDLE, FTP_ERROR_CREATE_FILE, destination) );
	}	

	public boolean upload(InputStream in, int length, String destination) {
		boolean uploaded = false;
		LOGGER.debug("Creating file: {}", destination);
		try {
			String name = FilenameUtils.getName(destination);
			double kbs = length /1024.0;
			logger.info( AonUtil.getMessage(CONFIG_BUNDLE, FTP_UPLOAD_FILE, name, kbs) );
			uploaded = ftp.storeFile(destination, in);
		} catch (IOException e) {
			LOGGER.error( "Error creating file: " + destination, e );
		}
		return uploaded;
	}	
	
	private  void uploadContent(File file, String destination) {
		String path = getFTPPath(destination, file.getName() );
		if (file.isDirectory()) {
			uploadDirectory(file, path);
		} else {
			uploadFile(file, path);
		}
	}	

	/**
	 * Upload.
	 * 
	 * @param directory the ftp dir
	 * @param destination the bread crum
	 */
	public void upload(File directory, String destination) {
		for( File file : directory.listFiles() ) {
			uploadContent(file, destination);
		}
	}
	
	private List<FTPFile> getFTPFiles( String destination ) throws IOException {
		List<FTPFile> list = new LinkedList<FTPFile>();
		FTPFile files[] = ftp.listFiles(destination);
		if (! ArrayUtils.isEmpty(files)) {
			for( FTPFile file : files ) {
				if ( file != null ) {
					String name = file.getName();
					if (! (StringUtils.equals(name, ".") || StringUtils.equals(name, "..")) ) {
						list.add(file);
					}
				}
			}
		}
		return list;
	}
	
	private FTPFile getFTPFile( List<FTPFile> ftpFiles, String name ) {
		for( FTPFile ftpFile : ftpFiles ) {
			if ( StringUtils.equals(ftpFile.getName(), name) ) {
				return ftpFile;
			}
		}
		return null;
	}
	
	private void synchronizeDirectory(File directory, String destination, FTPFile ftpFile) throws IOException {
		String path = getFTPPath(destination, ftpFile.getName());
		if (! ftpFile.isDirectory() ) {
			delete(destination, ftpFile);
			uploadDirectory(directory, path);
		} else {
			synchronize(directory, path);
		}
	}
	
	private void synchronizeFile(File file, String destination, FTPFile ftpFile) throws IOException {
		boolean upload = false;
		if (! ftpFile.isFile() ) {
			delete(destination, ftpFile);
			upload = true;
		} else {
			if ( file.length() != ftpFile.getSize() ) {
				upload = true;
			} else {
				Date date1 = new Date( file.lastModified() );
				Date date2 = ftpFile.getTimestamp().getTime();
				if ( date1.after(date2) ) {
					upload = true;
				}
			}
		}
		if ( upload ) {
			uploadFile(file, getFTPPath(destination, ftpFile.getName()));
		}
	}	
	
	/**
	 * Upload.
	 * 
	 * @param ftpDir the ftp dir
	 * @param breadCrum the bread crum
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public void synchronize (File ftpDir, String destination) throws IOException {
		if ( changeDirectory(destination) ) {
			List<FTPFile> ftpFiles = getFTPFiles(destination);
			for( File file : ftpDir.listFiles() ) {
				FTPFile ftpFile = getFTPFile(ftpFiles, file.getName());
				if ( ftpFile != null ) {
					ftpFiles.remove(ftpFile);
					if ( file.isDirectory() ) {
						synchronizeDirectory(file, destination, ftpFile);
					} else {
						synchronizeFile(file, destination, ftpFile);
					}
				} else {
					uploadContent(file, destination);
				}
			}
			for( FTPFile file: ftpFiles ) {
				delete(destination, file);
			}			
		}
	}
	
}
