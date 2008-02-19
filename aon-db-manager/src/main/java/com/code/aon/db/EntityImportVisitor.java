package com.code.aon.db;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.EntityMode;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class EntityImportVisitor  implements IEntityVisitor {

	private static Log LOGGER = LogFactory.getLog(EntityImportVisitor.class.getName());
	
	private int counter;
	
	private int maxExport;
	
	private SessionFactory sessionFactory;
	
	private Session session;
	
	private Session dom4jSession;
	
	private Transaction tx;

	private String className;
	
	private List<String> notNullableStringProperties;
	
	@SuppressWarnings("unchecked")
	public EntityImportVisitor(SessionFactory sessionFactory, int maxExport, Class entity) {
		this.sessionFactory = sessionFactory;
		this.maxExport = maxExport;
		this.className = entity.getName();
		initNotNullableStringProperties(sessionFactory, entity);
		initTransaction();
	}
	
	private void initNotNullableStringProperties(SessionFactory sessionFactory, Class entity) {
		notNullableStringProperties = new ArrayList<String>();
		ClassMetadata cmd = sessionFactory.getClassMetadata(entity);
		String[] names = cmd.getPropertyNames();
		Type[] types = cmd.getPropertyTypes();
		boolean[] nullables = cmd.getPropertyNullability();
		for( int i = 0; i < names.length; i++ ) {
			if ( (!nullables[i]) && types[i].getClass().isAssignableFrom(StringType.class) ) {
				notNullableStringProperties.add( names[i] );
			}
		}
	}
	
	private void initTransaction() {
		session = sessionFactory.openSession();
		dom4jSession = session.getSession(EntityMode.DOM4J);
		tx = session.beginTransaction();
	}

	private void endTransaction() {
		tx.commit();
		session.close();
	}

	public void startDocument() {
		initTransaction();
	}

	private void patch( Element element ) {
		for( String property : this.notNullableStringProperties ) {
			Element e = element.element(property);
			if ( (e != null) && StringUtils.isEmpty(e.getText()) ) {
				e.addText(" ");
			}
		}
	}
	
	public void visit(Element element) {
		patch(element);
		dom4jSession.replicate( className, element, ReplicationMode.EXCEPTION );
    	if ( ++counter == maxExport ) {
			counter = 0;
			endTransaction();
			initTransaction();
    	}
	}

	public void endDocument() {
		endTransaction();
	}
	
}
