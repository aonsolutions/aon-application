package com.code.aon.ui.common.controller;

import static com.code.aon.ui.common.ICommonMessages.CONFIGURATION_ERROR;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
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

import com.code.aon.common.AonVersion;
import com.code.aon.common.util.Classpath;
import com.code.aon.ui.util.AonUtil;

public class BeanConfiguration implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private final static Logger LOGGER = LoggerFactory.getLogger(BeanConfiguration.class);
	
	private static final String AON_CONFIG_XML = "/WEB-INF/aon-config.xml";
	
	private static final String CONFIG_SHCHEMA = "config.xsd";

	private Map<String,Map<String,Object>> bean;
	
	public BeanConfiguration() {
		Document document = getConfigDocument();
		if ( document != null ) {
			bean = loadBeanConfiguration( document );
		}		
	}

	private StreamSource[] getXmlSchemas() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        try {
	        URL[] urls = Classpath.search(cl, "META-INF/", CONFIG_SHCHEMA);
	        if (! ArrayUtils.isEmpty(urls) ) {
	        	StreamSource[] list = new StreamSource[urls.length];
		        for( int i = 0; i < urls.length; i++ ) {
		        	InputStream in = urls[i].openStream();
		        	byte[] data = IOUtils.toByteArray(in);
		        	list[i] = new StreamSource(new ByteArrayInputStream(data));
		        	IOUtils.closeQuietly(in);
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
		factory.setIgnoringElementContentWhitespace(true);
		
		SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		
		Document document = null;
		LogErrorHandler errorHandler = new LogErrorHandler();
		try {
			Schema schemaGrammar = schemaFactory.newSchema(getXmlSchemas());
			factory.setSchema(schemaGrammar);
			DocumentBuilder builder = factory.newDocumentBuilder();
			builder.setErrorHandler( errorHandler );
			document = builder.parse(config.toString());
		} catch (Throwable th) {
			AonUtil.addErrorMessageFromBundle( CONFIGURATION_ERROR, AON_CONFIG_XML, th.getMessage() );
			LOGGER.error(th.getMessage(), th);
		} finally {
			if ( errorHandler.isValidationError() ) {
				AonUtil.addErrorMessageFromBundle( CONFIGURATION_ERROR, AON_CONFIG_XML, errorHandler.getException().getMessage() );
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
	
	private HashMap<String,Map<String,Object>> loadBeanConfiguration( Document document ) {
		HashMap<String,Map<String,Object>> bean = new HashMap<String, Map<String,Object>>();
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
	 * Gets the bean.
	 * 
	 * @return the bean
	 */
	public Map<String, Map<String, Object>> getBeanCopy() {
		Map<String, Map<String, Object>> copy = new HashMap<String, Map<String,Object>>();
		for( Map.Entry<String, Map<String, Object>> entry : this.bean.entrySet() ) {
			copy.put(entry.getKey(), new HashMap<String, Object>(entry.getValue()));
		}
		return copy;
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
