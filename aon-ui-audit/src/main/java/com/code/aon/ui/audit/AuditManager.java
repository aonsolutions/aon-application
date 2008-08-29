package com.code.aon.ui.audit;

import java.util.Date;
import java.util.List;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionExecution;
import com.code.aon.audit.Application;
import com.code.aon.audit.Domain;
import com.code.aon.audit.DomainApplication;
import com.code.aon.audit.Session;
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
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.session";	
	
	public static final String AUDIT_DOMAIN_APPLICATION_PROPERTY = "com.code.aon.audit.domainApplication";

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

	public DomainApplication getDomainApplication( Application application, Domain domain ) throws ManagerBeanException {
		DomainApplication domainApplication = null;
		IManagerBean bean = BeanManager.getManagerBean(DomainApplication.class);
		Criteria criteria = new Criteria();
		String applicationField = bean.getFieldName(IAuditAlias.DOMAIN_APPLICATION_APPLICATION_ID);		
		criteria.addEqualExpression( applicationField, application.getId() );
		String domainField = bean.getFieldName(IAuditAlias.DOMAIN_APPLICATION_DOMAIN_ID);		
		criteria.addEqualExpression( domainField, domain.getId() );
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			domainApplication = new DomainApplication();
			domainApplication.setApplication(application);
			domainApplication.setDomain( domain );
			bean.insert( domainApplication );
		} else {
			domainApplication = (DomainApplication) list.get(0);
		}
		return domainApplication;
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
	
	public Session createLoginAudit( Application application, User user, String sessionId, Date date ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Session.class);
		Session loginAudit = new Session();
		loginAudit.setApplication( application );
		loginAudit.setUser( user );
		loginAudit.setSessionId( sessionId );
		loginAudit.setStartDate( date );
		bean.insert( loginAudit );
		return loginAudit;
	}

	public void closeLoginAudit( Session loginAudit ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Session.class);
		loginAudit.setEndDate( new Date() );
		bean.update( loginAudit );
	}

	public Action getAction( String name, Application application ) throws ManagerBeanException {
		Action action = null;
		IManagerBean bean = BeanManager.getManagerBean(Action.class);
		Criteria criteria = new Criteria();
		String nameField = bean.getFieldName(IAuditAlias.ACTION_NAME);		
		criteria.addEqualExpression( nameField, name );
		String applicationField = bean.getFieldName(IAuditAlias.ACTION_APPLICATION_ID);		
		criteria.addEqualExpression( applicationField, application.getId() );
		List<ITransferObject> list = bean.getList(criteria);
		if ( list.isEmpty() ) {
			action = new Action();
			action.setName( name );
			action.setApplication(application);
			bean.insert( action );
		} else {
			action = (Action) list.get(0);
		}
		return action;
	}	

	public void createActionExecution( Session session, Action action ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ActionExecution.class);
		ActionExecution ae = new ActionExecution();
		ae.setSession( session );
		ae.setAction(action);
		ae.setExecutionDate( new Date() );
		bean.insert( ae );
	}

}
