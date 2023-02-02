package com.esferalia.aon.seres.ftp;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.apache.commons.net.ftp.FTPFile;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SeresSftpConnectionProvider implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	public static boolean checkLogin(String server, Integer port, String user,
			String passwd) throws JSchException {
		JSch jsch = new JSch();
	    Session jschSession = jsch.getSession(user, server);
	    jschSession.setPassword(passwd);
	    jschSession.connect();	    
	    
		return true;
	}
	
	public static Session connect(String server, Integer port, String user,
			String passwd) throws JSchException {
		JSch jsch = new JSch();
	    Session jschSession = jsch.getSession(user, server);
	    jschSession.setPassword(passwd);
	    jschSession.connect();
	    return jschSession;
	}
	
	
	
	static class StoreResult {
		boolean completed;
		String message;
	}
	
	public static boolean storeFile(String remotePath, String fileName,
			InputStream localInputStream, String server, Integer port,
			String user, String passwd) throws JSchException, SftpException {
		Session session = connect(server, port, user, passwd);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		channel.cd(remotePath);		
		channel.put(localInputStream, fileName);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}
	
	public static byte[] retrieveFile(String remotePath, String remoteFile,
			String server, Integer port, String user, String passwd)
			throws SftpException, JSchException {
		Session session = connect(server, port, user, passwd);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		channel.cd(remotePath);
		ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
		channel.get(remoteFile, localOutputStream);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return localOutputStream.toByteArray();
	}
		
	public static List<String> retrieveDirectoryList(String remotePath,
			String server, Integer port, String user, String passwd)
			throws JSchException, SftpException {
		Session session = connect(server, port, user, passwd);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
	
		channel.connect();
		channel.cd(remotePath);
		Vector<String> vector = channel.ls(remotePath);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return vector;
	}
	
	public static List<FtpFile> retrieveFileList(String remotePath, Date start,
			Date end, String server, Integer port, String user, String passwd)
			throws FtpLoginException, FtpException {
		FTPFile file = new FTPFile();
		
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
					throws SftpException, JSchException {
		Session session = connect(server, port, user, passwd);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		channel.rm(remotePath);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}
	
	
	
}