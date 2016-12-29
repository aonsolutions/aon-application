package com.esferalia.aon.file.seres.util.ftp;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class SeresFtpConnectionProvider {

	
	private String ftpServer = "webconnect.seresnet.com";
	private Integer ftpPort = 21;
	private String ftpUser = "ftp1251";
	private String ftpPassword = "x1xx0wub";

	public void storeFile(String remotePath, String fileName, InputStream localInputStream) {
		
		FtpConnector ftp = new FtpConnector() {
			
			@Override
			public void onSuccess() throws IOException {
				
				boolean success = changeWorkingDirectory(remotePath);
				
				if (success) {
					this.setFileType(FTP.BINARY_FILE_TYPE);
					
					// APPROACH #1: uploads first file using an InputStream
//					File firstLocalFile = new File("D:/Test/Projects.zip");
					
//					String firstRemoteFile = "Projects.zip";
//					InputStream inputStream = new FileInputStream(firstLocalFile);
					
					System.out.println("Start uploading first file");
					boolean done = this.storeFile(fileName, localInputStream);
					localInputStream.close();
					if (done) {
						System.out.println("The first file is uploaded successfully.");
					}
//					
//					// APPROACH #2: uploads second file using an OutputStream
//					File secondLocalFile = new File("E:/Test/Report.doc");
//					String secondRemoteFile = "test/Report.doc";
//					inputStream = new FileInputStream(secondLocalFile);
//					
//					System.out.println("Start uploading second file");
//					OutputStream outputStream = ftpClient.storeFileStream(secondRemoteFile);
//					byte[] bytesIn = new byte[4096];
//					int read = 0;
//					
//					while ((read = inputStream.read(bytesIn)) != -1) {
//						outputStream.write(bytesIn, 0, read);
//					}
//					inputStream.close();
//					outputStream.close();
//					
					boolean completed = this.completePendingCommand();
					if (completed) {
						System.out.println("file upload finished.");
					}
				}
				
			}
		};
		try {
			assignJvmSystemProperies();
			ftp.connect(ftpServer, ftpPort, ftpUser, ftpPassword);
		} finally{
			restoreJvmSystemProperies();
		}
	}
	
	public void retrieveFile(String path, String remoteFile) {
		
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				
//				int list = ftpc.list();
//				String[] listNames = ftpc.listNames();
//				FTPFile[] listFiles = ftpc.listFiles();
//				FTPFile[] listDirectories = ftpc.listDirectories();
				
				
				printFileTree("/", "", 0);
				
				boolean success = changeWorkingDirectory(path);
//				boolean success = changeWorkingDirectory("recepcion");
//				success = changeWorkingDirectory(ftpClient.listFiles()[7].getName());
				
				
				if (success) {
					
					this.setFileType(FTP.BINARY_FILE_TYPE);
//					String remoteFile = "ORDERS_1482924688-17457.TXT";
					File localFile = new File("/tmp/"+remoteFile);
					OutputStream localOutputStream = new BufferedOutputStream(
							new FileOutputStream(localFile));
					success = this.retrieveFile(remoteFile, localOutputStream);// <--- ERROR LINE
					localOutputStream.close();
					if (!success) {
						System.out.println("There was some problem retrieving file.");
						return;
					}
					System.out.println("File was downloaded!");
					
//		            // APPROACH #1: using retrieveFile(String, OutputStream)
//		            String remoteFile1 = "/test/video.mp4";
//		            File downloadFile1 = new File("C:/tmp/ftp1.unknown");
//		            OutputStream outputStream1 = new BufferedOutputStream(new FileOutputStream(downloadFile1));
//		            boolean success = ftpClient.retrieveFile(remoteFile1, outputStream1);
//		            outputStream1.close();
//		 
//		            if (success) {
//		                System.out.println("File #1 has been downloaded successfully.");
//		            }
//		 					
//		            // APPROACH #2: using InputStream retrieveFileStream(String)
//		            String remoteFile2 = "/test/song.mp3";
//		            File downloadFile2 = new File("C:/tmp/ftp2.unknown");
//		            OutputStream outputStream2 = new BufferedOutputStream(new FileOutputStream(downloadFile2));
//		            InputStream inputStream = ftpClient.retrieveFileStream(remoteFile2);
//		            byte[] bytesArray = new byte[4096];
//		            int bytesRead = -1;
//		            while ((bytesRead = inputStream.read(bytesArray)) != -1) {
//		                outputStream2.write(bytesArray, 0, bytesRead);
//		            }
// 
//		            success = ftpClient.completePendingCommand();
//		            if (success) {
//		                System.out.println("File #2 has been downloaded successfully.");
//		            }
//		            outputStream2.close();
//		            inputStream.close();
					
				} else {
					System.out.println("Could not change directory!");
				}
				
			}
		};
		
		try {
			assignJvmSystemProperies();
			ftp.connect(ftpServer, ftpPort, ftpUser, ftpPassword);
		} finally{
			restoreJvmSystemProperies();
		}
	}
	
	public List<String> obtainFiles(String path) {
		List<String> fileList = new LinkedList<>();
		
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				if (changeWorkingDirectory(path)) {
					this.fillDirectoryFileList(path, fileList);
				} else {
					System.out.println("Could not change directory!");
				}
			}
		};
		
		try {
			assignJvmSystemProperies();
			ftp.connect(ftpServer, ftpPort, ftpUser, ftpPassword);
		} finally{
			restoreJvmSystemProperies();
		}
		return fileList;
	}
	
	
	
	private void printFiles(FTPFile[] listFiles){
		System.out.println("LIST COUNT: "+listFiles.length);
		for(int i=0; i<listFiles.length;i++){
			System.out.print(listFiles[i].getName());
			System.out.print(" (size: "+listFiles[i].getSize());
			System.out.print(", group: "+listFiles[i].getGroup());
			System.out.print(", type: "+listFiles[i].getType());
			System.out.print(", link: "+listFiles[i].getLink());
			System.out.print(", timestamp: "+listFiles[i].getTimestamp().getTime());
			System.out.println(")");
			System.out.print("   File: "+listFiles[i].isFile());
			System.out.print(" - Directory: "+listFiles[i].isDirectory());
			System.out.print(" - Unknown: "+listFiles[i].isUnknown());
			System.out.println(" - SymbolicLink: "+listFiles[i].isSymbolicLink());
			System.out.println("");
		}
	}
	
	private void printFileDetails(FTPFile[] files) {
		DateFormat formater = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		for (FTPFile file : files) {
			String details = file.getName();
			details = file.isDirectory() ? "[" + details + "]" : details;
			details += "\t\t" + file.getSize();
			details += "\t\t" + formater.format(file.getTimestamp().getTime());
			System.out.println(details);
		}
	}
	

	
	abstract class FtpConnector {
		
		private String server;
		private Integer port;
		private String user;
		private String passwd;
		
		private List<String> fileTree;
		
		private FTPClient ftp = new FTPClient();
		
		public abstract void onSuccess() throws IOException;
		
		public void connect(String server, Integer port, String user, String passwd){
			this.server = server;
			this.port = port;
			this.user = user;
			this.passwd = passwd;
//			assignJvmSystemProperies();
			try {
				boolean success = login();
				if (success) {
					onSuccess();
				}
			} catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
//	        	restoreJvmSystemProperies();
	        	disconnect();
	        }
		}
		
		private boolean login() throws IOException {
			try {
				ftp.connect(server, port);
			} catch (Exception e) {
				System.out.println("Connection failed! (check hostname and port)");
			}
			
			int replyCode = ftp.getReplyCode();
			String replyString = ftp.getReplyString();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("Some error!");
				return false;
			}
			boolean success = ftp.login(user, passwd);
			if (success) {
				System.out.println("FTP LOGIN: " + server + (port!=null?":"+port:"") + "@" + user
						+ " (using password "
						+ (passwd != null ? "YES" : "NO") + ")");

				System.out.println("Login successful!");
				System.out.println(replyString);
				ftp.enterLocalPassiveMode();
				System.out.println(ftp.printWorkingDirectory());
				return success;
			} else {
				System.out.println("Login failed! (username and password)");
				return !success;
			}
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
				System.out.println(ftp.printWorkingDirectory());
//				FTPFile[] listFiles = ftp.listFiles();
//				printFiles(listFiles);
//				printFileDetails(listFiles);
			}
			return success;
		}
		
			
		protected void printFileTree(String parentDir, String currentDir, int level)
				throws IOException {
			buildFileTree(parentDir, currentDir, level);
			fileTree.forEach(System.out::println);
		}
		
		protected void buildFileTree(String parentDir, String currentDir,
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
						buildFileTree(dirToList, currentFileName, level + 1);
					} else {
						fileTree.add(String.format("%0" + (level + 1) + "d", 0)
								.replace("0", "\t") + currentFileName);
					}
				}
			}
		}
		
		protected void fillDirectoryFileList(String directoryPath, List<String> list) throws IOException {
			if(list!=null){
//				FTPFile[] subFiles = ftp.listFiles(directoryPath);
				FTPFile[] subFiles = ftp.listFiles();
				if (subFiles != null && subFiles.length > 0) {
					for (FTPFile aFile : subFiles) {
						String currentFileName = aFile.getName();
						if (currentFileName.equals(".")
								|| currentFileName.equals("..")) {
							continue;
						}
						if (aFile.isFile()) {
							list.add(currentFileName);
						}
					}
				}
			}
		}
		
		protected void setFileType(int fileType) throws IOException {
			ftp.setFileType(fileType);
		}
		
		protected boolean retrieveFile(String remoteFile, OutputStream localOutputStream) throws IOException {
			return ftp.retrieveFile(remoteFile, localOutputStream);
		}
		
		protected boolean storeFile(String remote, InputStream localInputStream) throws IOException {
			return ftp.storeFile(remote, localInputStream);
		}
		
		protected boolean completePendingCommand() throws IOException {
			return ftp.completePendingCommand();
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
		
	}

	// **************************************************
	// JVM system properties
	// **************************************************
	private static String preferIPv4Stack = null;
	private final static String PROPERTY_preferIPv4Stack = "java.net.preferIPv4Stack";
	
	private static void assignJvmSystemProperies() {
		preferIPv4Stack = System.getProperty(PROPERTY_preferIPv4Stack);
		System.setProperty(PROPERTY_preferIPv4Stack, "true");
	}

	private static void restoreJvmSystemProperies() {
		if(StringUtils.isBlank(preferIPv4Stack)){
			System.clearProperty(PROPERTY_preferIPv4Stack);
		} else {
			System.setProperty(PROPERTY_preferIPv4Stack, preferIPv4Stack);
		}
	}
	
	
	// ***************
	// main method	
	// ***************
	public static void main(String[] args) {
		
//		String ftpServer = "webconnect.seresnet.com";
//		Integer ftpPort = 21;
//		String ftpUser = "ftp1251";
//		String ftpPassword = "x1xx0wub";
		
		SeresFtpConnectionProvider connection = new SeresFtpConnectionProvider();

		String remoteFile = "";
		String remotePath = "";
		
		// SHOW FILES
		System.out.println("# Operation: show files");
		remotePath = "recepcion/orders_d96a";
		connection.obtainFiles(remotePath).forEach(System.out::println);
		
		// RETRIEVE FILE
		System.out.println("# Operation: retrieve file");
		remoteFile = "ORDERS_1482924688-17457.TXT";
		connection.retrieveFile("recepcion/orders_d96a", remoteFile);
		
		// STORE FILE
//		System.out.println("# Operation: store file");
//		connection.storeFile(null, null, null);
		
		System.out.println("## FINISH!!!");
		
	}
}
