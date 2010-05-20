package com.code.aon.ui.cms.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;

import com.code.aon.cms.Config;
import com.code.aon.common.AonException;
import com.code.aon.ui.cms.controller.GeneratorStatusController;
import com.code.aon.ui.cms.controller.ICMSConstants;
import com.code.aon.ui.util.AonUtil;

public class FTPUtil implements ICMSConstants {

	private static Config config;

	public static boolean uploadFTP() throws IOException {
		config = ControllerUtil.getCurrentConfig();
		String destinationFolder = config.getFtp_path();
		File sourceFolder = ControllerUtil.getPreviewPath();
		String server = config.getFtp_server();
		String user = config.getFtp_user();
		String password = config.getFtp_password();
		return uploadFTP(destinationFolder,
				sourceFolder,
				server,
				user,
				password);
	}

	public static boolean uploadPreviewFTP() throws IOException {
		config = ControllerUtil.getCurrentConfig();
		String destinationFolder = config.getPreview_ftp_path();
		File sourceFolder = ControllerUtil.getPreviewPath();
		String server = config.getPreview_ftp_server();
		String user = config.getPreview_ftp_user();
		String password = config.getPreview_ftp_password();
		return uploadFTP(destinationFolder,
				sourceFolder,
				server,
				user,
				password);
	}

	@SuppressWarnings({ "finally", "finally" })
	public static boolean uploadFTP(
			String dest, 
			File source,
			String server,
			String user,
			String password
			) throws IOException {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
		status.info("Publicando via FTP");
		status.info("Conectando....");
		
		boolean error = true;
		String destinationFolder = dest;
		FTPClient ftp = new FTPClient();

		try {
			ftp.connect(server);
			if (ftp.getReplyCode() >= 500) {
				status.error(ftp.getReplyString());
				throw new AonException();
			} else {
				status.info(ftp.getReplyString());
			}
			status.info("Validando....");
			ftp.login(user, password);
			if (ftp.getReplyCode() >= 500) {
				status.error(ftp.getReplyString());
				throw new AonException();
			} else {
				status.info(ftp.getReplyString());
			}

			status.info("Conectado.");
			ftp.enterLocalPassiveMode();
			ftp.changeWorkingDirectory(destinationFolder);
			status.info(ftp.getReplyString());
			status.info(ftp.getSystemName());
			ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
			if (ftp.isConnected()) {
				FTPFile files[] = ftp.listFiles();
				if (files.length > 0) {
					if (!files[0].hasPermission(FTPFile.USER_ACCESS, FTPFile.WRITE_PERMISSION) ) {
						status.info("Error de escritura en el servidor.");
					}
				}
				ftpDir(source, ftp, destinationFolder);
				status.info("Publicacion finalizada.");
			} else {
				status.info("No hubo conexion con el servidor.");
				error = false;
			}
		} catch (Throwable th) {
			status.error("FTP Error. Se produjo un error durante la conexion al FTP, si el error persite consulte con su administrador.");
			error = false;
		} finally {
			ftp.logout();
			status.info("Logout.");
			ftp.disconnect();
			status.info("Desconectado.");
			return error;
		}
	}

	public static String invalidFolder[] = {"ckfinder", "_thumbs"};
	
	public static void ftpDir(File ftpDir, FTPClient fc, String breadCrum) {
		GeneratorStatusController status = (GeneratorStatusController)AonUtil.getRegisteredBean(GENERATOR_STATUS);
		try {
			String[] dirList = ftpDir.list();
			for (int i = 0; i < dirList.length; i++) {
				File f = new File(ftpDir, dirList[i]);
				if (!f.getName().equals(config.getDomain() + ".zip")) {
					status.info("Subiendo archivo: /" + f.getName());
					if (f.isDirectory()) {
						if (isValidFolder(f.getName())) { 
							fc.makeDirectory(breadCrum + "/" + f.getName());
							ftpDir(f, fc, breadCrum + "/" + f.getName());
						}
						continue;
					}
					FileInputStream fis = null;
					try{
						fis= new FileInputStream(f);
						fc.storeFile(breadCrum + "/" + f.getName(), fis);
					}catch (Throwable th){
						IOUtils.closeQuietly(fis);
					}
				}
			}
		} catch (Throwable th) {
			AonUtil.addErrorMessage(th.getMessage());
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
