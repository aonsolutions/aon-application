package com.code.aon.ui.db.hibernate;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.code.aon.common.dao.hibernate.DefaultConfigurationFactory;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;

public class ReplicateConfigurationFactory extends DefaultConfigurationFactory {

	private static final String TEST_HIBERNATE_PROPERTIES_FILE = "/test.properties";

	private static final Logger LOGGER = Logger.getLogger(ReplicateConfigurationFactory.class.getName());
	
	private IConfigurationFactory defaultFactory;

	private Configuration configuration;
	
	private List<ClassMetadata> entities;

	public ReplicateConfigurationFactory( IConfigurationFactory defaultFactory ) {
		super();
		this.defaultFactory = defaultFactory;		
	}

	public List<ClassMetadata> getEntities() {
		return entities;
	}

	public void setEntities(List<ClassMetadata> entities) {
		this.entities = entities;
	}

	private Document newDocument() {
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dBF.newDocumentBuilder();
			return builder.newDocument();	
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private boolean isIdentifierAssignedByInsert( ClassMetadata cm ) {
		boolean result = false;
		if ( EntityPersister.class.isAssignableFrom(cm.getClass()) ) {
			EntityPersister ep = (EntityPersister) cm;
			result = ep.isIdentifierAssignedByInsert();
		}
		return result;
	}
	
	private Document createDocument() {
		Document document = newDocument();
		Element root = document.createElement("entity-mappings");
		root.setAttribute("version", "1.0");
		document.appendChild(root);

		for( ClassMetadata cm : entities ) {
			if ( isIdentifierAssignedByInsert(cm) ) {
				Element entity = document.createElement("entity");
				entity.setAttribute( "class", cm.getEntityName() );
				root.appendChild(entity);
	
				Element attributes = document.createElement("attributes");
				entity.appendChild(attributes);
	
				Element id = document.createElement("id");
				String idName = cm.getIdentifierPropertyName();
				id.setAttribute( "name", idName );
				attributes.appendChild(id);
				
				Element generatedValue = document.createElement("generated-value");
				generatedValue.setAttribute( "strategy", "TABLE" );
				id.appendChild(generatedValue);
			}
		}		
		return document;
	}
	
	private void addTestProperties(Configuration configuration) {
		Properties properties = new Properties();
		try {
			InputStream in = ReplicateConfigurationFactory.class.getResourceAsStream(TEST_HIBERNATE_PROPERTIES_FILE);
			if ( in != null ) {
				properties.load( in );
				configuration.setProperties(properties);
				in.close();
			}			
		} catch (IOException e) {
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}		
	}
	
	@Override
	protected void completeConfiguration(Configuration configuration) {
		Document document = createDocument();
		Element root = document.getDocumentElement();
		if ( root.hasChildNodes() ) {
			configuration.addDocument(document);	
		}
		addTestProperties(configuration);
	}

	@Override
	public Configuration getConfiguration(String sessionFactoryName) {
		if ( ReplicateSessionFactoryNameProvider.SESSION_FACTORY_NAME.equals(sessionFactoryName) ) {
			if ( configuration == null ) {
				configuration = super.getConfiguration(sessionFactoryName);
			}
			return configuration;
		}
		return this.defaultFactory.getConfiguration(sessionFactoryName);
	}

}
