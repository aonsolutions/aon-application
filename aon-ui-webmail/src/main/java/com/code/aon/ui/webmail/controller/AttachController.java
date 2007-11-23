package com.code.aon.ui.webmail.controller;

import java.util.LinkedList;
import java.util.List;

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
			String name = aonAttachment.getFileName()+" &lt;"+aonAttachment.getFileSize()+"&gt;";
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
    
    public boolean isAttachment() throws WebmailException{
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	AonMessage aonMessage = messageController.getMessage();
    	return aonMessage.isAttachment() && (aonMessage.getAttachements().size()>0);
    }
    
}
