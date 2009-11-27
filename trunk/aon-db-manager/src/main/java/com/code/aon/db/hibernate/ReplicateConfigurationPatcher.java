package com.code.aon.db.hibernate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.EntityPersister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ReplicateConfigurationPatcher {

	private static final Logger LOGGER = LoggerFactory.getLogger(ReplicateConfigurationPatcher.class.getName());
	
	private List<ClassMetadata> entities;

	public ReplicateConfigurationPatcher( List<ClassMetadata> entities ) {
		this.entities = entities;	
	}

	public ReplicateConfigurationPatcher( SessionFactory sessionFactory ) {
		this( getEntities(sessionFactory) );	
	}
	
	public List<ClassMetadata> getEntities() {
		return entities;
	}

	@SuppressWarnings("unchecked")
	private static List<ClassMetadata> getEntities( SessionFactory sessionFactory ) {
		List<ClassMetadata> entities = new LinkedList<ClassMetadata>();
		Iterator i = sessionFactory.getAllClassMetadata().values().iterator();
		while ( i.hasNext() ) {
			entities.add( (ClassMetadata) i.next() );
		}
		return entities;
	}	
	
	private Document newDocument() {
		DocumentBuilderFactory dBF = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = dBF.newDocumentBuilder();
			return builder.newDocument();	
		} catch (ParserConfigurationException e) {
			LOGGER.error( e.getMessage(), e );
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
	
	public void completeConfiguration(Configuration configuration) {
		Document document = createDocument();
		Element root = document.getDocumentElement();
		if ( root.hasChildNodes() ) {
			configuration.addDocument(document);	
		}
	}

}
