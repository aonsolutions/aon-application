package com.code.aon.ui.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.code.aon.cms.Config;
import com.code.aon.common.AonException;
import com.code.aon.ui.util.AonUtil;

public class FTPUtil {

	private static Config config;

	@SuppressWarnings({ "finally", "finally" })
	public static boolean uploadFTP() throws IOException {
		boolean error = false;
		config = ControllerUtil.getCurrentConfig();
		String server = config.getFtp_server();
		String user = config.getFtp_user();
		String password = config.getFtp_password();
		String destinationFolder = config.getFtp_path();
		String sourceFolder = ControllerUtil.getPreviewPath();
		FTPClient ftp = new FTPClient();

		try {
			ftp.connect(server);
			if (ftp.getReplyCode() >= 500) {
				AonUtil.addErrorMessage(ftp.getReplyString());
				throw new AonException();
			}
			else System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getReplyString());
			ftp.login(user, password);
			if (ftp.getReplyCode() >= 500) {
				AonUtil.addErrorMessage(ftp.getReplyString());
				throw new AonException();
			}
			else System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getReplyString());

			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Connected to server");
			ftp.changeWorkingDirectory(destinationFolder);
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getReplyString());
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getSystemName());
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.printWorkingDirectory());
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftp.isConnected()) {
				FTPFile files[] = ftp.listFiles();
				if (files.length > 0) {
					if (!files[0].hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ) {
						System.out.println("FTP ERROR>>>>>>>>>>>>>>>>>>>>>> Error de escritura en el servidor.");
					}
				}
				ftpDir(sourceFolder, ftp, destinationFolder);
			}
			else {
				System.out.println("FTP ERROR>>>>>>>>>>>>>>>>>>>>>> No hubo conexion con el servidor.");
				error = true;
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage("FTP Error. Se produjo un error durante la conexion al FTP, si el error persite consulte con su administrador.");
			error = true;
		} finally {
			ftp.logout();
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Logout.");
			ftp.disconnect();
			System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Diconnected.");
			return error;
		}
		
	}

	public static String invalidFolder[] = {"ckfinder", "_thumbs"};
	
	public static void ftpDir(String dir2ftp, FTPClient fc, String breadCrum) {
		try {
			File ftpDir = new File(dir2ftp);
			String[] dirList = ftpDir.list();
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(ftpDir, dirList[i]);
				if (!f.getName().equals(config.getDomain() + ".zip")) {
					System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Name: " + breadCrum + "/" + f.getName());
					if (f.isDirectory()) {
						if (isValidFolder(f.getName())) { 
							fc.makeDirectory(breadCrum + "/" + f.getName());
							String filePath = f.getPath();
							ftpDir(filePath, fc, breadCrum + "/" + f.getName());
						}
						continue;
					}
					FileInputStream fis = new FileInputStream(f);
					//fc.deleteFile(breadCrum + "/" + f.getName());
					fc.storeFile(breadCrum + "/" + f.getName(), fis);
					fis.close();
				}
			}
		} catch (Exception e) {
			AonUtil.addErrorMessage(e.getMessage());
			//addMessage("FTP Error. Se produjo un error al intentar subir el fichero " + file, GEN_ERROR);
		}
	}

	private static boolean isValidFolder(String name) {
		for (int i = 0; i < invalidFolder.length ; i++) {
			if (name.equals(invalidFolder[i])) {
				return false;
			}
		}
 		return true;
	}


}
