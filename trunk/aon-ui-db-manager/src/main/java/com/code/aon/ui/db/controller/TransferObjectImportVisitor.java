package com.code.aon.ui.db.controller;

import java.io.Serializable;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.dom4j.Node;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.engine.Mapping;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.Type;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.db.EntityImportVisitor;

public class TransferObjectImportVisitor extends EntityImportVisitor {
	
	private static final Logger LOGGER = Logger.getLogger(TransferObjectImportVisitor.class.getName());
	
	private boolean previousBeginTransaction;
	
	private boolean previousCloseSession;
	
	private String factoryName;

	public TransferObjectImportVisitor() {
		setMaxImport(0);
	}

	@Override
	protected void entityChanged(Class<? extends Serializable> oldEntity,
			Class<? extends Serializable> newEntity) {
	}

	@Override
	protected void initTransaction() {
		this.previousBeginTransaction = HibernateUtil.mustBeginTransaction();
		this.previousCloseSession = HibernateUtil.mustCloseSession();
		HibernateUtil.setBeginTransaction(false);
		HibernateUtil.setCloseSession(false);
		factoryName = HibernateUtil.getSessionFactoryName();
		try {
			HibernateUtil.beginTransaction(factoryName);
		} catch (DAOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
	}
	
	@Override
	protected void endTransaction() {
		try {
			HibernateUtil.commitTransaction(factoryName);
		} catch (DAOException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
		HibernateUtil.setBeginTransaction(this.previousBeginTransaction);
		HibernateUtil.setCloseSession(this.previousCloseSession);
	}
	
	private void initialize( ITransferObject to, Element element ) {
		SessionFactory sessionFactory = HibernateUtil.getSessionFactory(factoryName); 
		Mapping factory = (Mapping) sessionFactory;
		ClassMetadata cm = sessionFactory.getClassMetadata(to.getClass());
		Iterator i = element.elementIterator();
		while ( i.hasNext() ) {
			Element propertyElement = (Element) i.next();
			String propertyName = propertyElement.getName();
			Type type = null;
			if ( StringUtils.equals(propertyName, cm.getIdentifierPropertyName()) ) {
				type = cm.getIdentifierType();
			} else {
				type = cm.getPropertyType(propertyName);	
			}
			Object value = type.fromXMLNode( propertyElement, factory);
			try {
				PropertyUtils.setProperty(to, propertyName, value);
			} catch (Throwable e) {
				LOGGER.log(Level.SEVERE, e.getMessage(), e);
			}
		}
	}

	@Override
	protected void replicate(Element element, String entityName) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(entityName);
			ITransferObject to = bean.createNewTo();
			initialize(to, element);
			bean.replicate(to);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);			
		}
	}

}
