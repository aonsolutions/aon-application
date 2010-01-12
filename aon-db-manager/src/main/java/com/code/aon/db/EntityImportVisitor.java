package com.code.aon.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class EntityImportVisitor implements IEntityVisitor {

	private int counter;
	
	private int maxImport;
	
	private SessionFactory sessionFactory;
	
	private Session session;
	
	private Session dom4jSession;
	
	private Transaction tx;

	private String entityName;
	
	private Class<? extends Serializable> lastEntity;
	
	private Set<String> notNullableStringProperties;

	public EntityImportVisitor(SessionFactory sessionFactory, int maxImport) {
		this.notNullableStringProperties = new HashSet<String>();
		setSessionFactory(sessionFactory);
		setMaxImport(maxImport);
	}
	
	public int getMaxImport() {
		return maxImport;
	}

	public void setMaxImport(int maxImport) {
		this.maxImport = maxImport;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private void setEntity(Class<? extends Serializable> entity) {
		if ( this.lastEntity != entity ) {
			this.entityName = entity.getName();
			entityChanged(this.lastEntity, entity);
			this.lastEntity = entity;
		}
	}

	private void initNotNullableStringProperties(Class<? extends Serializable> entity) {
		notNullableStringProperties.clear();
		ClassMetadata cmd = getSessionFactory().getClassMetadata(entity);
		String[] names = cmd.getPropertyNames();
		boolean[] nullables = cmd.getPropertyNullability();
		for( int i = 0; i < names.length; i++ ) {
			if ( !nullables[i] ) {
				notNullableStringProperties.add( names[i] );
			}
		}
	}
	
	protected void entityChanged( Class<? extends Serializable> oldEntity, Class<? extends Serializable> newEntity ) {
		initNotNullableStringProperties(newEntity);
	}
	
	protected void initTransaction() {
		session = sessionFactory.openSession();
		dom4jSession = session.getSession(EntityMode.DOM4J);
		tx = session.beginTransaction();
	}

	protected void endTransaction() {
		tx.commit();
		session.close();
	}
	
	protected boolean isNotNullable( String propertyName ) {
		return this.notNullableStringProperties.contains( propertyName );
	}
	
	protected void replicate( Element element, String entityName ) throws EntityProcessException {
		try {
			patch(element);
			dom4jSession.replicate( entityName, element, ReplicationMode.EXCEPTION );
		} catch ( Throwable th ) {
			throw new EntityProcessException( th.getMessage(), th );
		}
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
	
	public void visit( Element element, Class<? extends Serializable> entity ) throws EntityProcessException {
		setEntity(entity);
		replicate(element, entityName);
    	if ( (maxImport != 0) && (++counter == maxImport) ) {
			counter = 0;
			endTransaction();
			initTransaction();
    	}
	}

	public void endDocument() {
		endTransaction();
	}
	
}
