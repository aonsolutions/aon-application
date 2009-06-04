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
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.WebmailException;
import com.code.aon.webmail.bean.AonAttachment;
import com.code.aon.webmail.bean.AonMessage;

public class AttachController {
	
	private static final Logger LOGGER = Logger.getLogger(AttachController.class.getName());

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
    	try {
    		this.attachments = this.aonMessage.getAttachements();
		} catch (WebmailException e) {
    		AonUtil.addErrorMessage(e.getMessage());
    		throw new AbortProcessingException(e);
		}
	}
	
	public List<AonAttachment> getAttachments() {
    	return attachments;
    }

	private void download(AonAttachment attachment, HttpServletResponse response) {
		try {
			String filename = attachment.getFileName();
			response.setContentType(attachment.getPart().getContentType());
			response.setHeader("Content-disposition", "attachment;filename=\""
					+ filename + "\"");
			ServletOutputStream sos = response.getOutputStream();
			BufferedInputStream bis = new BufferedInputStream(attachment.getPart().getInputStream());
			int size = IOUtils.copy( bis, sos );
			if ( size > 0 ) {
				response.setHeader("Content-Length", String.valueOf(size));
			}
			bis.close();
			sos.close();
			response.flushBuffer();
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} catch (MessagingException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
	}
	
    public void getAttachment(String pos,HttpServletResponse response) throws MessagingException{
    	int position = Integer.parseInt(pos);
    	AonAttachment aonAttachment = attachments.get(position);
    	download(aonAttachment, response);
    }

    public void downloadAttachment( ActionEvent event ) throws MessagingException, WebmailException {
        FacesContext context = FacesContext.getCurrentInstance();
		String index = context.getExternalContext().getRequestParameterMap().get("index");
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        getAttachment( index, response);
        context.responseComplete();    	
    }
    
    public void downloadZippedAttachments( ActionEvent event ) throws MessagingException, WebmailException {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletResponse response = (HttpServletResponse) context.getExternalContext().getResponse();
        getZippedAttachments(response);
        context.responseComplete();    	
    }
    
    private void getZippedAttachments(HttpServletResponse response) throws MessagingException, WebmailException{
        try {
	        String outFilename = "attachments.zip";
	        response.setContentType("application/zip");
	        response.setHeader("Content-Encoding", "deflate");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ outFilename + "\"");

			File tempFile = File.createTempFile(outFilename, ".zip");
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
            if ( size > 0 ) {
                response.setHeader("Content-Length", String.valueOf(size));	
            }
            ServletOutputStream sos = response.getOutputStream();
            InputStream fileIn = new BufferedInputStream( new FileInputStream(tempFile) );
            IOUtils.copy( fileIn, sos );
            fileIn.close();
            sos.close();
    		response.flushBuffer();
    		tempFile.delete();
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		} catch (MessagingException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
    }

    public boolean isAttachment() throws WebmailException{
    	return ! this.attachments.isEmpty();
    }
    
}
