package com.code.aon.ui.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javax.el.ELContext;
import javax.el.ExpressionFactory;
import javax.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.common.role.RoleManager;

/**
 * AonUtil includes some common methods.
 */
public class AonUtil {

	/** Suffix for message details (<code>_detail</code>) */
	private static final String DETAIL_SUFFIX = "_detail";

	/** Default bundle for messages (<code>javax.faces.Messages</code>) */
	private static final String DEFAULT_BUNDLE = "javax.faces.Messages";

	/** Key to identify an aon error. (value is ""aon_error"") */
	public static final String AON_ERROR = "aon_error";

	/** Obtains a suitable Logger. */
	private static final Logger LOGGER = Logger.getLogger(AonUtil.class.getName());

	/**
	 * Gets the bean registered in <code>faces-bean-config.xml</code> with
	 * that name.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the registered bean
	 */
	public static Object getRegisteredBean(String name) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx,"#{" + name + "}",Object.class);
		return ve.getValue(elctx);
	}

	/**
	 * Gets the configuration controller.
	 * 
	 * @return the configuration controller
	 */
	public static ConfigurationController getConfigurationController() {
		return AonUtil.getConfigurationController("aonConfiguration");

	}

	/**
	 * Gets the Role Manager Controller
	 * 
	 * @return the Role Manager Controller
	 */
	public static RoleManager getRoleManager() {
		return (RoleManager) AonUtil.getRegisteredBean("aonRole");

	}

	/**
	 * Gets the configuration controller.
	 * 
	 * @param name
	 *            the name
	 * 
	 * @return the configuration controller
	 */
	private static ConfigurationController getConfigurationController(String name) {
		return (ConfigurationController) AonUtil.getRegisteredBean(name);
	}

	/**
	 * Gets the manager bean related with a ITransferObject.
	 * 
	 * @param to
	 *            the ITransferObject
	 * 
	 * @return the manager bean
	 */
	public static IManagerBean getManagerBean(ITransferObject to) {
		return getManagerBean(to.getClass());
	}

	/**
	 * Gets the manager bean related with a class.
	 * 
	 * @param clazz
	 *            the class
	 * 
	 * @return the manager bean
	 */
	public static IManagerBean getManagerBean(Class<? extends ITransferObject> clazz) {
		try {
			return BeanManager.getManagerBean(clazz);
		} catch (ManagerBeanException e) {
			LOGGER.severe(clazz + " has not valid IManagerBean registered!");
		}
		return null;
	}

	/**
	 * Return the login name of the user making the current request if any;
	 * otherwise, return <code>null</code>.
	 * 
	 * @return the remote user
	 */
	public static String getRemoteUser() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		return ec.getRemoteUser();
	}

	/**
	 * Return true if the currently authenticated user is included in the
	 * specified role. Otherwise, return <code>false</code>.
	 * 
	 * @param role
	 *            Logical role name to be checked.
	 * 
	 * @return the remote user
	 */
	public static boolean isUserInRole(String role) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		return ec.isUserInRole(role);
	}

	/**
	 * Gets the context path.
	 * 
	 * @return the context path
	 */
	public static String getContextPath() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		return ec.getRequestContextPath();
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
		FacesMessage msg = getMessage(ctx, AonUtil.AON_ERROR, message);
		msg.setSeverity(severity);
		ctx.addMessage(AonUtil.AON_ERROR, msg);
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
		if (params == null || msgtext == null) {
			return msgtext;
		}
		MessageFormat mf = new MessageFormat( msgtext, locale );
		return mf.format( params );
	}
	
    /**
     * @param messageKey
     * @return String
     */
    public static String getMessage(String messageKey) {
    	return AonUtil.getMessage(ICommonConstants.DEFAULT_BUNDLE, messageKey);
    }
	
    /**
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addFatalMessageFromBundle( String bundleKey, String messageKey, Object ... arguments) {
    	String msg = AonUtil.getMessage(bundleKey, messageKey, arguments);
    	addFatalMessage(msg);
    	return msg;
    }

    /**
     * @param messageKey
     * @return String
     */
    public static String addFatalMessageFromBundle(String messageKey) {
    	String msg = AonUtil.getMessage(messageKey);
    	addFatalMessage(msg);
    	return msg;
    }

    /**
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addErrorMessageFromBundle( String bundleKey, String messageKey, Object ... arguments) {
    	String msg = AonUtil.getMessage(bundleKey, messageKey, arguments);
    	addErrorMessage(msg);
    	return msg;
    }

    /**
     * @param messageKey
     * @return String
     */
    public static String addErrorMessageFromBundle(String messageKey) {
    	String msg = AonUtil.getMessage(messageKey);
    	addErrorMessage(msg);
    	return msg;
    }

    /**
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addInfoMessageFromBundle(String bundleKey, String messageKey, Object ... arguments ) {
    	String msg = AonUtil.getMessage(bundleKey, messageKey, arguments);
    	addInfoMessage(msg);
    	return msg;
    }

    /**
     * @param messageKey
     * @return String
     */
    public static String addInfoMessageFromBundle(String messageKey) {
    	String msg = AonUtil.getMessage(messageKey);
    	addInfoMessage(msg);
    	return msg;
    }

    /**
     * @param bundleKey
     * @param messageKey
     * @return String
     */
    public static String addWarningMessageFromBundle(String bundleKey,String messageKey) {
    	String msg = AonUtil.getMessage(bundleKey, messageKey);
    	addWarningMessage(msg);
    	return msg;
    }

    /**
     * @param messageKey
     * @return String
     */
    public static String addWarningMessageFromBundle(String messageKey) {
    	String msg = AonUtil.getMessage(messageKey);
    	addWarningMessage(msg);
    	return msg;
    }

    
    /**
     * Gets the resource bundle.
     * 
     * @param bundleKey the bundle key
     * 
     * @return the resource bundle
     */
    public static ResourceBundle getResourceBundle( String bundleKey ) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	return context.getApplication().getResourceBundle(context, bundleKey);    	
    }
    
    /**
     * Gets the message.
     * 
     * @param bundleKey the bundle key
     * @param messageKey the message key
     * @param arguments the arguments
     * 
     * @return String
     */
    public static String getMessage(String bundleKey, String messageKey, Object ... arguments ) {
    	ResourceBundle bundle = getResourceBundle(bundleKey);
    	String value = bundle.getString(messageKey);
    	if ( arguments.length > 0 ) {
    		MessageFormat mf = new MessageFormat( value, AonUtil.getCurrentLocale() );
    		value = mf.format( arguments );
    	}
    	return value;
    }
    
}
