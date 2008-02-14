package com.code.aon.db;

import org.dom4j.Element;
import org.hibernate.EntityMode;
import org.hibernate.ReplicationMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

public class EntityImportVisitor  implements IEntityVisitor {

	private int counter;
	
	private int maxExport;
	
	private SessionFactory sessionFactory;
	
	private Session session;
	
	private Session dom4jSession;
	
	private Transaction tx;

	private String className;
	
	@SuppressWarnings("unchecked")
	public EntityImportVisitor(SessionFactory sessionFactory, int maxExport, Class entity) {
		this.sessionFactory = sessionFactory;
		this.maxExport = maxExport;
		this.className = entity.getName();
		initTransaction();
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

	public void visit(Element element) {
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
