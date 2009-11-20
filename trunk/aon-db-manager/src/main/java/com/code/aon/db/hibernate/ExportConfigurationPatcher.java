package com.code.aon.db.hibernate;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ExportConfigurationPatcher implements IConfigurationPatcher {

	private static final Logger LOGGER = Logger.getLogger(ExportConfigurationPatcher.class.getName());
	
	private List<ClassMetadata> entities;

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
			LOGGER.log( Level.SEVERE, e.getMessage(), e );
		}
		return null;
	}
	
	private List<String> getCollections( ClassMetadata cm ) {
		List<String> list = new LinkedList<String>();
		String[] names = cm.getPropertyNames();
		Type[] types = cm.getPropertyTypes();
		for( int i = 0; i < names.length; i++ ) {
			if ( types[i].isCollectionType() ) {
				list.add( names[i] );
			}
		}
		return list;
	}
	
	private Document createDocument() {
		Document document = newDocument();
		Element root = document.createElement("entity-mappings");
		root.setAttribute("version", "1.0");
		document.appendChild(root);

		for( ClassMetadata cm : entities ) {
			List<String> collections = getCollections(cm);
			if (! collections.isEmpty() ) {
				Element entity = document.createElement("entity");
				entity.setAttribute( "class", cm.getEntityName() );
				root.appendChild(entity);
	
				Element attributes = document.createElement("attributes");
				entity.appendChild(attributes);
	
				for( String name : collections ) {
					Element _transient = document.createElement("transient");
					_transient.setAttribute( "name", name );
					attributes.appendChild(_transient);					
				}
			}
		}		
		return document;
	}
	
	@Override
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.entities = getEntities(sessionFactory);
	}

	@Override
	public void completeConfiguration( Configuration configuration) {
		Document document = createDocument();
		Element root = document.getDocumentElement();
		if ( root.hasChildNodes() ) {
			configuration.addDocument(document);	
		}
	}

}
