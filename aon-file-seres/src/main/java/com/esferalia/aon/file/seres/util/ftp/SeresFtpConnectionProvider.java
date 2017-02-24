package com.esferalia.aon.file.seres.util.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPFileFilter;
import org.apache.commons.net.ftp.FTPReply;

import com.code.aon.common.ILogger;

public class SeresFtpConnectionProvider implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public static boolean checkLogin(String server, Integer port, String user,
			String passwd) throws FtpLoginException, FtpException {
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				// FtpLoginException is throwed if login failed
			}
		};
		ftp.connect(server, port, user, passwd);
		return true;
	}
	
	public static boolean storeFile(String remotePath, String fileName,
			InputStream localInputStream, String server, Integer port,
			String user, String passwd) throws FtpLoginException, FtpException {
		boolean completed = false;
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException, FtpException {
				this.storeFile(remotePath, fileName, localInputStream,
						completed);
				localInputStream.close();
			}
		};
		ftp.connect(server, port, user, passwd);
		return completed;
	}
	
	public static byte[] retrieveFile(String remotePath, String remoteFile,
			String server, Integer port, String user, String passwd)
			throws FtpLoginException, FtpException {

		ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				boolean success = this.retrieveFile(remotePath, remoteFile,
						localOutputStream);
				if (!success) {
					System.out
							.println("There was some problem retrieving file.");
					return;
				}
				// TODO log me
//				System.out.println("File was downloaded!");
			}
		};
		try {
			ftp.connect(server, port, user, passwd);
			return localOutputStream.toByteArray();
		} finally {
			try {
				localOutputStream.close();
			} catch (IOException e) {
				// nothing
			}
		}
	}
	
	public static List<String> retrieveDirectoryList(String remotePath,
			String server, Integer port, String user, String passwd)
			throws FtpLoginException, FtpException {
		List<String> fileList = new LinkedList<>();

//		printFileTree("/", "", 0);

		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				this.fillDirectoryList(remotePath, fileList);
			}
		};

		ftp.connect(server, port, user, passwd);

		return fileList;
	}
	
	public static List<FtpFile> retrieveFileList(String remotePath, Date start,
			Date end, String server, Integer port, String user, String passwd)
			throws FtpLoginException, FtpException {
		List<FtpFile> fileList = new LinkedList<>();

		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException, FtpException {
				if (changeWorkingDirectory(remotePath)) {
					this.fillFileList(remotePath, start, end, fileList);
				} else {
					System.out.println("Could not change directory!");
				}
			}
		};

		ftp.connect(server, port, user, passwd);

		return fileList;
	}
	
	public static boolean deleteFile(String remotePath,
			String server, Integer port, String user, String passwd)
					throws FtpLoginException, FtpException {
		boolean completed = false;
		
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException, FtpException {
				this.deleteFile(remotePath, completed);
			}
		};
		
		ftp.connect(server, port, user, passwd);
		
		return completed;
	}
	
	
	
}







abstract class FtpConnector {
	
	private List<String> fileTree;
	
	private FTPClient ftp = new FTPClient();
	
	public abstract void onSuccess() throws IOException, FtpException;
	
	
	public void connect(String server, Integer port, String user, String passwd)
			throws FtpLoginException, FtpException {
		assignJvmSystemProperies();
		try {
			boolean success = login(server, port, user, passwd);
			if (success) {
				onSuccess();
			}
		} catch (IOException ex) {
			System.out.println("Error: " + ex.getMessage());
			ex.printStackTrace();
		} finally {
			restoreJvmSystemProperies();
			disconnect();
		}
	}
	
	private boolean login(String server, Integer port, String user,
			String passwd) throws IOException, FtpLoginException, FtpException {
		try {
			ftp.connect(server, port);
		} catch (Exception e) {
//			System.out.println("Connection failed! (check hostname and port)");
			throw new FtpException("Connection failed! (check hostname and port)", e);
		}

		// TODO log me
//		String replyString = ftp.getReplyString();
//		System.out.println("REPLY: " + replyString);

		int replyCode = ftp.getReplyCode();
		if (!FTPReply.isPositiveCompletion(replyCode)) {
			System.err.println("Some error!");
			return false;
		}
		ftp.enterLocalPassiveMode();
		// TODO log me
//		System.out.print("FTP LOGIN: " + server
//				+ (port != null ? ":" + port : "") + "@" + user
//				+ " (using password "
//				+ (passwd != null && !"".equals(passwd) ? "YES" : "NO") + ")");
		boolean success = ftp.login(user, passwd);
		if (success) {
			// TODO log me
//			System.out.println(" -> SUCCESS!");
		} else {
			// TODO log me
//			System.out.println(" -> FAILED! (check username and password)");
			throw new FtpLoginException(
					"Login failed! (check username and password)");
		}
		return success;
	}
	
	private void disconnect() {
		try {
			if (ftp.isConnected()) {
				ftp.logout();
				ftp.disconnect();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
	
	protected boolean changeWorkingDirectory(String path) throws IOException{
		boolean success = ftp.changeWorkingDirectory(path);
		if(success){
			// TODO log me
//			System.out.print("WorkingDirectory changed to ");
//			System.out.println("'"+ftp.printWorkingDirectory()+"'");
		}
		return success;
	}
	
		
	protected void printFileTree(String parentDir, String currentDir, int level)
			throws IOException {
		buildFullTree(parentDir, currentDir, level);
		fileTree.forEach(System.out::println);
	}
	
	protected void buildFullTree(String parentDir, String currentDir,
			int level) throws IOException {
		String dirToList = parentDir!=null?parentDir:"/";
		
		if (currentDir==null || currentDir.equals("")) {
			fileTree = new LinkedList<>();
		}
		if (!currentDir.equals("")) {
			dirToList += "/" + currentDir;
		}
		
		FTPFile[] subFiles = ftp.listFiles(dirToList);
		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile aFile : subFiles) {
				String currentFileName = aFile.getName();
				if (currentFileName.equals(".")
						|| currentFileName.equals("..")) {
					continue;
				}
				if (aFile.isDirectory()) {
					fileTree.add(String.format("%0" + (level + 1) + "d", 0)
							.replace("0", "\t")
							+ "[" + currentFileName + "]");
					buildFullTree(dirToList, currentFileName, level + 1);
				} else {
					fileTree.add(String.format("%0" + (level + 1) + "d", 0)
							.replace("0", "\t") + currentFileName);
				}
			}
		}
	}
	
	protected void fillDirectoryList(String directoryPath, List<String> list) throws IOException {
		if (list == null) {
			list = new LinkedList<>();
		}
		FTPFile[] subDirectories = null;
		try {
			if(directoryPath!=null){
				subDirectories = ftp.listFiles(directoryPath);
			} else {
				subDirectories = ftp.listFiles();
			}
		} catch (IOException e) {
			subDirectories = ftp.listFiles();
		}
		if (subDirectories != null && subDirectories.length > 0) {
			for (FTPFile f : subDirectories) {
				String currentFileName = f.getName();
				if (currentFileName.equals(".")
						|| currentFileName.equals("..")) {
					continue;
				}
				if (f.isDirectory()) {
					list.add((directoryPath!=null?directoryPath:"")+"/"+f.getName());
					fillDirectoryList((directoryPath!=null?directoryPath:"")+"/"+f.getName(), list);
				}
			}
		}
	}
	
	protected void fillFileList(String directoryPath, Date start, Date end,
			List<FtpFile> list) throws IOException, FtpException {
		if (list == null) {
			list = new LinkedList<>();
		}
		FTPFile[] subFiles = null;
		try {
			FTPFileFilter filter = null;
			if (start != null || end != null) {
				filter = new FTPFileFilter() {
					@Override
					public boolean accept(FTPFile arg0) {
						return (start == null ? true : DateUtils.ceiling(
								arg0.getTimestamp().getTime(),
								Calendar.DAY_OF_MONTH)
								.compareTo(
										DateUtils.ceiling(start,
												Calendar.DAY_OF_MONTH)) >= 0)
								&& (end == null ? true : DateUtils.ceiling(
										arg0.getTimestamp().getTime(),
										Calendar.DAY_OF_MONTH).compareTo(
										DateUtils.ceiling(end,
												Calendar.DAY_OF_MONTH)) <= 0);
					}
				};
			}
			if (directoryPath != null && filter != null) {
				subFiles = ftp.listFiles(directoryPath, filter);
			} else if (directoryPath != null) {
				subFiles = ftp.listFiles(directoryPath);
			} else {
				subFiles = ftp.listFiles();
			}
		} catch (IOException e) {
			throw new FtpException(e);
		}
		if (subFiles != null && subFiles.length > 0) {
			for (FTPFile f : subFiles) {
				String currentFileName = f.getName();
				if (currentFileName.equals(".") || currentFileName.equals("..")) {
					continue;
				}
				if (f.isFile()) {
					list.add(new FtpFile(f));
				}
			}
		}
	}

	protected boolean retrieveFile(String remotePath, String remoteFile, OutputStream localOutputStream) throws IOException {
		boolean success = changeWorkingDirectory(remotePath);
		if (success) {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			success = ftp.retrieveFile(remoteFile, localOutputStream);
		}
		return success;
	}
	
	protected void storeFile(String remotePath, String fileName, InputStream localInputStream, boolean completed) throws IOException, FtpException {
		boolean success = changeWorkingDirectory(remotePath);
		if (success) {
			ftp.setFileType(FTP.BINARY_FILE_TYPE);
			success =  ftp.storeFile(fileName, localInputStream);
			if (!success) {
				// TODO log me
				System.out.println("ERROR: the file is not uploaded successfully.");
				throw new FtpException("Ha ocurrido un error en la transmision del fichero");
			}
			completed = ftp.completePendingCommand();
		}
	}
	
	protected boolean deleteFile(String remotePath, boolean completed) throws IOException {
		ftp.setFileType(FTP.BINARY_FILE_TYPE);
		completed = ftp.deleteFile(remotePath);
		return completed;
	}
	
	
	// **************************************************
	// JVM system properties
	// **************************************************
	private String preferIPv4Stack = null;
	private final static String PROPERTY_preferIPv4Stack = "java.net.preferIPv4Stack";
	
	private void assignJvmSystemProperies() {
		preferIPv4Stack = System.getProperty(PROPERTY_preferIPv4Stack);
		System.setProperty(PROPERTY_preferIPv4Stack, "true");
	}

	private void restoreJvmSystemProperies() {
		if(StringUtils.isBlank(preferIPv4Stack)){
			System.clearProperty(PROPERTY_preferIPv4Stack);
		} else {
			System.setProperty(PROPERTY_preferIPv4Stack, preferIPv4Stack);
		}
	}

	
	class DefaultLogger implements ILogger {

		@Override
		public void error(String arg0) {
			System.err.println("# FTP ERROR: " + arg0);
		}

		@Override
		public void info(String arg0) {
			System.out.println("# FTP: " + arg0);
		}

		@Override
		public void warn(String arg0) {
			System.out.println("# FTP WARN: " + arg0);
		}
		
	}
}
