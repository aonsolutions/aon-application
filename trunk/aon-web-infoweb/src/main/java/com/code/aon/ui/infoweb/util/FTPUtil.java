package com.code.aon.ui.infoweb.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.logging.Logger;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.code.aon.ui.util.AonUtil;

public class FTPUtil {

	private static final Logger LOGGER = Logger.getLogger(FTPUtil.class.getName());
	
	private static final String SERVER = "192.168.3.47";
	private static final String USER = "ftpcms";
	private static final String PASSWORD = "cms2001";
	
	public static void uploadFTP(String source, String destination, String domain) throws IOException {

		FTPClient ftp = new FTPClient();
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> Connecting to: " + SERVER );
		ftp.connect(SERVER); // + domain
		ftp.login(USER, PASSWORD);
		ftp.changeWorkingDirectory(destination);
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> Connected.");
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getReplyString());
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.getSystemName());
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.printWorkingDirectory());
		LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> " + ftp.setFileType(FTPClient.BINARY_FILE_TYPE));
		FTPFile files[] = ftp.listFiles();
		if (files.length > 0) {
			if (!files[0].hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ) {
				LOGGER.severe("FTP>>>>>>>>>>>>>>>>>>>>>> WRITE PERMISSION DENIED");
				AonUtil.addErrorMessage("FTP ERROR: Error intentando escribir en el servidor.");
			}
		}
		ftpDir(source, ftp, destination);

		ftp.logout();
		ftp.disconnect();
	}

	private static void ftpDir(String dir2ftp, FTPClient fc, String breadCrum) throws IOException {
		File ftpDir = new File(dir2ftp);
		String[] dirList = ftpDir.list();
		for (int i = 0; i < dirList.length; i++) {
			File f = new File(ftpDir, dirList[i]);
			if (f.isDirectory()) {
				LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> Creating directory: " + breadCrum + "/" + f.getName());
				if (!fc.makeDirectory(breadCrum + "/" + f.getName())) {
					LOGGER.severe(">>>>>>>>>> FTP ERROR, Can not create " + f.getName() + " directory");
				}
				String filePath = f.getPath();
				ftpDir(filePath, fc, breadCrum + "/" + f.getName());
				continue;
			}
			FileInputStream fis = new FileInputStream(f);
			LOGGER.fine("FTP>>>>>>>>>>>>>>>>>>>>>> Creating file: " + breadCrum + "/" + f.getName());
			if (!fc.storeFile(breadCrum + "/" + f.getName(), fis)) {
				LOGGER.severe(">>>>>>>>>>>>> FTP ERROR, Can not write " + f.getName());
			}
			fis.close();
		}
	}

}
