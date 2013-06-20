package com.code.aon.ui.common.controller;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.Principal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import com.code.aon.common.util.Classpath;
import com.code.aon.common.util.PrincipalUtil;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.common.LocaleElement;
import com.code.aon.ui.util.AonUtil;
import com.sun.org.apache.xerces.internal.jaxp.JAXPConstants;

/**
 * The Class ConfigurationController is used to set some default configurable
 * parameters of the application.
 */
public class ConfigurationController implements Serializable, ICommonConstants, JAXPConstants {
	
	private static final long serialVersionUID = -1159615075844874762L;
	
	private static final Locale SPANISH = new Locale("es");
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);
	
	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");
	
	private static final String AON_CONFIG_XML = "/WEB-INF/aon-config.xml";
	
	private static final String CONFIG_SHCHEMA = "config.xsd";
	
    public static final int DEFAULT_PAGE_LIMIT = 20;	

    public static final int DEFAULT_LINES_PAGE_LIMIT = 10;

	/** The all style sheets. */
	private List<String> styleSheets;

	/** The properties. */
	private Map<String, Object> properties;
	
	private Map<String,Map<String,Object>> bean;	
	
	private String currentAction = "home";
	
	private String application;
	
	private LocaleElement[] locales;
	
	private List<SelectItem> localeList;
	
	private AuthPrincipal principal;
	
	private int pageLimit = DEFAULT_PAGE_LIMIT;
	
	private int linesPageLimit = DEFAULT_LINES_PAGE_LIMIT;
	
	/**
	 * The Constructor.
	 */
	public ConfigurationController() {
		this.properties = new HashMap<String, Object>();
		this.styleSheets = new ArrayList<String>();
		Document document = getConfigDocument();
		if ( document != null ) {
			bean = loadBeanConfiguration( document );
		}		
		initApplication();
		this.locales = new LocaleElement[] {
			new LocaleElement(SPANISH), new LocaleElement(Locale.ENGLISH) 
		};
		this.principal = resolvePrincipal();
	}
	
	private AuthPrincipal resolvePrincipal() {
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletRequest request = (HttpServletRequest) ctx.getExternalContext().getRequest(); 
		Principal principal = request.getUserPrincipal();
		if ( principal instanceof AuthPrincipal ) {
			return (AuthPrincipal) principal;
		}
		return PrincipalUtil.getAuthPrincipal();
	}

	public int getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(int pageLimit) {
		this.pageLimit = pageLimit;
	}

	public int getLinesPageLimit() {
		return linesPageLimit;
	}

	public void setLinesPageLimit(int linesPageLimit) {
		this.linesPageLimit = linesPageLimit;
	}

	public AuthPrincipal getAuthPrincipal() {
		return this.principal;
	}

	/**
	 * Gets the properties map.
	 * 
	 * @return the properties
	 */
	public Map<String, Object> getProperties() {
		return properties;
	}

	/**
	 * Sets the properties map.
	 * 
	 * @param properties the properties map
	 */
	public void setProperties(Map<String, Object> properties) {
		this.properties = properties;
	}

	/**
	 * Sets the property in the properties map.
	 * 
	 * @param key the key
	 * @param value the value
	 */
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}

	/**
	 * Gets the property.
	 * 
	 * @param key the key
	 * 
	 * @return the property linked with 'key' in the map
	 */
	public Object getProperty(String key) {
		return properties.get(key);
	}

	/**
	 * Gets the style sheets.
	 * 
	 * @return the style sheets
	 */
	public List<String> getStyleSheets() {
		return styleSheets;
	}

	
	/**
	 * Sets the style sheets.
	 * 
	 * @param styleSheets the new style sheets
	 */
	public void setStyleSheets(List<String> styleSheets) {
		this.styleSheets = styleSheets;
	}

	/**
	 * Calculate application version.
	 * 
	 */
	private void initApplication() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		this.application = StringUtils.stripStart(ec.getRequestContextPath(), "/" );
	}
	
    /**
     * Gets the current date.
     * 
     * @return the current date
     */
    public String getCurrentDate() {
        return FORMATTER.format(new Date()).toUpperCase();
    }

    /**
     * Gets the session id.
     * 
     * @return the session id
     */
    public String getSessionId() {
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	return session.getId();
    }
    
    private void onLogout( ActionEvent event ) {
    	String onLogout = (String) getProperty(ON_LOGOUT);
    	if (! StringUtils.isEmpty(onLogout) ) {
			String expression = "#{" + onLogout + "}";
			AonUtil.actionListener(expression, event);
    	}
    }
    
    /**
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	onLogout(event);
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	session.invalidate();    	
    	context.responseComplete();
    }
    
	/**
	 * Gets the bean.
	 * 
	 * @return the bean
	 */
	public Map<String, Map<String, Object>> getBean() {
		return bean;
	}

	private String[] getXmlSchemas() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", CONFIG_SHCHEMA);
	        if (! ArrayUtils.isEmpty(urls) ) {
		        String[] list = new String[urls.length];
		        for( int i = 0; i < urls.length; i++ ) {
		        	list[i] = urls[i].toString();
		        }
		        return list;
	        }
        } catch (IOException e) {
        	LOGGER.error("Error searching files: " + CONFIG_SHCHEMA, e);
        }		
        return null;
	}	
	
	private Document getConfigDocument() {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		URL config = null;
		try {
			config = ec.getResource(AON_CONFIG_XML);
		} catch (MalformedURLException e) {
			LOGGER.error(AON_CONFIG_XML + " not found", e);
			return null;
		}
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		
		factory.setNamespaceAware(true);
		factory.setValidating(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setAttribute( JAXP_SCHEMA_LANGUAGE, XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		String[] schemas = getXmlSchemas();
		factory.setAttribute( JAXP_SCHEMA_SOURCE, schemas );		
		
		Document document = null;
		LogErrorHandler errorHandler = new LogErrorHandler();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler( errorHandler );
			document = builder.parse(config.toString());
		} catch (Throwable th) {
			AonUtil.addErrorMessageFromBundle( DEFAULT_BUNDLE, CONFIGURATION_ERROR, AON_CONFIG_XML, th.getMessage() );
			LOGGER.error(th.getMessage(), th);
		} finally {
			if ( errorHandler.isValidationError() ) {
				AonUtil.addErrorMessageFromBundle( DEFAULT_BUNDLE, CONFIGURATION_ERROR, AON_CONFIG_XML, errorHandler.getException().getMessage() );
				document = null;
			}
		}
		return document;
	}
	
	private Object getAttributeValue( Node attribute ) {
		String value = attribute.getNodeValue();
		if ( Boolean.TRUE.toString().equals(value) ) {
			return Boolean.TRUE;
		} else if ( Boolean.FALSE.toString().equals(value) ) {
			return Boolean.FALSE;
		} else if ( NumberUtils.isNumber(value) ) {
			return NumberUtils.createNumber(value);
		}
		return value;
	}
	
	private Map<String,Map<String,Object>> loadBeanConfiguration( Document document ) {
		Map<String,Map<String,Object>> bean = new HashMap<String, Map<String,Object>>();
		Element root = document.getDocumentElement();
		NodeList list = root.getChildNodes();
		for( int i = 0; i < list.getLength(); i++ ) {
			Node config = list.item(i);
			if ( config.getNodeType() == Node.ELEMENT_NODE ) {
				NodeList childs = config.getChildNodes();
				for( int j = 0; j < childs.getLength(); j++ ) {
					Node child = childs.item(j);
					if ( child.getNodeType() == Node.ELEMENT_NODE ) {
						Map<String,Object> values = new HashMap<String, Object>();
						NamedNodeMap attributes = child.getAttributes();
						for( int n = 0; n < attributes.getLength(); n++ ) {
							Node attribute = attributes.item(n);
							Object value = getAttributeValue( attribute );
							values.put( attribute.getLocalName(), value);
						}
						bean.put( child.getLocalName(), values );
					}
				}
			}
		}
		return bean;
	}
	
	/**
	 * Gets the current action.
	 *
	 * @return the current action
	 */
	public String getCurrentAction() {
		return currentAction;
	}

	/**
	 * Sets the current action.
	 *
	 * @param currentAction the new current action
	 */
	public void setCurrentAction(String currentAction) {
		this.currentAction = currentAction;
	}

	private String getURL( String application, String action ) {
		StringBuffer url = new StringBuffer();
		url.append("http://help.aonsolutions.es/ayuda/resumen.php?application=");
		url.append( application );
		if ( action != null ) {
			url.append("&action_id=").append(action);
		}
		return url.toString();		
	}
	
	/**
	 * Gets the help url.
	 *
	 * @return the help url
	 */
	public String getHelpURL() {
		return getURL(this.application, this.currentAction);
	}
	
	public LocaleElement[] getLocales() {
		return locales;
	}

	public List<SelectItem> getLocaleList() {
    	if ( localeList == null ) {
    		localeList = new LinkedList<SelectItem>();
    		for( LocaleElement element : getLocales() ) {
	            SelectItem item = new SelectItem(element.getId(), element.getDisplayName());
	            localeList.add( item );
    		}
        }
        return localeList;
	}
	
	private class LogErrorHandler implements ErrorHandler {
		
		private boolean validationError;
		
		private SAXParseException exception;

		@Override
		public void error(SAXParseException exception) throws SAXException {
			this.validationError = true;
			this.exception = exception;
			LOGGER.error( exception.getMessage(), exception );
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			this.validationError = true;
			this.exception = exception;
			LOGGER.error( exception.getMessage(), exception );
		}

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			LOGGER.error( exception.getMessage(), exception );
		}

		/**
		 * Checks if is validation error.
		 * 
		 * @return true, if is validation error
		 */
		public boolean isValidationError() {
			return validationError;
		}

		/**
		 * Gets the exception.
		 * 
		 * @return the exception
		 */
		public SAXParseException getException() {
			return exception;
		}
		
	}
	
}