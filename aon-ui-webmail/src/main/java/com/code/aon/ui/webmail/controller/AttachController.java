package com.code.aon.ui.webmail.controller;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.faces.model.SelectItem;
import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonAttachment;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;

public class AttachController {

	private AonMessage aonMessage;
	
	private List<AonAttachment> aonList;
	
	private int attachPos;
	
	public int getAttachPos() {
		return attachPos;
	}

	public void setAttachPos(int attachPos) {
		this.attachPos = attachPos;
	}
	
    public AonAttachment getAttach() {
   		return aonList.get(attachPos);
	}

	public List<SelectItem> getAttachmentsDrop() throws ManagerBeanException, ExpressionException, WebmailException {
		List<SelectItem> types = new LinkedList<SelectItem>();
		for (AonAttachment aonAttachment : getAttachments()) {
			String name = aonAttachment.getFileName()+" ("+aonAttachment.getFileSize()+")";
			types.add(new SelectItem(aonAttachment.getPosition()-1, name));
		}
		return types;
	}

	public List<AonAttachment> getAttachments() throws WebmailException{
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	AonMessage currentMessage = messageController.getMessage();
    	if (this.aonMessage != currentMessage){
    		this.aonMessage = currentMessage;
        	aonList = aonMessage.getAttachements();
        	attachPos=0;
    	}
    	return aonList;
    }

    public void getAttachment(String pos,HttpServletResponse response) throws MessagingException{
    	int position = Integer.parseInt(pos);
    	AonAttachment aonAttachment = aonList.get(position-1);
    	aonAttachment.download(response);
    }

    public void getZippedAttachments(HttpServletResponse response) throws MessagingException, WebmailException{
        try {
	        String outFilename = "attachments.zip";
			response.setContentType("application/zip");
			response.setHeader("content-disposition", "attachment;filename=\""
					+ outFilename + "\"");
			byte[] data = new byte[1024];

            ZipOutputStream out = new ZipOutputStream(response.getOutputStream());

    		for (AonAttachment aonAttachment : aonList) {
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

    		out.flush();
            out.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
    }

    public boolean isAttachment() throws WebmailException{
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	AonMessage aonMessage = messageController.getMessage();
    	return aonMessage.isAttachment() && (aonMessage.getAttachements().size()>0);
    }
    
}
