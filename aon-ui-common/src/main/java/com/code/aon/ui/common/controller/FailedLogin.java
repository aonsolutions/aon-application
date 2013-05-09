/**
 * 
 */
package com.code.aon.ui.common.controller;

import static com.code.aon.common.util.BeanServerUtil.SESSION_MANAGER;

import java.text.MessageFormat;

import javax.management.MBeanServer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.BeanServerUtil;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 21/05/2007
 *
 */
public class FailedLogin {

    /** Obtains the SessionFilter Logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(FailedLogin.class);
	
	private final static String GET_LASTLOGIN_EXCEPTION = "getLastLoginException";
	
	private static final String LOGIN_ERROR_PREFFIX = "aon_login_error_";
	
	private static final String LOGIN_ERROR_DEFAULT = "aon_login_error_default";

	private String message;

	public FailedLogin() {
		AuthenticationLoginException e = getLoginException(); 
		if ( e != null ) {
			setMessage( getMessage(e) );
		}
	}
	
    private AuthenticationLoginException getLoginException() {
		Object[] params = { "" };
		String[] sig = { String.class.getName() }; 	
		try {
			MBeanServer server = BeanServerUtil.getMBeanServer();
			return (AuthenticationLoginException) server.invoke( SESSION_MANAGER, GET_LASTLOGIN_EXCEPTION, params, sig );
		} catch (Throwable e) {
			LOGGER.warn( e.getMessage(), e );
		} 
    	return null;
    }    
	
	private String getMessageString( AuthenticationLoginException e ) {
		String message = null;
		String errorId = e.getMessage();
		if ( errorId.startsWith(LOGIN_ERROR_PREFFIX) ) {
			errorId = LOGIN_ERROR_DEFAULT;
		}
		if ( message == null ) {
			message = AonUtil.getMessage( errorId );
		}
		return message;	
	}
	
	private String getMessage( AuthenticationLoginException e ) {
		MessageFormat messageFormat = new MessageFormat( getMessageString(e) );
		Object[] arguments;
		if ( e.getArg().getClass().isArray() ) {
			arguments = (Object[]) e.getArg();
		} else {
			arguments = new Object[] { e.getArg() }; 
		}
		return messageFormat.format(arguments);
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

}
