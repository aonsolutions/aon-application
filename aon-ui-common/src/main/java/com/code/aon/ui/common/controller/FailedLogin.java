package com.code.aon.ui.common.controller;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import jakarta.servlet.ServletRequest;

import com.code.aon.jaas.auth.IConstants;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.ui.common.ICommonMessages;

public class FailedLogin {

	public String getMessage(ServletRequest request) {
		AuthenticationLoginException exp = (AuthenticationLoginException) 
				request.getAttribute(IConstants.AON_LOGIN_EXCEPTION);
		if ( exp != null ) {
			Locale locale = request.getLocale();
			ResourceBundle bundle = ResourceBundle.getBundle(ICommonMessages.BUNDLE_RESOURCE, locale);
			String message = bundle.getString(exp.getMessage());
			MessageFormat messageFormat = new MessageFormat( message );
			Object[] arguments;
			if ( exp.getArg().getClass().isArray() ) {
				arguments = (Object[]) exp.getArg();
			} else {
				arguments = new Object[] { exp.getArg() }; 
			}
			return messageFormat.format(arguments);
		}
		return null;
	}
}

