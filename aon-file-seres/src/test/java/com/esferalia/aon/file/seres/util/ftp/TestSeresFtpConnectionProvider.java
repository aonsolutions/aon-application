package com.esferalia.aon.file.seres.util.ftp;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

public class TestSeresFtpConnectionProvider {
	
//	private final static String ARG_USER = "user";
//	private final static String ARG_PASSWD = "pwd";
	private final static String ARG_PORT = "p";
	
	
	public static void main(String[] args) {
		
		Map<String, String> arguments = obtainParams(args);
		
		
		String ftpServer = "webconnect.seresnet.com";
		Integer ftpPort = Integer.valueOf(arguments.containsKey(ARG_PORT)?arguments.get(ARG_PORT):"21");
		String ftpUser = "ftp1251";
		String ftpPassword = "x1xx0wub";
		String orderPath = "/recepcion/orders_d96a";
		String deliveryPath = "/envio/desadv_d96a";
		String remotePath = "";
		
		
		System.out.println("# Operation: show all files and directories");
		
		List<String> fileList = new LinkedList<>();
		
		FTPClient ftp = new FTPClient();
		try {
			assignJvmSystemProperies();
			
			try {
				boolean success = false;
//				InetAddress localAddress = null;
//				InetAddress remoteAddress = null;
				try {
//					ftp.connect(ftpServer);
					ftp.connect(ftpServer, Integer.valueOf(ftpPort));
					
//					localAddress = InetAddress.getLocalHost();
//					remoteAddress = InetAddress.getByName(ftpServer);
//					System.out.println("FTP CONNECT: "
//									+ "localHost: " + localAddress + (ftpPort!=null?":"+ftpPort:"")
//									+ " - "
//									+ "remoteHost: " + remoteAddress + ":21" 
//							);
//					ftp.connect(remoteAddress, ftp.getLocalPort(), localAddress, Integer.valueOf(ftpPort));
					
				} catch (Exception e) {
					System.out.println("Connection failed! (check hostname and port)");
					System.exit(-1);
				}
				
				int replyCode = ftp.getReplyCode();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
	                System.out.println("Connect failed");
	                return;
	            }
				
				System.out.println("FTP Connected! "
						+ "LOCAL -> " + ftp.getLocalAddress() +":"+ ftp.getLocalPort() 
						+ ", REMOTE -> " + ftp.getRemoteAddress() +":"+ftp.getRemotePort());
				
				
				String replyString = ftp.getReplyString();
				if (!FTPReply.isPositiveCompletion(replyCode)) {
					System.out.println("Some error!");
					success = false;
				}
				ftp.enterLocalPassiveMode();				
				ftp.enterRemotePassiveMode();
//				ftp.enterLocalActiveMode();
//				ftp.enterRemoteActiveMode(remoteAddress, Integer.valueOf(ftpPort));
				System.out.print("FTP LOGIN: " + ftpServer + (ftpPort!=null?":"+ftpPort:"") + "@" + ftpUser
						+ " (using password "
						+ (ftpPassword != null ? "YES" : "NO") + ")");
				success = ftp.login(ftpUser, ftpPassword);
				if (success) {
					System.out.println(" -> SUCCESS!");
					System.out.print("REPLY: ");
					System.out.println(replyString);
				} else {
					System.out.println(" -> FAILED! (check username and password)");
					success = !success;
				}
				
				System.out.println(ftp.printWorkingDirectory());
				printFileTree(ftp, "/", "", 0);
				
				if (success) {
					success = ftp.changeWorkingDirectory(remotePath);
					if(success){
						System.out.print("WorkingDirectory changed to ");
						System.out.println("'"+ftp.printWorkingDirectory()+"'");
//						FTPFile[] listFiles = ftp.listFiles();
//						printFiles(listFiles);
//						printFileDetails(listFiles);
					
//						if(fileList!=null){
//							FTPFile[] subFiles = ftp.listFiles();
//							if (subFiles != null && subFiles.length > 0) {
//								for (FTPFile aFile : subFiles) {
//									String currentFileName = aFile.getName();
//									if (currentFileName.equals(".")
//											|| currentFileName.equals("..")) {
//										continue;
//									}
//									if (aFile.isFile()) {
//										fileList.add(currentFileName);
//									}
//								}
//							}
//						}
					} else {
						System.out.println("Could not change directory!");
					}
				}
			} catch (IOException ex) {
	            System.out.println("Error: " + ex.getMessage());
	            ex.printStackTrace();
	        } finally {
	        	restoreJvmSystemProperies();
	        	try {
					if (ftp.isConnected()) {
						ftp.logout();
						ftp.disconnect();
					}
				} catch (IOException ex) {
					ex.printStackTrace();
				}
	        }
			
		} finally{
			restoreJvmSystemProperies();
		}
		fileList.forEach(System.out::println);
		
		
		try {
			// SHOW FILES
			System.out.println("# ACTION: show files");
			remotePath = orderPath;
			SeresFtpConnectionProvider.retrieveFileList(remotePath, null, null, ftpServer,
					ftpPort, ftpUser, ftpPassword).forEach(System.out::println);
			
			// SHOW DIRECTRY TREE
			System.out.println("# ACTION: show directory tree");
			remotePath = "/";
			SeresFtpConnectionProvider.retrieveDirectoryList(remotePath, ftpServer,
					ftpPort, ftpUser, ftpPassword).forEach(System.out::println);
			
			// RETRIEVE FILE
//			System.out.println("# ACTIOB: retrieve file");
//			remoteFile = "ORDERS_1482924688-17457.TXT";
//			SeresFtpConnectionProvider.retrieveFile("recepcion/orders_d96a", remoteFile, ftpServer,
//				ftpPort, ftpUser, ftpPassword);
			
			// STORE FILE
			System.out.println("# ACTION: store file");
			remotePath = deliveryPath;
//			SeresFtpConnectionProvider.storeFile(null, null, null);
		} catch (FtpLoginException e) {
			e.printStackTrace();
		} catch (FtpException e) {
			e.printStackTrace();
		}
		
		System.out.println("## FINISH!!!");
		
	}
	
	private static Map<String, String> obtainParams(String[] args) {
		Map<String, String> arguments = new HashMap<>();
    	Arrays.asList(args).forEach(arg -> {
    		if(arg.matches("-[p]\\d{1,2}")){
    			arguments.put(ARG_PORT, arg.substring(arg.indexOf(ARG_PORT)+1, arg.length()));
    		}
    	});
    	return arguments;
	}
	
	
	
	private static List<String> fileTree;
	
	protected static void printFileTree(FTPClient ftp, String parentDir, String currentDir, int level)
			throws IOException {
		buildFileTree(ftp, parentDir, currentDir, level);
		fileTree.forEach(System.out::println);
	}
	
	protected static void buildFileTree(FTPClient ftp, String parentDir, String currentDir,
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
					buildFileTree(ftp, dirToList, currentFileName, level + 1);
				} else {
//					fileTree.add(String.format("%0" + (level + 1) + "d", 0)
//							.replace("0", "\t") + currentFileName);
					if(!fileTree.get(fileTree.size()-1).contains("...")){
						fileTree.add(String.format("%0" + (level + 1) + "d", 0)
								.replace("0", "\t") + "...");
					}
				}
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
		if(preferIPv4Stack==null || "".equals(preferIPv4Stack)){
			System.clearProperty(PROPERTY_preferIPv4Stack);
		} else {
			System.setProperty(PROPERTY_preferIPv4Stack, preferIPv4Stack);
		}
	}

	
}
