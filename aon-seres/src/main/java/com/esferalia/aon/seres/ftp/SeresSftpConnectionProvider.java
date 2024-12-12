package com.esferalia.aon.seres.ftp;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.esferalia.aon.occam.api.model.seres.SeresInfo;
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
	
	
	public static boolean checkLogin(SeresInfo info) throws JSchException {
		Session session = connect(info);
		session.disconnect();
		return true;
	}
	
	public static Session connect(SeresInfo info) throws JSchException {
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		
		JSch jsch = new JSch();
	    Session jschSession = jsch.getSession(info.getUser(), info.getServer(), info.getPort());
	    jschSession.setPassword(info.getPassword());
	    jschSession.setConfig(config);
	    jschSession.connect();
	    return jschSession;
	}
	
	public static boolean storeFile(SeresInfo info, String fileName, InputStream localInputStream) throws JSchException, SftpException {
		Session session = connect(info);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		channel.cd(info.getSeresPath().getPath());		
		channel.put(localInputStream, fileName);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}
	
	public static byte[] retrieveFile(SeresInfo info, String remoteFile) throws SftpException, JSchException {
		Session session = connect(info);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		channel.cd(info.getSeresPath().getPath());
		ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
		channel.get(remoteFile, localOutputStream);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return localOutputStream.toByteArray();
	}
		
	public static List<ChannelSftp.LsEntry> retrieveDirectoryList(SeresInfo info) throws JSchException, SftpException {
		Session session = connect(info);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
	
		channel.connect();
		channel.cd(info.getSeresPath().getPath());	
		Vector<ChannelSftp.LsEntry> vector =  channel.ls(info.getSeresPath().getPath());
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return vector;
	}
	
	public static boolean deleteFile(SeresInfo info, String fileName) throws SftpException, JSchException {
		Session session = connect(info);
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		channel.rm(fileName);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}

}