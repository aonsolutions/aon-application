package com.code.aon.webinfo.controller;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.validator.LengthValidator;

import org.apache.commons.lang.StringUtils;
import org.apache.myfaces.custom.fileupload.UploadedFile;

import com.code.aon.common.IAttachment;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class TomahawkFileController extends BasicController {

	private UploadedFile inputFile;
	
	private long maximumSize;
	
	public TomahawkFileController() {
		this.maximumSize = -1;
	}

	public UploadedFile getInputFile() {
		return inputFile;
	}

	public void setInputFile(UploadedFile inputFile) {
		this.inputFile = inputFile;
	}
	
	public long getMaximumSize() {
		return maximumSize;
	}

	public void setMaximumSize(long maximumSize) {
		this.maximumSize = maximumSize;
	}

	public IAttachment getAttachment() {
		return (IAttachment) getTo();
	}
	
	public void fileUploaded( ActionEvent ent ) throws IOException {
		if ( this.inputFile!= null ) {
			long size = this.inputFile.getSize();
			String fileName = new File( inputFile.getName() ).getName();
			if ( (maximumSize != -1) && (size > maximumSize) ) {
				FacesContext ctx = FacesContext.getCurrentInstance();
				FacesMessage message = AonUtil.getMessage( ctx,
						LengthValidator.MAXIMUM_MESSAGE_ID, new Object[]{maximumSize, fileName} );
				ctx.addMessage(AonUtil.AON_ERROR, message);
			} else {
				String contentType = this.inputFile.getContentType();
				MimeType mimeType = MimeType.get(contentType);
				updateFile( fileName, this.inputFile.getBytes(), mimeType);
			}
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
			return attach.getMimeType().getName();
		}
    	return MimeType.MIME_JPEG.getName(); 
    }
    
    public void paint(OutputStream out, Object data) throws IOException {
    	if ( getFileData() != null ) {
    		out.write( getFileData() );
    	}
    }

}