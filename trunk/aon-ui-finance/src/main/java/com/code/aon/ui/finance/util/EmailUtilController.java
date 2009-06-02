package com.code.aon.ui.finance.util;

import java.io.UnsupportedEncodingException;

import javax.faces.event.AbortProcessingException;
import javax.mail.Address;
import javax.mail.internet.InternetAddress;

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webmail.EmailSender;
import com.code.aon.webmail.MailAccount;
import com.code.aon.webmail.WebmailUtil;

public class EmailUtilController {
	
	private EmailSender sender;
	
	private MailAccount getDefaultMailAccount() {
		AuthPrincipal user = UserUtils.getInstance().getPrincipal();		
		String domain = user.getDomain();
		String login = user.getShortName();
		MailAccount mailAccount;
		try {
			mailAccount = WebmailUtil.getDefaultAccount(domain,login);
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage( "El usuario " + login + " no tiene definida ninguna cuenta de correo" );
			throw new AbortProcessingException( e.getMessage(), e);
		}
		return mailAccount;
	}
	
	private String getUserName() {
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
		return loggedUser.getLoggedUserName();
	}

	public EmailSender getEmailSender() throws UnsupportedEncodingException {
		if ( this.sender == null ) {
			MailAccount mailAccount = getDefaultMailAccount();
			Address from = new InternetAddress( mailAccount.getEmail(), getUserName() );
			this.sender = new EmailSender( from, mailAccount );			
		}
		return this.sender;
	}
	
}
