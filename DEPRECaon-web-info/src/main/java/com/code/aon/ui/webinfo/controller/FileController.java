package com.code.aon.ui.webinfo.controller;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.LengthValidator;

import org.apache.commons.lang.StringUtils;
import org.richfaces.event.UploadEvent;
import org.richfaces.model.UploadItem;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class FileController extends BasicController {

	private long maximumSize = -1;
	
	public FileController() {
		this.maximumSize = -1;
	}

	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = 524288;
	}

	public IAttachment getAttachment() {
		return (IAttachment) getTo();
	}
	
	public void uploadListener(UploadEvent event) throws IOException{
		UploadItem item = event.getUploadItem();
		byte[] data = item.getData();
		long size = data.length;
		String fileName = item.getFileName();
		String contentType = item.getContentType();
		MimeType mimeType = MimeType.get(contentType);
		System.out.println("-----------========== MAX: " + maximumSize + " SIZE: " + size);
		if ( (maximumSize != -1) && (size > maximumSize) ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
			FacesMessage message = AonUtil.getMessage( ctx, LengthValidator.MAXIMUM_MESSAGE_ID, new Object[]{maximumSize, fileName} );
			AonUtil.addErrorMessage(message.getDetail());
		}
		else {
			updateFile( fileName, data, mimeType);
		}
	}

	public void updateFile( String fileName, byte[] data, MimeType mimeType ) {
		IAttachment attach = getAttachment();
		attach.setData( data );
		attach.setMimeType(mimeType);
		if ( StringUtils.isEmpty(attach.getDescription()) ) {
			int index = fileName.lastIndexOf(".");
			attach.setDescription((index==-1?fileName:fileName.substring(0, index)));
		}
	}
	
	public void fileRemove( ActionEvent ent ) throws IOException {
		IAttachment attach = getAttachment();
		if ( attach != null ) {
			attach.setData( null );
			attach.setMimeType( null );
		}
	}

	public byte[] getFileData() throws IOException {
    	IAttachment attach = getAttachment();
		if ( attach != null ) {
			return attach.getData();
		}
    	return null; 
    }
    
    public String getMimeType() {
    	IAttachment attach = getAttachment();
		if ( (attach != null) && (attach.getMimeType() != null) ) {
			Random r = new Random();
			String mimetype = attach.getMimeType().getName()+r.nextInt();
			return mimetype;
		}
    	return MimeType.MIME_JPEG.getName(); 
    }
    
    public void paint(OutputStream out, Object data) throws IOException {
    	if ( getFileData() != null ) {
    		System.out.println(getFileData());
    		out.write( getFileData() );
    	}
    }

}