package com.code.aon.ui.audit;

import java.util.Date;
import java.util.List;

import com.code.aon.audit.Application;
import com.code.aon.audit.Domain;
import com.code.aon.audit.LoginAudit;
import com.code.aon.audit.User;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.ql.Criteria;

public class AuditManager {

	private static final AuditManager SINGLETON = new AuditManager();
	
	private ISessionFactoryNameProvider previousNameProvider;
	
	private AuditConfigurationFactory auditConfigurationFactory;
	
	private IConfigurationFactory previousConfigurationFactory;
	
	private AuditManager() {
	}
	
	public static AuditManager getInstance() {
		return SINGLETON;
	}
	
	private AuditConfigurationFactory getAuditConfigurationFactory() {
		if ( this.auditConfigurationFactory == null ) {
			this.auditConfigurationFactory = new AuditConfigurationFactory();
		}
		return this.auditConfigurationFactory;
	}
	
	public void changeToAuditDB() {
		this.previousNameProvider = HibernateUtil.getSessionFactoryNameProvider();
		this.previousConfigurationFactory = HibernateUtil.getConfigurationFactory();
		ISessionFactoryNameProvider auditNameProvider = new AuditSessionFactoryNameProvider();
		HibernateUtil.setSessionFactoryNameProvider(auditNameProvider);
		getAuditConfigurationFactory().setSessionFactoryName(auditNameProvider.getName());
		HibernateUtil.setConfigurationFactory(getAuditConfigurationFactory());
	}
	
	public void restoreToPreviousDB() {
		HibernateUtil.setSessionFactoryNameProvider(this.previousNameProvider);
		HibernateUtil.setConfigurationFactory(this.previousConfigurationFactory);
	}
	
	public Domain getDomain( String name ) throws ManagerBeanException {
		Domain domain = null;
		IManagerBean bean = BeanManager.getManagerBean(Domain.class);
		String field = bean.getFieldName(IAuditAlias.DOMAIN_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( field, name );
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			domain = new Domain();
			domain.setName( name );
			bean.insert( domain );
		} else {
			domain = (Domain) list.get(0);
		}
		return domain;
	}

	public Application getApplication( String name ) throws ManagerBeanException {
		Application application = null;
		IManagerBean bean = BeanManager.getManagerBean(Application.class);
		String field = bean.getFieldName(IAuditAlias.APPLICATION_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( field, name );
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			application = new Application();
			application.setName( name );
			bean.insert( application );
		} else {
			application = (Application) list.get(0);
		}
		return application;
	}

	public User getUser( String name, Domain domain ) throws ManagerBeanException {
		User user = null;
		IManagerBean bean = BeanManager.getManagerBean(User.class);
		Criteria criteria = new Criteria();
		String loginField = bean.getFieldName(IAuditAlias.USER_LOGIN);		
		criteria.addEqualExpression( loginField, name );
		String domainField = bean.getFieldName(IAuditAlias.USER_DOMAIN_ID);		
		criteria.addEqualExpression( domainField, domain.getId() );
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			user = new User();
			user.setLogin( name );
			user.setDomain( domain );
			bean.insert( user );
		} else {
			user = (User) list.get(0);
		}
		return user;
	}
	
	public LoginAudit createLoginAudit( Application application, User user, String sessionId, Date date ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(LoginAudit.class);
		LoginAudit loginAudit = new LoginAudit();
		loginAudit.setApplication( application );
		loginAudit.setUser( user );
		loginAudit.setSessionId( sessionId );
		loginAudit.setStartDate( date );
		bean.insert( loginAudit );
		return loginAudit;
	}

	public void closeLoginAudit( LoginAudit loginAudit ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(LoginAudit.class);
		loginAudit.setEndDate( new Date() );
		bean.update( loginAudit );
	}
	
}
