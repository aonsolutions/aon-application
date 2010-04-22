package com.code.aon.ui.cms.util;

import java.io.File;
import java.io.FileInputStream;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.code.aon.cms.Config;


public class FTPUtil {

	private Config config;

	public void uploadFTP() {
		config = ControllerUtil.getCurrentConfig();
		String server = config.getFtp_server();
		String user = config.getFtp_user();
		String password = config.getFtp_password();
		String destinationFolder = config.getFtp_path();
		String sourceFolder = ControllerUtil.getPreviewPath();

		try {
			FTPClient ftp = new FTPClient();
			ftp.connect(server);
			ftp.login(user, password);
			ftp.changeWorkingDirectory(destinationFolder);
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
			ftpDir(sourceFolder, ftp, destinationFolder);

			ftp.logout();
			ftp.disconnect();
			//addMessage("La publicacion por FTP de la pagina web a finalizado.", GEN_INFO);
		} catch (Exception e) {
			e.printStackTrace();
			//addMessage("FTP Error. Se produjo un error durante la conexion al FTP, si el error persite consulte con su administrador.", GEN_ERROR);
		}
	}

	public void ftpDir(String dir2ftp, FTPClient fc, String breadCrum) {
		try {
			File ftpDir = new File(dir2ftp);
			String[] dirList = ftpDir.list();
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(ftpDir, dirList[i]);
				if (!f.getName().equals(config.getDomain() + ".zip")) {
					System.out.println("FTP>>>>>>>>>>>>>>>>>>>>>> Name: " + breadCrum + "/" + f.getName());
					if (f.isDirectory()) {
						fc.makeDirectory(breadCrum + "/" + f.getName());
						String filePath = f.getPath();
						ftpDir(filePath, fc, breadCrum + "/" + f.getName());
						continue;
					}
					FileInputStream fis = new FileInputStream(f);
					//fc.deleteFile(breadCrum + "/" + f.getName());
					fc.storeFile(breadCrum + "/" + f.getName(), fis);
					fis.close();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			//addMessage("FTP Error. Se produjo un error al intentar subir el fichero " + file, GEN_ERROR);
		}
	}


}
