package com.code.aon.ui.google.apis.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import org.richfaces.event.UploadEvent;

import com.code.aon.AonVersion;
import com.code.aon.google.apis.DriveFile;
import com.code.aon.google.apis.DriveUtils;
import com.code.aon.oauth2.google.GoogleUser;
import com.code.aon.oauth2.sessionInfo.SessionInfo;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.model.FileList;



public class GoogleDriveController implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String beanName;

	private DriveFile upload;
	
	public boolean google= isGoogle();
	
	public static Boolean gconnection;
	
	public static Drive dconnection;
	
	public static GoogleUser uconnection;
	
	// pasarela a gwt Document.

	public Drive getClientSession(){
		Drive drive=null;
		
		GoogleUser user = null;
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("Oauth2callback.email");
		String email= (String) session;
		
		String domain= AonUtil.getDomainName();
		String username=AonUtil.getAuthPrincipal().getShortName();
		if (SessionInfo.table.containsKey(domain) && SessionInfo.table.get(domain).getUsers().containsKey(username)){
			drive= SessionInfo.table.get(domain).getUsers().get(username).getGoogleUsers().get(email).getDrive();
			user = SessionInfo.table.get(domain).getUsers().get(username).getGoogleUsers().get(email);
		}
		dconnection= drive;
		uconnection = user;
		return drive;
	}
	
	public boolean isGoogle(){
		/*HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		Object session = (HttpSession) request.getSession().getAttribute("isGoogle");
		*/
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Object session=((HttpSession) ec.getSession(false)).getAttribute("isGoogle");
		if (session == null){
			gconnection= false;
			return false;
		}
		else gconnection =  (Boolean) session;
		return (Boolean) session;
		/*if(getClientSession()!=null) return true;
		else return false;
		*/
	}
	
	public DriveFile [] getFiles() throws IOException{
		
		Drive drive=getClientSession();
		
		FileList files = DriveUtils.getRootFiles(drive);
		DriveFile[] driveFiles= new DriveFile[files.getFiles().size()];
		for(int i=0;i<driveFiles.length;i++){
			driveFiles[i]=new DriveFile(files.getFiles().get(i).getId(),files.getFiles().get(i).getWebViewLink()
					,files.getFiles().get(i).getName(),files.getFiles().get(i).getMimeType());
			
			System.out.println(files.getFiles().get(i).getMimeType());
		}
		
		return driveFiles; //DriveUtils.getFiles(drive);//new DriveFile[]{new DriveFile("_1", "Hello"), new DriveFile("_2", "World!!!") };
	}


	public void onAccept(ActionEvent event) {
		System.out.println("onUpload");
		upload = new DriveFile("",null, "","");
	}

	public void onUpload(ActionEvent event) {
		System.out.println("onUpload");
		upload = new DriveFile("",null, "","");
	}
	
	public void onDownload(ActionEvent event){
		System.out.println("onDownload");
		
		Drive drive=getClientSession();
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		String fileId = ctx.getExternalContext().getRequestParameterMap().get("id");
	}
	
	public void onDelete(ActionEvent event) throws IOException{
		System.out.println("onDelete");
		
		Drive drive=getClientSession();
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		String fileId = ctx.getExternalContext().getRequestParameterMap().get("id");
		
		System.out.println(fileId);		
		DriveUtils.deleteFile(drive, fileId);;
		
	}
	
	public void onSearch(ActionEvent event) {
		System.out.println("onSearch");
	}
	
	/**
	 * File uploaded.
	 * 
	 * @param event the event
	 * @throws IOException 
	 */
	public void fileUploaded(UploadEvent event) throws IOException {
		Drive drive=getClientSession();
		DriveFile file=new DriveFile("","", event.getUploadItem().getFileName(), event.getUploadItem().getContentType());
		DriveUtils.insertFile(drive,event.getUploadItem().getFile(), file);
	}
	
	public void fileDownloaded(ActionEvent event) throws IOException {
		Drive drive=getClientSession();
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse)ctx.getExternalContext().getResponse();
		String fileId = ctx.getExternalContext().getRequestParameterMap().get("id");

		System.out.println(fileId);
		com.google.api.services.drive.model.File driveFile = drive.files().get(fileId).execute();
		InputStream data = DriveUtils.downloadFile(drive, driveFile);
		
		response.setContentType(driveFile.getMimeType());
		response.setHeader("Content-Disposition", "attachment; filename=\"" + driveFile.getName() + "\";");
		response.getOutputStream().write(AonIOUtils.toByteArray(data));
		response.flushBuffer();
		ctx.responseComplete();
		
	}
	

	public DriveFile getUpload() {
		return upload;
	}
	
	public String getBeanName() {
		return beanName;
	}
	
	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}
	
}
