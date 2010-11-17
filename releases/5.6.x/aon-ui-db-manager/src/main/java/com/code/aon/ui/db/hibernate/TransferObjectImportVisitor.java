package com.code.aon.ui.db.hibernate;

import java.io.Serializable;
import java.util.Iterator;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.Element;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.engine.Mapping;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.type.EntityType;
import org.hibernate.type.Type;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.ReplicationMode;
import com.code.aon.common.dao.sql.DAOException;
import com.code.aon.db.EntityImportVisitor;
import com.code.aon.db.EntityProcessException;

public class TransferObjectImportVisitor extends EntityImportVisitor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(TransferObjectImportVisitor.class.getName());
	
	private boolean previousBeginTransaction;
	
	private boolean previousCloseSession;
	
	private String factoryName;
	
	private IManagerBean bean;
	
	private String lastEntityName;
	
	private ReplicationMode replicationMode;

	public TransferObjectImportVisitor(SessionFactory sessionFactory, int maxImport) {
		super(sessionFactory, maxImport);
		this.replicationMode = ReplicationMode.EXCEPTION;
	}
	
	public void setReplicationMode( ReplicationMode mode ) {
		this.replicationMode = mode;
	}

	@Override
	protected void entityChanged(Class<? extends Serializable> oldEntity,
			Class<? extends Serializable> newEntity) {
	}

	@Override
	protected void initTransaction() {
		factoryName = HibernateUtil.getSessionFactoryName();
		this.previousCloseSession = HibernateUtil.mustCloseSession();
		HibernateUtil.setCloseSession(false);
		this.previousBeginTransaction = HibernateUtil.mustBeginTransaction(); 
		HibernateUtil.setBeginTransaction(false);
		try {
			HibernateUtil.beginTransaction(factoryName);
		} catch (DAOException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	@Override
	protected void endTransaction() {
		try {
			HibernateUtil.commitTransaction(factoryName);
		} catch (DAOException e) {
			LOGGER.error(e.getMessage(), e);
		}
		HibernateUtil.setBeginTransaction(this.previousBeginTransaction);
		HibernateUtil.setCloseSession(this.previousCloseSession);
	}
	
	private void initialize( ITransferObject to, Element element ) throws EntityProcessException {
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
			if (! type.isCollectionType() ) {
				Object value = type.fromXMLNode(propertyElement, factory);
				if ( type.isEntityType() ) {
					EntityType et = (EntityType) type;
					Session session = HibernateUtil.getSession(factoryName);
					value = session.get(et.getReturnedClass(), (Serializable) value);
				}
				if ( value != null ) {
					try {
						PropertyUtils.setProperty(to, propertyName, value);
					} catch (Throwable th) {
						LOGGER.error( "Error in set property " + propertyName + " value " + value + " for element " + element );
						throw new EntityProcessException( th.getMessage(), th );
					}
				}
			}
		}
	}
	
	private IManagerBean getManagerBean( String entityName ) throws ManagerBeanException {
		if (! StringUtils.equals(lastEntityName, entityName) ) { 
			LOGGER.info( "Importing entity " + entityName );
			this.bean = BeanManager.getManagerBean(entityName);
			this.lastEntityName = entityName;
		}		
		return this.bean;
	}

	@Override
	protected void replicate(Element element, String entityName) throws EntityProcessException {
		try {
			IManagerBean bean = getManagerBean(entityName);
			ITransferObject to = (ITransferObject) bean.getPOJOClass().newInstance();
			initialize(to, element);
			bean.replicate(to, replicationMode);
		} catch (Throwable th) {
			LOGGER.error( "Error in replicate " + entityName + ": " + element );
			throw new EntityProcessException( th.getMessage(), th );
		}
	}

}
