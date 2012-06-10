package com.code.aon.common.domain;


import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.common.dao.hibernate.HibernateUtil;

public abstract class AbstractDomainSwitcher implements IDomainSwitcher {

	private static final String DOMAIN_CLASS_NAME = "com.code.aon.config.Domain";
	
	private List<IDomainChangeListener> listeners;
	protected Integer domainId;
	private boolean domainManagementAvailable;
	private boolean parentDomain;
	private Collection<Integer> domainFilter;
	
	@Override
	public Integer getDomainId() {
		return domainId;
	}

	@Override
	public void setDomainId(Integer domainId) {
		Integer oldDomainId = this.domainId;
		fireBeforeDomainChanged(oldDomainId, domainId);
		this.domainId = domainId;
		initializeDomainManagementAvailable();
		initializeDomainFilter();
		fireAfterDomainChanged(oldDomainId, domainId);
	}
	
	@Override
	public boolean isDomainManagementAvailable() {
		return domainManagementAvailable;
	}
	@Override
	public boolean isParentDomain() {
		return parentDomain;
	}
	
	@Override
	public void addDomainChangeListener( IDomainChangeListener listener) {
		if (listeners == null) {
			listeners = new LinkedList<IDomainChangeListener>();
		}
		listeners.add(listener);
	}
	
	@Override
	public void fireBeforeDomainChanged(Integer oldDomain, Integer newDomain) {
		if (listeners != null) {
			for (IDomainChangeListener listener : listeners) {
				DomainEvent event = new DomainEvent(this, oldDomain, newDomain);
				listener.beforeDomainChanged(event);
			}
		}
	}
	
	@Override
	public void fireAfterDomainChanged(Integer oldDomain, Integer newDomain) {
		if (listeners != null) {
			for (IDomainChangeListener listener : listeners) {
				DomainEvent event = new DomainEvent(this, oldDomain, newDomain);
				listener.afterDomainChanged(event);
			}
		}
	}

	private void initializeDomainManagementAvailable() {
		parentDomain = false;
		domainManagementAvailable = false;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(DOMAIN_CLASS_NAME);
		String q = "SELECT d.parent,d.domainManagement FROM domain d"
				+ " WHERE d.id = " + domainId
				+ " AND d.active = 1";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		List<?> queryList = query
				.addScalar("parent", Hibernate.INTEGER)
				.addScalar("domainManagement", Hibernate.BOOLEAN)
				.list();
		Iterator<?> iterator = queryList.iterator();
		
		if (iterator.hasNext()) {
			Object[] arr = (Object[]) iterator.next();
			Integer parent = (Integer) arr[0]; 
			parentDomain = (parent == null);
			domainManagementAvailable = (Boolean) arr[1];
		}
	}
	
	@SuppressWarnings("unchecked")
	private void initializeDomainFilter() {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(DOMAIN_CLASS_NAME);
		String q = "SELECT id FROM domain d"
				+ " WHERE d.id = " + this.domainId
				+ " OR d.parent = " + this.domainId
				+ " AND d.active = 1";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		domainFilter = query.addScalar("id", Hibernate.INTEGER).list();
	}
	
	@Override
	public Collection<Integer> getDomainFilter() {
		return domainFilter;
	}
	
}
