package com.code.aon.ui.webmail.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonAttachment;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;

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

    public void getAttachment(String pos,HttpServletResponse response) throws MessagingException{
    	int position = Integer.parseInt(pos);
    	AonAttachment aonAttachment = attachments.get(position);
    	aonAttachment.download(response);
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
    
    public void getZippedAttachments(HttpServletResponse response) throws MessagingException, WebmailException{
        try {
	        String outFilename = "attachments.zip";
			response.setContentType("application/zip");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ outFilename + "\"");
			byte[] data = new byte[1024];

            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());

    		for (AonAttachment aonAttachment : attachments) {
                InputStream in = (InputStream)aonAttachment.getPart().getInputStream();
                
                
            	String filename = aonAttachment.getFileName();
            	boolean done = false;
            	int i = 0;
            	while (!done){
	                try{
	                	out.putNextEntry(new ZipEntry(filename+(i==0?"":"["+i+"]")));
	                	done = true;
	                }catch (Exception e) {
	                	++i;
					}
            	}

                int len;
                while ((len = in.read(data)) > 0) {
                    out.write(data, 0, len);
                }

                out.closeEntry();
                in.close();
            }

    		response.flushBuffer();
            out.close();
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
