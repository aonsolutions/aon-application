package com.code.aon.webinfo.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

public class FTPUtil {

	private static String server = "127.0.0.1";
	private static String user = "ftpcms";
	private static String password = "cms2001";
	
	public static void uploadFTP(String source, String destination) {

		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(server);
			ftp.login(user, password);
			ftp.changeWorkingDirectory(destination);
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Connected to server");
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getReplyString());
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getSystemName());
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.printWorkingDirectory());
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.setFileType(FTPClient.BINARY_FILE_TYPE));
			FTPFile files[] = ftp.listFiles();
			if (files.length > 0) {
				if (!files[0].hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ) {
					System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> WRITE PERMISSION DENIED");
				}
			}
			ftpDir(source, ftp, destination);

			ftp.logout();
			ftp.disconnect();
			//addMessage("La publicacion por FTP de la pagina web a finalizado.", GEN_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			//addMessage("FTP Error. Se produjo un error durante la conexion al FTP, si el error persite consulte con su administrador.", GEN_ERROR);
		}
	}

	private static void ftpDir(String dir2ftp, FTPClient fc, String breadCrum) {
		try {
			File ftpDir = new File(dir2ftp);
			String[] dirList = ftpDir.list();
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(ftpDir, dirList[i]);
				if (f.isDirectory()) {
					System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Creating directory: " + breadCrum + "/" + f.getName());
					if (!fc.makeDirectory(breadCrum + "/" + f.getName())) {
						System.out.println(">>>>>>>>>> FTP ERROR, Can not create " + f.getName() + " directory");
					}
					String filePath = f.getPath();
					ftpDir(filePath, fc, breadCrum + "/" + f.getName());
					continue;
				}
				FileInputStream fis = new FileInputStream(f);
				//fc.deleteFile(breadCrum + "/" + f.getName());
				System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Creating file: " + breadCrum + "/" + f.getName());
				if (!fc.storeFile(breadCrum + "/" + f.getName(), fis)) {
					System.out.println(">>>>>>>>>>>>> FTP ERROR, Can not write " + f.getName());
				}
				fis.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
			//addMessage("FTP Error. Se produjo un error al intentar subir el fichero " + file, GEN_ERROR);
		}
	}


}
