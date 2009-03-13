/**
 * 
 */
package com.code.aon.bridge.session;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.naming.Name;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.deployment.DeploymentException;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 21/05/2007
 *
 */
public class FailedLogin implements ILdapConstants, IAonObjectClasses {

	protected static final String AON_LAST_EXCEPTION_KEY = "AON_LAST_EXCEPTION_KEY";
	
    /** Obtains the SessionFilter Logger. */
	private static final Log LOGGER = LogFactory.getLog( FailedLogin.class.getName() );	
	
	private static final String LOGIN_ERROR_PREFFIX = "aon_login_error_";
	
	private static final String LOGIN_ERROR_DEFAULT = "aon_login_error_default";
	
	private static final int DEFAULT_STATUS = 10;

	private String message;

	public FailedLogin() throws DeploymentException {
        IConsoleAdmin console = Utils.getSecurityConsole();
		String oname = console.getAonSessionManagerName();
		AuthenticationLoginException e = 
			(AuthenticationLoginException) console.invoke( oname, IOperation.GET_LASTLOGIN_EXCEPTION, new Object[] {IConsoleAdmin.EMPTY_STRING}, new String[] {String.class.getName()} );
		if ( e != null ) {
			setMessage( getMessage(e) );
		}
	}

	/**
	 * This method gets called myfaces login implementation, using html frames.
	 * 
	 * @return
	 */
	public boolean isException() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = 
			(HttpServletRequest) ctx.getExternalContext().getRequest();
		AuthenticationLoginException e = 
			(AuthenticationLoginException) request.getAttribute( AON_LAST_EXCEPTION_KEY );
		if ( e != null ) {
			setMessage( getMessage(e) );
			return true;
		}
		return false;
	}
	
	private String getLdapMessage( int status, String language ) {
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
				LOGGER.error( "Error retrieving message from LDAP: " + status );	
			}
		}
		return null;
	}
	
	private String getMessageString( AuthenticationLoginException e ) {
		String message = null;
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String errorId = e.getMessage();
		if ( errorId.startsWith(LOGIN_ERROR_PREFFIX) ) {
			int status = Integer.valueOf(errorId.substring(LOGIN_ERROR_PREFFIX.length()));
			message = getLdapMessage(status, locale.getLanguage());
			if ( message == null ) {
				errorId = LOGIN_ERROR_DEFAULT;
			}
		}
		if ( message == null ) {
	        ResourceBundle bundle = ResourceBundle.getBundle( IOperation.MESSAGES_FILE, locale );
			message = bundle.getString( errorId );			
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
