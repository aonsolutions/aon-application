package com.code.aon.ui.webmail.controller;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.util.DownloadUtil;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonAttachment;
import com.code.aon.webmail.bean.AonMessage;

public class AttachController {
	
	private static final String ATTACHMENTS_FILE_NAME = "attachments";

	private final static Logger LOGGER = LoggerFactory.getLogger(AttachController.class);

	private AonMessage aonMessage;
	
	private List<AonAttachment> attachments;
	
	private int attachPos;
	
	public int getAttachPos() {
		return attachPos;
	}

	public void setAttachPos(int attachPos) {
		this.attachPos = attachPos;
	}
	
    public AonAttachment getAttach() {
   		return attachments.get(attachPos);
	}

	public List<SelectItem> getAttachmentsDrop() throws ManagerBeanException, ExpressionException, WebmailException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		for (AonAttachment aonAttachment : getAttachments()) {
			String name = aonAttachment.getFileName()+" ("+aonAttachment.getFileSize()+")";
			types.add(new SelectItem(aonAttachment.getPosition(), name));
		}
		return types;
	}

	public void update( AonMessage message ) {
		this.aonMessage = message;
    	this.attachPos = 0;
   		this.attachments = this.aonMessage.getAttachements();
	}
	
	public List<AonAttachment> getAttachments() {
    	return attachments;
    }

	private void downloadAttachment( AonAttachment attach ) {
		InputStream in = null;
		try {
			in = new BufferedInputStream(attach.getInputStream());
			// El size del BodyPart no siempre es correcto por eso no se indica
			// ya que puede dar problemas en la descarga si es mayor del real
			DownloadUtil.downloadAttachment(attach.getFileName(), attach.getMimeType(), in, -1); 
		} finally {
			IOUtils.closeQuietly(in);
		}		
	}

    public void downloadAttachment( ActionEvent event ) throws MessagingException, WebmailException {
        FacesContext context = FacesContext.getCurrentInstance();
		String index = context.getExternalContext().getRequestParameterMap().get("index");
		if ( NumberUtils.isNumber(index) ) {
	    	AonAttachment aonAttachment = attachments.get( Integer.parseInt(index) );
	        downloadAttachment( aonAttachment );    				
		}
    }
    
    public void downloadZippedAttachments( ActionEvent event ) {
		HttpServletResponse response = null;
		OutputStream out = null;
		File tempFile = null;
        try {
			tempFile = File.createTempFile(ATTACHMENTS_FILE_NAME, "." + MimeType.MIME_ZIP.getExtension());
			OutputStream fileOut = new BufferedOutputStream( new FileOutputStream(tempFile) );
			ZipOutputStream zipOut = new ZipOutputStream(fileOut);
			Set<String> fileNames = new HashSet<String>();
			for (AonAttachment aonAttachment : attachments) {
                InputStream in = aonAttachment.getPart().getInputStream();
                                
            	String filename = aonAttachment.getFileName();
           		for( int i = 0; fileNames.contains(filename); i++ ) {
           			filename = aonAttachment.getFileName() + "("+i+")";
            	}
           		zipOut.putNextEntry(new ZipEntry(filename));

            	IOUtils.copy( in, zipOut );

            	zipOut.closeEntry();
                in.close();
                
           		fileNames.add(filename);                
            }
			zipOut.close();

			long size = tempFile.length();
        	response = DownloadUtil.getResponse();
        	out = DownloadUtil.initDownload(response, ATTACHMENTS_FILE_NAME, MimeType.MIME_ZIP, size);
	        // Unico Content-Type que soporta Firefox para ficheros comprimidos
	        response.setContentType("application/x-zip-compressed");
	        if ( AonUtil.isMSIE() ) {
	        	response.setHeader("Content-Encoding", "deflate");
	        }			
            InputStream fileIn = new BufferedInputStream( new FileInputStream(tempFile) );
            IOUtils.copy( fileIn, out );
            IOUtils.closeQuietly(fileIn);
		} catch (IOException e) {
			LOGGER.error( e.getMessage(), e );
		} catch (MessagingException e) {
			LOGGER.error( e.getMessage(), e );
		} finally {
			DownloadUtil.finishDownload(response, out);
			if ( (tempFile != null) && (tempFile.exists()) ) {
	    		tempFile.delete();
			}
		}
    }

    public boolean isAttachment() throws WebmailException{
    	return ! this.attachments.isEmpty();
    }
    
}
