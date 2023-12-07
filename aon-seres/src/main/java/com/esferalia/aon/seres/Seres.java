package com.esferalia.aon.seres;

import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Vector;

import org.apache.commons.io.output.ByteArrayOutputStream;

import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class Seres implements Serializable {

	private static final long serialVersionUID = 1L;
	
	Domain domain;
	String login;
	SeresInfo info;
	
	public Seres(Domain domain, String login, SeresInfo info) {
		this.domain = domain;
		this.login = login;
		this.info = info;
	}
	
	public Seres(SeresInfo info) {
		this.info = info;
	}
	
	public SeresInfo getInfo() {
		return info;
	}
	
	public Seres setInfo(SeresInfo info) {
		this.info = info;
		return this;
	}
	
	public Domain getDomain() {
		return domain;
	}
	
	public Seres setDomain(Domain domain) {
		this.domain = domain;
		return this;
	}
	
	public String getLogin() {
		return login;
	}
	
	public Seres setLogin(String login) {
		this.login = login;
		return this;
	}
	
	public boolean checkLogin() throws JSchException {
		Session session = connect();
		session.disconnect();
		return true;
	}
	
	public Session connect() throws JSchException {
		java.util.Properties config = new java.util.Properties(); 
		config.put("StrictHostKeyChecking", "no");
		
		JSch jsch = new JSch();
	    Session jschSession = jsch.getSession(getInfo().getUser(), getInfo().getServer(), getInfo().getPort());
	    jschSession.setPassword(getInfo().getPassword());
	    jschSession.setConfig(config);
	    jschSession.connect();
	    return jschSession;
	}
	
	public boolean storeFile(String fileName, InputStream localInputStream) throws JSchException, SftpException {
		Session session = connect();
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		if(getInfo().getSeresPath() != null)
			channel.cd(getInfo().getSeresPath().getPath());		
		channel.put(localInputStream, fileName);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}
	
	public byte[] retrieveFile(SeresInfo info, String remoteFile)
			throws SftpException, JSchException {
		Session session = connect();
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		
		channel.connect();
		if(getInfo().getSeresPath() != null)
			channel.cd(info.getSeresPath().getPath());
		ByteArrayOutputStream localOutputStream = new ByteArrayOutputStream();
		channel.get(remoteFile, localOutputStream);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return localOutputStream.toByteArray();
	}
		
	public List<ChannelSftp.LsEntry> retrieveDirectoryList()
			throws JSchException, SftpException {
		Session session = connect();
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
	
		channel.connect();
		if(getInfo().getSeresPath() != null)
			channel.cd(getInfo().getSeresPath().getPath());	
		Vector<ChannelSftp.LsEntry> vector =  channel.ls(getInfo().getSeresPath().getPath());
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return vector;
	}
	
	public boolean deleteFile(String fileName)
					throws SftpException, JSchException {
		Session session = connect();
		ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
		channel.connect();
		channel.rm(fileName);
		channel.exit();
		channel.disconnect();
		session.disconnect();
		return true;
	}
	
}
