package com.code.aon.ui.common.controller;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.context.FacesContext;

import org.apache.commons.lang.StringEscapeUtils;

/**
 * The Class ExceptionBean handles the exceptions ocurred within the application while it's running.
 */
public class ExceptionBean implements Serializable {

	private static final long serialVersionUID = 2507626847725395948L;

	/** The cause of the generated exception */
	private Throwable cause;

	/** The error code of the generated exception */
	private int errorCode;

	/**
	 * The Constructor.
	 */
	public ExceptionBean() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ex = ef.createValueExpression("#{aon_exception}",Throwable.class);
		if (ex != null ) {
			cause = (Throwable) ex.getValue( elctx );	
		}
		ValueExpression er = ef.createValueExpression("#{aon_http_error_code}",Integer.class);
		if (er != null ) {
			Integer i = (Integer) er.getValue( elctx );
			if (i != null) {
				errorCode = i.intValue();	
			}
		}
	}	

	/**
	 * Gets the cause of the exception.
	 * 
	 * @return the cause
	 */
	public Throwable getCause() {
		return cause;
	}

	/**
	 * Sets the cause of the exception.
	 * 
	 * @param cause the cause
	 */
	public void setCause(Throwable cause) {
		this.cause = cause;
	}

	/**
	 * Gets the error code of the exception.
	 * 
	 * @return the error code
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * Sets the error code of the exception.
	 * 
	 * @param errorCode the error code
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}

	/**
	 * Gets the error message.
	 * 
	 * @return the error message
	 */
	public String getErrorMessage() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		Locale locale = ctx.getApplication().getDefaultLocale();
		ResourceBundle bundle = ResourceBundle.getBundle("com.code.aon.ui.common.i18n.error_messages", locale);
		String m = null;
		try {
		    m = bundle.getString("aon_http_error_" + getErrorCode());	
		} catch (MissingResourceException e ) {
			m = bundle.getString("aon_exception");
		}
		
		return m;
	}

	/**
	 * Gets the exception message.
	 * 
	 * @return the exception message
	 */
	public String getExceptionMessage() {
		return cause == null ? "" : cause.getMessage();
	}

	/**
	 * Gets the messages.
	 * 
	 * @return the messages
	 */
	public Object[] getMessages() {
		List<ExceptionMessage> list = new LinkedList<ExceptionMessage>();
		if (cause != null) {
			addMessage(list, cause);
		}
		return list.toArray();
	}

	/**
	 * Adds the message.
	 * 
	 * @param list the list of messages
	 * @param t the Throwable element to add to the list
	 */
	private void addMessage(List<ExceptionMessage> list, Throwable t) {
		ExceptionMessage em = new ExceptionMessage(t.getClass().getName(), t.getMessage());
		list.add(em);
		if (t.getCause() != null) {
			addMessage(list, t.getCause());
		}
	}

	/**
	 * Gets the full stack trace.
	 * 
	 * @return the full stack trace
	 */
	public String getFullStackTrace() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintStream ps = new PrintStream(out);
		cause.printStackTrace(ps);
		ps.flush();
		String r = out.toString();
		return r;
	}

	/**
	 * Gets the pretty full stack trace.
	 * 
	 * @return the pretty full stack trace
	 */
	public String getPrettyFullStackTrace() {
		String r = getFullStackTrace();
		r = r.replace("\r", " ");
		r = r.replace("\n", "<br/>");
		r = r.replace("\t", "&#160;&#160;&#160;&#160;");
		return r;
	}

	/**
	 * Gets the java script full stack trace.
	 * 
	 * @return the java script full stack trace
	 */
	public String getJavaScriptFullStackTrace() {
		return StringEscapeUtils.escapeJavaScript(getFullStackTrace());
	}

	/**
	 * The Class ExceptionMessage.
	 */
	public class ExceptionMessage {

		/** The class of the exception message. */
		private String clazz;

		/** The message. */
		private String message;

		/**
		 * The Constructor.
		 * 
		 * @param message the message
		 * @param clazz the clazz
		 */
		public ExceptionMessage(String clazz, String message) {
			this.clazz = clazz;
			this.message = message;
		}

		/**
		 * Gets the clazz.
		 * 
		 * @return the clazz
		 */
		public String getClazz() {
			return clazz;
		}

		/**
		 * Gets the message.
		 * 
		 * @return the message
		 */
		public String getMessage() {
			return message;
		}

	}

}
