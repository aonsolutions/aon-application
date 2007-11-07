package com.code.aon.ui.webmail.controller;

import java.util.List;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.ui.util.AonUtil;
import com.code.aon.ui.webmail.bean.AonAttachment;
import com.code.aon.ui.webmail.bean.AonConstants;
import com.code.aon.ui.webmail.bean.AonMessage;
import com.code.aon.ui.webmail.exception.WebmailException;

public class AttachController {

	private List<AonAttachment> aonList;
	
    public List<AonAttachment> getAttachments() throws WebmailException{
    	MessageController messageController = (MessageController)AonUtil.getRegisteredBean(AonConstants.BEAN_MESSAGE);
    	AonMessage aonMessage = messageController.getMessage();
    	aonList = aonMessage.getAttachements();
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
    	return aonMessage.isAttachment();
    }
    
}
