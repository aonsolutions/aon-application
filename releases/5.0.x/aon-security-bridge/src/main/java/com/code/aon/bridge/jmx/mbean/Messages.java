package com.code.aon.bridge.jmx.mbean;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

/**
 * Clase para obtener los mensajes en función de una clave.   
 * 
 * @author Consulting & Development. Eugenio Castellano - 17-feb-2005
 * @since 1.0
 *  
 */
public class Messages {

	/**
     * Recurso donde se encuentran los mensajes. En este caso
     * <code>com.code.aon.bridge.jmx.mbean.messages</code>.
     */
    private static final String BUNDLE_NAME = "com.code.aon.bridge.jmx.mbean.messages";//$NON-NLS-1$

    /**
     * Instancia estática de esta clase.
     */
    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle(BUNDLE_NAME);

	/** Suffix for message details (<code>_detail</code>) */
	private static final String DETAIL_SUFFIX = "_detail";

	/** Default bundle for messages (<code>javax.faces.Messages</code>) */
	private static final String DEFAULT_BUNDLE = "javax.faces.Messages";

	/** Key to identify an aon error. (value is ""aon_error"") */
	public static final String AON_ERROR = "aon_error";

	/**
     * Devuelve el mensaje correspondiente a la clave especificada.
     * 
     * @param key
     *            String Clave del mensaje.
     * @return El mensaje correspondiente a la clave especificada.
     */
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param message
	 *            Message to be added to the messages collection
	 */

	public static void addFatalMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_FATAL);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addFatalMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_FATAL);
	}
	/**
	 * Adds <code>message</code> as an error message to the messages
	 * collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addErrorMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addErrorMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_ERROR);
	}

	/**
	 * Adds <code>message</code> as an info message to the messages collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addInfoMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addInfoMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_INFO);
	}

	/**
	 * Adds <code>message</code> as an warning message to the messages
	 * collection
	 * 
	 * @param message
	 *            the message
	 */
	public static void addWarningMessage(String message) {
		String[] args = {message};
		addMessage(args, FacesMessage.SEVERITY_WARN);
	}
	/**
	 * Adds <code>message</code> to the messages collection
	 * 
	 * @param args
	 *            Messages to be added to the messages collection
	 */
	public static void addWarningMessage(String[] args) {
		addMessage(args, FacesMessage.SEVERITY_WARN);
	}

	/**
	 * @param message
	 * @param severity
	 */
	public static void addMessage(String[] message, FacesMessage.Severity severity) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		FacesMessage msg = getMessage(ctx, Messages.AON_ERROR, message);
		msg.setSeverity(severity);
		ctx.addMessage(Messages.AON_ERROR, msg);
	}

	/**
	 * @param context
	 * @param messageId
	 * @param params
	 * @return d
	 */
	public static FacesMessage getMessage(FacesContext context, String messageId, Object params[]) {
		if (context == null || messageId == null)
			throw new NullPointerException(" context " + context + " messageId " + messageId);
		Locale locale = getCurrentLocale();
		if (null == locale)
			throw new NullPointerException(" locale " + locale);
		FacesMessage message = getMessage(locale, messageId, params);
		if (message != null) {
			return message;
		}
		// TODO /FIX: Note that this has fallback behavior to default Locale for
		// message,
		// but similar behavior above does not. The methods should probably
		// behave
		locale = Locale.getDefault();
		return getMessage(locale, messageId, params);

	}

	/**
	 * 
	 * @return currently applicable Locale for this request.
	 */
	public static Locale getCurrentLocale() {
		Locale locale;

		FacesContext context = FacesContext.getCurrentInstance();
		if (context != null && context.getViewRoot() != null) {
			locale = context.getViewRoot().getLocale();
			if (locale == null)
				locale = Locale.getDefault();
		} else {
			locale = Locale.getDefault();
		}

		return locale;
	}

	/**
	 * @param locale
	 * @param messageId
	 * @param params
	 * @return message
	 */
	public static FacesMessage getMessage(Locale locale, String messageId, Object params[]) {
		String summary = null;
		String detail = null;
		String bundleName = FacesContext.getCurrentInstance().getApplication().getMessageBundle();
		ResourceBundle bundle = null;

		if (bundleName != null) {
			try {
				bundle = ResourceBundle.getBundle(bundleName, locale, getCurrentLoader(bundleName));
				summary = bundle.getString(messageId);
			} catch (MissingResourceException e) {
				// NoOp
			}
		}

		if (summary == null) {
			try {
				bundle = ResourceBundle.getBundle(DEFAULT_BUNDLE, locale, getCurrentLoader(DEFAULT_BUNDLE));
				if (bundle == null) {
					throw new NullPointerException();
				}
				summary = bundle.getString(messageId);
			} catch (MissingResourceException e) {
				// NoOp
			}
		}

		if (summary == null) {
			summary = messageId;
		}

		if (bundle == null) {
			throw new NullPointerException("Unable to locate ResrouceBundle: bundle is null");
		}
		summary = substituteParams(locale, summary, params);

		try {
			detail = substituteParams(locale, bundle.getString(messageId + DETAIL_SUFFIX), params);
		} catch (MissingResourceException e) {
			// NoOp
		}

		return new FacesMessage(summary, detail);
	}

	/**
	 * Gets the ClassLoader associated with the current thread. Returns the
	 * class loader associated with the specified default object if no context
	 * loader is associated with the current thread.
	 * 
	 * @param defaultObject
	 *            The default object to use to determine the class loader (if
	 *            none associated with current thread.)
	 * @return ClassLoader
	 */
	protected static ClassLoader getCurrentLoader(Object defaultObject) {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		if (loader == null) {
			loader = defaultObject.getClass().getClassLoader();
		}
		return loader;
	}

	/**
	 * Uses <code>MessageFormat</code> and the supplied parameters to fill in
	 * the param placeholders in the String.
	 * 
	 * @param locale
	 *            The <code>Locale</code> to use when performing the
	 *            substitution.
	 * @param msgtext
	 *            The original parameterized String.
	 * @param params
	 *            The params to fill in the String with.
	 * @return The updated String.
	 */
	public static String substituteParams(Locale locale, String msgtext, Object params[]) {
		String localizedStr = null;
		if (params == null || msgtext == null)
			return msgtext;
		StringBuffer b = new StringBuffer(100);
		MessageFormat mf = new MessageFormat(msgtext);
		if (locale != null) {
			mf.setLocale(locale);
			b.append(mf.format((params)));
			localizedStr = b.toString();
		}
		return localizedStr;
	}

}