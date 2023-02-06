package com.esferalia.aon.seres.ftp;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.net.ftp.FTPFile;

import com.esferalia.aon.occam.api.model.seres.SeresInfo;
import com.esferalia.aon.occam.api.model.seres.SeresPath;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;

public class SeresFtpConnectionProvider implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	
	
	public static boolean checkLogin(String server, Integer port, String user,
			String passwd) throws JSchException {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd);
		return SeresSftpConnectionProvider.checkLogin(info);
	}
	
	static class StoreResult {
		boolean completed;
		String message;
	}
	public static boolean storeFile(String remotePath, String fileName,
			InputStream localInputStream, String server, Integer port,
			String user, String passwd) throws JSchException, SftpException  {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd)
				.setSeresPath(SeresPath.safeValueOf(remotePath));
		SeresSftpConnectionProvider.storeFile(info, fileName, localInputStream);
		return true;
	}
	
	public static byte[] retrieveFile(String remotePath, String remoteFile,
			String server, Integer port, String user, String passwd) throws SftpException, JSchException {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd)
				.setSeresPath(SeresPath.safeValueOf(remotePath));
		
		return SeresSftpConnectionProvider.retrieveFile(info, remoteFile);
	}
	
	public static List<String> retrieveDirectoryList(String remotePath,
			String server, Integer port, String user, String passwd)
			throws JSchException, SftpException {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd)
				.setSeresPath(SeresPath.safeValueOf(remotePath));
		
		List<LsEntry> list = SeresSftpConnectionProvider.retrieveDirectoryList(info);
		return list.stream()
				.filter(f -> !f.getFilename().equals(".") && !f.getFilename().equals(".."))
				.map(r -> remotePath + "/" + r.getFilename())
				.collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static List<FtpFile> retrieveFileList(String remotePath, Date start,
			Date end, String server, Integer port, String user, String passwd)
			throws JSchException, SftpException {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd)
				.setSeresPath(SeresPath.safeValueOf(remotePath));
		Date start2 = AonDateUtils.getDateWithoutTime(start);
		List<LsEntry> list = SeresSftpConnectionProvider.retrieveDirectoryList(info);

		return list.stream()
		.filter(f -> !f.getFilename().equals(".") && !f.getFilename().equals("..") 
			&& start2.before(new Date(f.getAttrs().getMTime() * 1000L))
			&& end.after(new Date(f.getAttrs().getMTime() * 1000L)))
		.map(r -> {
			FTPFile file = new FTPFile();
			file.setName(r.getFilename());
		
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(r.getAttrs().getMTime() * 1000L);
			file.setTimestamp(calendar);
			
			return new FtpFile(file);
		}).collect(Collectors.toCollection(LinkedList::new));
	}
	
	public static boolean deleteFile(String remotePath,
			String server, Integer port, String user, String passwd)
					throws SftpException, JSchException {
		SeresInfo info = new SeresInfo()
				.setServer(server)
				.setUser(user)
				.setPassword(passwd);
		SeresSftpConnectionProvider.deleteFile(info, remotePath);
		return true;
	}
	
}







