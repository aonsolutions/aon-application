package com.code.aon.ui.common.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.naming.Name;
import javax.servlet.http.HttpSession;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
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

import com.code.aon.bridge.session.LoggedUser;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.Classpath;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ldap.BasicLdap;
import com.code.aon.ldap.Entry;
import com.code.aon.ldap.IAonObjectClasses;
import com.code.aon.ldap.ILdapConstants;
import com.code.aon.ldap.NameResolver;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.util.AonUtil;
import com.sun.org.apache.xerces.internal.jaxp.JAXPConstants;

/**
 * The Class ConfigurationController is used to set some default configurable
 * parameters of the application.
 */
public class ConfigurationController implements Serializable, ICommonConstants, JAXPConstants, ILdapConstants, IAonObjectClasses {
	
	private static final long serialVersionUID = -1159615075844874762L;

	private static final String IMPLEMENTATION_VERSION = "Implementation-Version";

	private static final String APPLICATION_VERSION = "applicationVersion";
	
	private static final String DATA_BASE_VERSION = "databaseVersion";
	
	private static final String BUILD_NUMBER = "buildNumber";
	
	private static final String BUILD_DATE = "buildDate";
	
	private static final String BUILD_REVISION = "buildRevision";
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ConfigurationController.class);
	
	private static final DateFormat FORMATTER = new SimpleDateFormat("EEEE, dd MMMM yyyy");
	
	private static final String AON_CONFIG_XML = "/WEB-INF/aon-config.xml";
	
	private static final String CONFIG_SHCHEMA = "config.xsd";		

	/** The all style sheets. */
	private List<String> styleSheets;

	/** The properties. */
	private Map<String, Object> properties;
	
	private Map<String,Map<String,Object>> bean;	
	
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
		initApplicationVersion();
		initDataBaseVersion();
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
	private void initApplicationVersion() {
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			InputStream in = ec.getResourceAsStream("META-INF/MANIFEST.MF");
			Manifest m = new Manifest(in);
			Attributes attrs = m.getMainAttributes();
			String applicationVersion = attrs.getValue(IMPLEMENTATION_VERSION);
			if (! StringUtils.isEmpty(applicationVersion)) {
				getProperties().put(APPLICATION_VERSION, StringUtils.trim(applicationVersion) );
				String buildNumber = StringUtils.trim( attrs.getValue(BUILD_NUMBER) );
				getProperties().put(BUILD_NUMBER, buildNumber );
				String buildDate = StringUtils.trim( attrs.getValue(BUILD_DATE) );
				getProperties().put(BUILD_DATE, buildDate );
				String buildRevision = StringUtils.trim( attrs.getValue(BUILD_REVISION) );
				getProperties().put(BUILD_REVISION, buildRevision );
			} else {
				LOGGER.warn("Imposible determinar la versión");
			}
		} catch (Throwable e) {
			LOGGER.warn("Imposible determinar la versión");
		}
	}
	
	/**
	 * Checks for data source.
	 * 
	 * @return true, if successful
	 */
	private boolean hasDataSource() {
		BasicLdap ldap = new BasicLdap();
		LoggedUser loggedUser = (LoggedUser) AonUtil.getRegisteredBean(LoggedUser.LOGGED_USER);
		AuthPrincipal principal = loggedUser.getPrincipal();
		if ( principal != null ) {
			String application = StringUtils.removeStart(principal.getContext(), "/");
			Name dn = NameResolver.getDomainApplicationDN(principal.getDomain(), application);
			Entry entry = ldap.get(dn, DOMAIN_APPLICATION, DATA_SOURCE_ATTRIBUTE );
			return (entry != null) && entry.containsKey(DATA_SOURCE_ATTRIBUTE);
		}
		return false;
	}
	
	/**
	 * Calculate database version.
	 * 
	 */
	private void initDataBaseVersion() {
		try {
			if ( hasDataSource() ) {
		    	String name = HibernateUtil.getSessionFactoryName();
		        Session session = HibernateUtil.getSession(name);
		        SQLQuery query = session.createSQLQuery("SELECT version_number FROM db_version");
		        List<?> list = query.list();
		        if (! list.isEmpty() ) {
		        	String dbVersion = (String) list.get(0);
		        	getProperties().put(DATA_BASE_VERSION, dbVersion );
		        }
			}
		} catch (Throwable e) {
			LOGGER.warn("Imposible determinar la versión");
		}
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
     * Logout from the current session.
     * 
     * @param event the event
     */
    public void logout( ActionEvent event ) {
    	FacesContext context = FacesContext.getCurrentInstance();
    	HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
    	session.invalidate();    	
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
		factory.setAttribute( JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
		
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