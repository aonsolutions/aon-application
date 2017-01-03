package com.esferalia.aon.file.seres.util.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class SeresFtpConnectionProvider implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
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
					
					System.out.println("Start uploading first file");
					boolean done = this.storeFile(fileName, localInputStream);
					localInputStream.close();
					if (done) {
						System.out.println("The first file is uploaded successfully.");
					}
				
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
	
	public byte[] retrieveFile(String path, String remoteFile) {
		
//		byte[] out = null;
		ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
		
		FtpConnector ftp = new FtpConnector() {
			@Override
			public void onSuccess() throws IOException {
				
//				printFileTree("/", "", 0);
				
				boolean success = changeWorkingDirectory(path);
				
				if (success) {
					
					this.setFileType(FTP.BINARY_FILE_TYPE);
//					String remoteFile = "ORDERS_1482924688-17457.TXT";
//					File localFile = new File("/tmp/"+remoteFile);
//					OutputStream localOutputStream = new BufferedOutputStream(
//							new FileOutputStream(localFile));
//					ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
					success = this.retrieveFile(remoteFile, localOutputStream);
					if (!success) {
						System.out.println("There was some problem retrieving file.");
						return;
					}
					System.out.println("File was downloaded!");
					
				} else {
					System.out.println("Could not change directory!");
				}
				
			}
		};
		
		try {
			assignJvmSystemProperies();
			ftp.connect(ftpServer, ftpPort, ftpUser, ftpPassword);
			return localOutputStream.toByteArray();
		} finally{
			try {
				localOutputStream.close();
			} catch (IOException e) {
				// nothing
			}
			restoreJvmSystemProperies();
		}
		
	}
	
	public List<FtpFile> obtainFiles(String path) {
		List<FtpFile> fileList = new LinkedList<>();
		
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
			assignJvmSystemProperies();
			try {
				boolean success = login();
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
		
		private boolean login() throws IOException {
			try {
				ftp.connect(server, port);
			} catch (Exception e) {
				System.out.println("Connection failed! (check hostname and port)");
			}
			
			String replyString = ftp.getReplyString();
			System.out.print("REPLY: ");
			System.out.println(replyString);
			
			int replyCode = ftp.getReplyCode();
			if (!FTPReply.isPositiveCompletion(replyCode)) {
				System.out.println("Some error!");
				return false;
			}
			ftp.enterLocalPassiveMode();
			System.out.print("FTP LOGIN: " + server + (port!=null?":"+port:"") + "@" + user
					+ " (using password "
					+ (passwd != null ? "YES" : "NO") + ")");
			boolean success = ftp.login(user, passwd);
			if (success) {
				System.out.println(" -> SUCCESS!");
				return success;
			} else {
				System.out.println(" -> FAILED! (check username and password)");
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
				System.out.print("WorkingDirectory changed to ");
				System.out.println("'"+ftp.printWorkingDirectory()+"'");
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
			
//			FTPFileFilter filter = FTPFileFilters.ALL;
//			filter.;
			
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
		
		protected void fillDirectoryFileList(String directoryPath, List<FtpFile> list) throws IOException {
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
							// list.add(currentFileName);
							list.add((new FtpFile()).setName(aFile.getName())
									.setGroup(aFile.getGroup())
									.setSize(aFile.getSize())
									.setTimestamp(aFile.getTimestamp()));
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
//		protected boolean retrieveFile(String remoteFile, OutputStream localOutputStream, byte[] out) throws IOException {
//			boolean success = ftp.retrieveFile(remoteFile, localOutputStream);
//			out = ((ByteArrayOutputStream) localOutputStream).toByteArray();
//			return success;
////			return ftp.retrieveFile(remoteFile, localOutputStream);
//		}
		
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
//		System.out.println("# Operation: retrieve file");
//		remoteFile = "ORDERS_1482924688-17457.TXT";
//		connection.retrieveFile("recepcion/orders_d96a", remoteFile);
		
		// STORE FILE
//		System.out.println("# Operation: store file");
//		connection.storeFile(null, null, null);
		
		System.out.println("## FINISH!!!");
		
	}
}
