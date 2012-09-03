/**
 * 
 */
package com.code.aon.ui.common.controller;

import static com.code.aon.common.util.BeanServerUtil.SESSION_MANAGER;

import java.text.MessageFormat;
import java.util.Locale;

import javax.management.MBeanServer;
import javax.naming.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.util.BeanServerUtil;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.util.AonUtil;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 21/05/2007
 *
 */
public class FailedLogin implements ILdapConstants, IAonObjectClasses {

    /** Obtains the SessionFilter Logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(FailedLogin.class);
	
	private final static String GET_LASTLOGIN_EXCEPTION = "getLastLoginException";
	
	private static final int DEFAULT_STATUS = 10;
	
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
	
	private String getLdapMessage( int status, String language ) {
		if (! AonUtil.isSkipLdap() ) {
			BasicLdap ldap = new BasicLdap();		
			Name dn = NameResolver.getMessageDN(status, language);
			if (! ldap.exists(dn, MESSAGE) ) {
				dn = NameResolver.getMessageDN(status);
				if (! ldap.exists(dn, MESSAGE) ) {
					dn = NameResolver.getMessageDN(DEFAULT_STATUS, language);
					if (! ldap.exists(dn, MESSAGE) ) {
						dn = NameResolver.getMessageDN(DEFAULT_STATUS);
						if (! ldap.exists(dn, MESSAGE) ) {
							dn = null;
						}
					}
				}
			}
			if ( dn != null ) {
				Entry entry = ldap.get(dn, MESSAGE, MESSAGE_ATTRIBUTE);
				if ( entry != null ) {
					return entry.getAsString(MESSAGE_ATTRIBUTE);	
				} else {
					LOGGER.error( "Error retrieving message from LDAP: {}", status );	
				}
			}
		}
		return null;
	}
	
	private String getMessageString( AuthenticationLoginException e ) {
		String message = null;
		Locale locale = AonUtil.getCurrentLocale();
		String errorId = e.getMessage();
		if ( errorId.startsWith(LOGIN_ERROR_PREFFIX) ) {
			int status = Integer.valueOf(errorId.substring(LOGIN_ERROR_PREFFIX.length()));
			message = getLdapMessage(status, locale.getLanguage());
			if ( message == null ) {
				errorId = LOGIN_ERROR_DEFAULT;
			}
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
