package com.code.aon.ui.ecommerce.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.AbortProcessingException;
import javax.mail.internet.InternetAddress;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;
import com.code.aon.webmail.bean.AonMessage;
import com.code.aon.webmail.bean.AonServer;

public class EmailParentController {
	
	private static final Logger LOGGER = Logger.getLogger(EmailParentController.class.getName());
	
	public void email(String subject, String from, String to, String content) {
		String domain = "localhost";
		String login = "admin";
		MailAccount mailAccount;
		try {
			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage( "El usuario " + login + " no tiene definida ninguna cuenta de correo" );
			throw new AbortProcessingException( e.getMessage(), e);
		}
		try {
			AonServer server = new AonServer(mailAccount);
			server.connect();
			String username = from;  
			AonMessage aonMessage = server.createAonMessage(from, username);
			InternetAddress iafrom = new InternetAddress(from, username);
			aonMessage.setSender(iafrom);
			aonMessage.setRecipientsTo(to);
			aonMessage.setSubject(subject);
			aonMessage.setContent(content.toString());
			server.sendMessage(aonMessage);
			server.disconnect();
		} catch (Throwable e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
			AonUtil.addErrorMessage( e.getMessage() );
			throw new AbortProcessingException( e.getMessage(), e);
		}
	}
}