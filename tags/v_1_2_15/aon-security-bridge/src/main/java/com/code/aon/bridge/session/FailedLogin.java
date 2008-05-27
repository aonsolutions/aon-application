/**
 * 
 */
package com.code.aon.bridge.session;

import java.text.MessageFormat;
import java.util.ResourceBundle;

import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

import com.code.aon.bridge.jmx.mbean.IConsoleAdmin;
import com.code.aon.bridge.jmx.mbean.IOperation;
import com.code.aon.bridge.plugin.Utils;
import com.code.aon.jaas.auth.session.AuthenticationLoginException;
import com.code.aon.jaas.deployment.DeploymentException;

/**
 * @author Consulting & Development. Iñaki Ayerbe - 21/05/2007
 *
 */
public class FailedLogin {

	protected static final String AON_LAST_EXCEPTION_KEY = "AON_LAST_EXCEPTION_KEY";

	private String message;

	public FailedLogin() throws DeploymentException {
        IConsoleAdmin console = Utils.getSecurityConsole();
		String oname = console.getAonSessionManagerName();
		AuthenticationLoginException e = 
			(AuthenticationLoginException) console.invoke( oname, IOperation.GET_LASTLOGIN_EXCEPTION, new Object[] {IConsoleAdmin.EMPTY_STRING}, new String[] {String.class.getName()} );
		if ( e != null ) {
			FacesContext ctx = FacesContext.getCurrentInstance();
	        ResourceBundle bundle = 
	            ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			MessageFormat messageFormat = new MessageFormat( bundle.getString( e.getMessage() ) );
			Object[] arguments = new Object[] { e.getArg() };
			setMessage( messageFormat.format(arguments) );
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
	        ResourceBundle bundle = 
	            ResourceBundle.getBundle( IOperation.MESSAGES_FILE, ctx.getViewRoot().getLocale() );
			MessageFormat messageFormat = new MessageFormat( bundle.getString( e.getMessage() ) );
			Object[] arguments = new Object[] { e.getArg() };
			setMessage( messageFormat.format(arguments) );
			return true;
		}
		return false;
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
