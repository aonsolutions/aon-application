package com.code.aon.ui.util;

import static com.code.aon.ui.common.ICommonConstants.AON_AIO_APPLICATION;
import static com.code.aon.ui.common.ICommonConstants.AON_ROLE_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonConstants.CONFIGURATION_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.ERROR;

import java.net.IDN;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import jakarta.el.ELContext;
import jakarta.el.ExpressionFactory;
import jakarta.el.MethodExpression;
import jakarta.el.ValueExpression;
import javax.faces.application.FacesMessage;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomain;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.jaas.vendor.tomcat.HttpServletRequestValve;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.controller.ConfigurationController;
import com.code.aon.ui.common.role.BasicRoleManager;

/**
 * AonUtil includes some common methods.
 */
public class AonUtil {

	/** Suffix for message details (<code>_detail</code>) */
	private static final String DETAIL_SUFFIX = "_detail";

	/** Default bundle for messages (<code>javax.faces.Messages</code>) */
	private static final String DEFAULT_BUNDLE = "javax.faces.Messages";

	/** Obtains a suitable Logger. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AonUtil.class);

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
	 * Gets the value of the EL expression.
	 * 
	 * @param expression
	 *            the expression
	 * 
	 * @return the expression value
	 */
	public static Object getValue(String expression) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		ValueExpression ve = ef.createValueExpression(elctx, expression, Object.class);
		return ve.getValue(elctx);
	}	
	
	/**
	 * Executes the Action listener expression.
	 *
	 * @param expression the expression
	 * @param event the event
	 */
	public static void actionListener(String expression, ActionEvent event) {
		FacesContext ctx = FacesContext.getCurrentInstance();
		ELContext elctx = ctx.getELContext();
		ExpressionFactory ef = ctx.getApplication().getExpressionFactory();
		MethodExpression me = ef.createMethodExpression(elctx, expression, null, new Class[] {ActionEvent.class});
		me.invoke(elctx, new Object[]{event});
	}		

	/**
	 * Gets the configuration controller.
	 * 
	 * @return the configuration controller
	 */
	public static ConfigurationController getConfigurationController() {
		return (ConfigurationController) AonUtil.getRegisteredBean(CONFIGURATION_CONTROLLER_NAME);
	}

	/**
	 * Gets the value of the property defined for the bean.
	 *
	 * @param beanName the bean
	 * @param property the property
	 * @return the value
	 */
	public static Object getBeanValue( String beanName, String property ) {
		ConfigurationController cc = AonUtil.getConfigurationController();
		Map<String, Object> map = cc.getBean().get(beanName);
		if ( map != null ) {
			return map.get(property);
		}
		return null;		
	}

	/**
	 * Gets the boolean value of the property defined for the bean.
	 *
	 * @param beanName the bean
	 * @param property the property
	 * @return the value
	 */
	public static boolean isBeanValue( String beanName, String property ) {
		Object value = getBeanValue(beanName, property);
		return (value != null) ? (Boolean) value : false; 
	}
	
	/**
	 * Puts the value of the property defined for the bean.
	 *
	 * @param beanName the bean
	 * @param property the property
	 * @param value the value
	 */
	public static void setBeanValue( String beanName, String property, Object value ) {
		ConfigurationController cc = AonUtil.getConfigurationController();
		Map<String, Object> map = cc.getBean().get(beanName);
		if ( map != null ) {
			map.put(property, value);
		}		
	}
	
	/**
	 * Gets the Role Manager Controller
	 * 
	 * @return the Role Manager Controller
	 */
	public static BasicRoleManager getRoleManager() {
		return (BasicRoleManager) AonUtil.getRegisteredBean("aonRole");

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
			LOGGER.error(clazz + " has not valid IManagerBean registered!", e);
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
		BasicRoleManager rm = (BasicRoleManager) AonUtil.getRegisteredBean(AON_ROLE_CONTROLLER_NAME);
		return rm.isUserInRole(role);
	}

	/**
	 * Gets the context path.
	 * 
	 * @return the context path
	 */
	public static String getContextPath() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		return StringUtils.defaultIfEmpty(request.getContextPath(), AON_AIO_APPLICATION);
	}

	/**
	 * Gets the server name.
	 * 
	 * @return the server name
	 */
	public static String getServerName() {
		return getServerName(HttpServletRequestValve.getHttpServletRequest());
	}

	/**
	 * Gets the server name.
	 * 
	 * @return the server name
	 */
	public static String getServerName(HttpServletRequest request) {
		return IDN.toUnicode(request.getServerName());
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
		FacesMessage msg = getMessage(ctx, ERROR, message);
		msg.setSeverity(severity);
		ctx.addMessage(ERROR, msg);
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
				LOGGER.debug( e.getMessage(), e );
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
				LOGGER.debug( e.getMessage(), e );
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
			LOGGER.debug( e.getMessage(), e );
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
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addFatalMessageFromBundle( String messageKey, Object ... arguments) {
    	String msg = AonUtil.getMessage(messageKey, arguments);
    	addFatalMessage(msg);
    	return msg;
    }

    /**
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addErrorMessageFromBundle( String messageKey, Object ... arguments) {
    	String msg = AonUtil.getMessage(messageKey, arguments);
    	addErrorMessage(msg);
    	return msg;
    }

    /**
     * @param bundleKey
     * @param messageKey
     * @param arguments 
     * @return String
     */
    public static String addInfoMessageFromBundle(String messageKey, Object ... arguments ) {
    	String msg = AonUtil.getMessage(messageKey, arguments);
    	addInfoMessage(msg);
    	return msg;
    }

    /**
     * @param messageKey
     * @return String
     */
    public static String addWarningMessageFromBundle(String messageKey, Object ... arguments ) {
    	String msg = AonUtil.getMessage(messageKey, arguments);
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
    public static String getMessage(String messageKey, Object ... arguments ) {
    	ResourceBundle bundle = getResourceBundle(ICommonMessages.BUNDLE_NAME);
    	String value = bundle.getString(messageKey);
    	if ( arguments.length > 0 ) {
    		MessageFormat mf = new MessageFormat( value, AonUtil.getCurrentLocale() );
    		value = mf.format( arguments );
    	}
    	return value;
    } 
    
    private static String getUserAgent() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	return context.getExternalContext().getRequestHeaderMap().get("User-Agent");
    }

    /**
     * Checks if is Internet Explorer.
     *
     * @return true, if is Internet Explorer
     */
    public static boolean isMSIE() {
		return StringUtils.contains(getUserAgent(), "MSIE");
    }

    /**
     * Checks if is Google Chrome.
     *
     * @return true, if is Google Chrome
     */
    public static boolean isChrome() {
		return StringUtils.contains(getUserAgent(), "Chrome");
    }
    
	/**
	 * Checks if is apple device.
	 *
	 * @return true, if is apple device
	 */
	public static boolean isAppleDevice() {
		String userAgent = getUserAgent();
		return StringUtils.contains(userAgent, "iPad") ||
				StringUtils.contains(userAgent, "iPod") ||
				StringUtils.contains(userAgent, "iPhone");
	}    

    /**
     * Sort the list of SelectItem
     * 
     * @param list
     */
    public static void sortSelectItems( List<SelectItem> list ) {
    	Comparator<SelectItem> comparator = new Comparator<SelectItem>() {

			@Override
			public int compare(SelectItem o1, SelectItem o2) {
				return o1.getLabel().compareToIgnoreCase(o2.getLabel());
			}
    		
		};
    	Collections.sort( list, comparator );
    }
 
    /**
     * Get SelectItem for an IDomain object.
     * 
     * @param to
     * @param label
	 * @return SelectItem 
     */
	public static SelectItem getSelectItem( IDomain to, String label ) {
		String _label = label;
		if ( to.getDomain() != DomainManager.getCurrentDomain() ) {
			String domainDescription = AdminUtil.getDomainDescription(to.getDomain());
			_label = label + " (" + domainDescription + ")"; 
		}
		SelectItem item = new SelectItem(to, _label);
		return item;
	}

	/**
	 * Returns the AuthPrincipal
	 * 
	 * @return AuthPrincipal
	 */
	public static AuthPrincipal getAuthPrincipal() {
		HttpServletRequest request = HttpServletRequestValve.getHttpServletRequest();
		if (request != null) {
			AuthPrincipal principal = (AuthPrincipal) request.getUserPrincipal();
			if ( principal != null ) {
				return principal;
			} else {
				LOGGER.error( "Request without principal: {}", request );
			}
		}
		return null;
	}
	
	public static String getDomainName() {
		return getAuthPrincipal().getDomain();
	}
 
	public static Object getServletContextAttribute(String name) {
		ServletContext servletContext = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();		
		return servletContext.getAttribute(name);
	}
	
}