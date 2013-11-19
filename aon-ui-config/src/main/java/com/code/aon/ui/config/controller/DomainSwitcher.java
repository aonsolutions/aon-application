package com.code.aon.ui.config.controller;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.AbstractDomainSwitcher;
import com.code.aon.common.domain.DomainEvent;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.domain.IDomainChangeListener;
import com.code.aon.config.Domain;
import com.code.aon.config.UserScope;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ui.util.AonUtil;

public class DomainSwitcher extends AbstractDomainSwitcher {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(DomainSwitcher.class);
	private List<IDomainChangeListener> listenerClasses;
	private DataModel model;
	private DataModel filteredModel;
	private Integer parentDomain;
	private String domainName;
	private String filter;
	private String modelFilter;
	private String domainURL;
	private Integer pageLimit;
	
	public DomainSwitcher() {
		try {
			setPageLimit(15);
			super.setDomainId( initializeDomain());
		} catch (Throwable th) {
			super.setDomainId(1);
		}
	}
	
	private Integer initializeDomain() {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		Integer domain = principal.getDomainId();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.parent FROM domain d WHERE d.id = " + domain;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		List<?> queryList = query
				.addScalar("parent", Hibernate.INTEGER)
				.list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			Integer pd = (Integer) iterator.next();
			setParentDomain( pd == null ? domain : null );
			return domain;
		}
		return null;
	}

	public List<IDomainChangeListener> getListenerClasses() {
		return listenerClasses;
	}

	public void setListenerClasses(List<IDomainChangeListener> listenerClasses) {
		this.listenerClasses = listenerClasses;
		DomainEvent event = new DomainEvent(this, null, getDomainId());
		for (IDomainChangeListener listener : this.listenerClasses ) {
			addDomainChangeListener(listener);
			listener.afterDomainChanged(event);
		}
	}

	public String getBeanName() {
		return ConfigConstants.DOMAIN_SWITCHER;
	}
	
	public String getDomainName() {
		if (domainName == null) {
			assignDomainName(getDomainId());
		}
		return domainName;
	}
	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}
	
	public String getFilter() {
		return filter;
	}
	public void setFilter(String filter) {
		this.filter = filter;
	}
	
	public DataModel getFilteredModel() {
		return filteredModel;
	}
	public void setFilteredModel(DataModel filteredModel) {
		this.filteredModel = filteredModel;
	}

	public Integer getParentDomain() {
		return parentDomain;
	}
	public void setParentDomain(Integer parentDomain) {
		this.parentDomain = parentDomain;
	}
	
	public boolean isChildDomain() {
		return !isParentDomain();
	}
	
	public boolean isParentDomainUserInChildDomain() {
		return DomainManager.isParentDomainUserInChildDomain();
	}

	public DataModel getModel() {
		if (model == null) {
			initializeModel();
		}
		if (StringUtils.isBlank(getFilter())) {
			return model;
		} else {
			if (!StringUtils.equals(modelFilter, filter) || filteredModel == null) {
				List<Domain> filteredList = new LinkedList<Domain>();
				@SuppressWarnings("unchecked")
				List<Domain> list = (List<Domain>) model.getWrappedData();
				for (Domain d :  list) {
					if (StringUtils.containsIgnoreCase(d.getName(), getFilter()) ||
						StringUtils.containsIgnoreCase(d.getDescription(), getFilter())) {
						filteredList.add(d);					
					}
				}
				filteredModel = new ListDataModel(filteredList);
				modelFilter = filter;
			}
			return filteredModel;
		}
	}
	
	private List<Integer> getUserScopes() {
		List<Integer> scopes = new LinkedList<Integer>();
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(UserScope.class.getName());
		String q = "SELECT us FROM UserScope us WHERE us.user = " + principal.getUserId();
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
		for( Object o : query.list() ) {
			scopes.add( ((UserScope) o).getScope().getId() );
		}
		return scopes;
	}
	
	private void initializeModel() {
		List<Domain> domains = new LinkedList<Domain>();
		if (getParentDomain() != null) {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
			StringBuffer sb = new StringBuffer(
					"SELECT d FROM Domain d"
					+ " WHERE d.parent = " + getParentDomain() 
					+ " AND d.active = 1"
					+ " AND (d.expirationDate is null OR d.expirationDate > NOW())" );
			if (! isAdminDomain() ) {
				sb.append( " AND (d.scope is null" );
				List<Integer> scopes = getUserScopes();
				if (! scopes.isEmpty() ) {
					sb.append( " or d.scope in (" );
					sb.append( StringUtils.join(scopes, ",") );
					sb.append( ")" );
				}
				sb.append( ")" );
			}
			sb.append(" ORDER BY d.description" );
			Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(sb.toString());
			List<?> queryList = query.list();
			Iterator<?> iterator = queryList.iterator();
			while (iterator.hasNext()) {
				Domain dom = (Domain) iterator.next();
				domains.add(dom);	
			}
		} 
		setModel(new ListDataModel(domains));
	}
	
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	public int getDomainCount() {
		if (getParentDomain() != null) {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
			String q = "SELECT count(d.id) FROM domain d"
					+ " WHERE d.parent = " +  getParentDomain()
					+ " AND (d.expirationDate is null OR d.expirationDate > NOW())"
					+ " AND d.active = 1";
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
			BigInteger count = (BigInteger) query.uniqueResult();
			return count.intValue();	
		}
		return 0;
	}
	
	public void onEditSearch(ActionEvent event){
		setModel(null);
		setFilter(null);
		setFilteredModel(null);
	}
	
	public void onUpperDomain(ActionEvent event){
		select(AonUtil.getAuthPrincipal().getDomainId(), null);
	}
	
	public void select(Integer id, String name){
		super.setDomainId(id);
		setDomainName(name);
		LOGGER.info("Domain swicthed. New domain: '{}' - '{}'", id, name);
		FacesContext ctx = FacesContext.getCurrentInstance();
		ExternalContext ec = ctx.getExternalContext();
		Map<String,Object> map = ec.getSessionMap();
		for (String key: map.keySet()) {
			String className = map.get(key).getClass().getName();
			if (isRemovable(key, className)) {
				map.remove(key);
				LOGGER.info("Element removed from session: [ key: {}, value class: {} ]", key, className);
			}
		}
		this.onEditSearch(null);
		this.domainURL = null;
		System.gc();
	}

	private void assignDomainName(Integer domainId) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(Domain.class.getName());
		String q = "SELECT d.description FROM domain d"
				+ " WHERE d.id = " + domainId;
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(q);
		String name = (String) query
				.addScalar("description", Hibernate.STRING)
				.uniqueResult();
		setDomainName(name);
	}
	
	private boolean isRemovable(String key, String className) {
		return ((StringUtils.startsWith(className, "com.code.aon")  
			&& !StringUtils.startsWith(key, "com.code.aon.audit.") 
			&& !StringUtils.startsWith(className, "com.code.aon.ui.audit.controller.ApplicationOptionController")
			&& !StringUtils.startsWith(className, "com.code.aon.ui.resources.bean.ResourceResolver")
			&& !StringUtils.startsWith(className, "com.code.aon.ui.common.controller.LoggedUser")
			&& !StringUtils.equals(className, this.getClass().getName()))
			|| StringUtils.startsWith(className, "com.esferalia.aon")
			|| StringUtils.endsWith(key, "OptionalListeners") );
	}
	
	public DomainType getType() {
		return DomainType.values()[this.type];
	}
	
	public String getDomainURL() throws ManagerBeanException {
		if ( domainURL == null ) {
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			Domain domain = (Domain) bean.get(getDomainId());
			String path = FacesContext.getCurrentInstance().getExternalContext().getRequestContextPath();
			domainURL = "http://" + domain.getName() + path;
		}
		return domainURL;
	}	

	public boolean isEnabledGoToParent() {
		if ( isParentDomainUserInChildDomain() || isAdminDomain() ) {
			return ! ObjectUtils.equals(getDomainId(), AonUtil.getAuthPrincipal().getDomainId());
		}
		return false;
	}
	
	public boolean isAdminDomain() {
		DomainType type = getDomainType(AonUtil.getAuthPrincipal().getDomainId());
		return type == DomainType.ADMIN;
	}

	public static DomainType getDomainType( Integer domainId ) {
		String sfn = HibernateUtil.getSessionFactoryName();
		Query query = HibernateUtil.getSession(sfn).createQuery("SELECT d.type FROM Domain d WHERE d.id = ?");
		query.setInteger(0, domainId);
		return (DomainType) query.uniqueResult();
	}

	public Integer getPageLimit() {
		return pageLimit;
	}

	public void setPageLimit(Integer pageLimit) {
		this.pageLimit = pageLimit;
	}
	
}