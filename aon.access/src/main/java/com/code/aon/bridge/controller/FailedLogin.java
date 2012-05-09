/**
 * 
 */
package com.code.aon.bridge.controller;




import static com.code.aon.bridge.controller.ISecurityBridgeConstants.LOGIN_ERROR_DEFAULT;
import static com.code.aon.bridge.controller.ISecurityBridgeConstants.LOGIN_ERROR_PREFFIX;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 21/05/2007
 *
 */
public class FailedLogin  {

    /** Obtains the SessionFilter Logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(FailedLogin.class);
    
	private static String MESSAGES_FILE = "com.code.aon.bridge.i18n.messages";
	
	private static final int DEFAULT_STATUS = 10;

	private String message;

	public FailedLogin() throws DeploymentException {
	}

	/**
	 * This method gets called myfaces login implementation, using html frames.
	 * 
	 * @return
	 */
	public boolean isException() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest();
		AuthenticationLoginException e = (AuthenticationLoginException) request.getAttribute( ISecurityBridgeConstants.AON_LAST_EXCEPTION_KEY );
		if ( e != null ) {
			setMessage( getMessage(e) );
			return true;
		}
		return false;
	}
	
	private String getMessageString( AuthenticationLoginException e ) {
		String message = null;
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		String errorId = e.getMessage();
		if ( message == null ) {
	        ResourceBundle bundle = ResourceBundle.getBundle( MESSAGES_FILE, locale );
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
