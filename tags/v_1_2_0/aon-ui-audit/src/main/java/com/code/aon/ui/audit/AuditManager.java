package com.code.aon.ui.audit;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionExecution;
import com.code.aon.audit.Application;
import com.code.aon.audit.Domain;
import com.code.aon.audit.DomainApplication;
import com.code.aon.audit.Session;
import com.code.aon.audit.User;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.dao.hibernate.IConfigurationFactory;
import com.code.aon.common.dao.hibernate.ISessionFactoryNameProvider;
import com.code.aon.ql.Criteria;

public class AuditManager implements IAuditAlias {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditManager.class);
	
	public static final String AUDIT = "aon-audit";
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.session";	
	
	public static final String AUDIT_DOMAIN_APPLICATION_PROPERTY = "com.code.aon.audit.domainApplication";

	private static final AuditManager SINGLETON = new AuditManager();
	
	private boolean auditConfigured;
	
	private IManagerBean domainBean;
	
	private IManagerBean applicationBean;
	
	private IManagerBean domainApplicationBean;
	
	private IManagerBean userBean;
	
	private IManagerBean sessionBean;
	
	private IManagerBean actionBean;
	
	private IManagerBean actionExecutionBean;
	
	private AuditManager() {
	}
	
	
	public static AuditManager getInstance() {
		return SINGLETON;
	}
	
	private void initManagerBeans() {
		if ( actionExecutionBean == null ) {
			try {
				domainBean = BeanManager.getManagerBean(Domain.class);
				applicationBean = BeanManager.getManagerBean(Application.class);
				domainApplicationBean = BeanManager.getManagerBean(DomainApplication.class);
				userBean = BeanManager.getManagerBean(User.class);
				sessionBean = BeanManager.getManagerBean(Session.class);
				actionBean = BeanManager.getManagerBean(Action.class);
				actionExecutionBean = BeanManager.getManagerBean(ActionExecution.class);
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error initalizing Audit Manager Beans", e );
			}
		}
	}
	
	public boolean isAuditConfigured() {
		return auditConfigured;
	}

	public void configureAudit() {
		ISessionFactoryNameProvider auditNameProvider = new AuditSessionFactoryNameProvider(HibernateUtil.getSessionFactoryNameProvider());
		HibernateUtil.setSessionFactoryNameProvider(auditNameProvider);
		IConfigurationFactory auditConfigurationFactory = new AuditConfigurationFactory(HibernateUtil.getConfigurationFactory());
		HibernateUtil.setConfigurationFactory(auditConfigurationFactory);
		this.auditConfigured = true;
		initManagerBeans();
	}
	
	public Domain getDomain( String name ) throws ManagerBeanException {
		Domain domain = null;
		String field = domainBean.getFieldName(DOMAIN_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( field, name );
		List<ITransferObject> list = domainBean.getList(criteria);
		if ( list.isEmpty() ) {
			domain = new Domain();
			domain.setName( name );
			domainBean.insert( domain );
		} else {
			domain = (Domain) list.get(0);
		}
		return domain;
	}

	public Application getApplication( String name ) throws ManagerBeanException {
		Application application = null;
		String field = applicationBean.getFieldName(APPLICATION_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( field, name );
		List<ITransferObject> list = applicationBean.getList(criteria);
		if ( list.isEmpty() ) {
			application = new Application();
			application.setName( name );
			applicationBean.insert( application );
		} else {
			application = (Application) list.get(0);
		}
		return application;
	}

	public DomainApplication getDomainApplication( Application application, Domain domain ) throws ManagerBeanException {
		DomainApplication domainApplication = null;
		Criteria criteria = new Criteria();
		String applicationField = domainApplicationBean.getFieldName(DOMAIN_APPLICATION_APPLICATION_ID);		
		criteria.addEqualExpression( applicationField, application.getId() );
		String domainField = domainApplicationBean.getFieldName(DOMAIN_APPLICATION_DOMAIN_ID);		
		criteria.addEqualExpression( domainField, domain.getId() );
		List<ITransferObject> list = domainApplicationBean.getList(criteria);
		if ( list.isEmpty() ) {
			domainApplication = new DomainApplication();
			domainApplication.setApplication(application);
			domainApplication.setDomain( domain );
			domainApplication.setAuditLevel(AuditLevel.MODULE);
			domainApplicationBean.insert( domainApplication );
		} else {
			domainApplication = (DomainApplication) list.get(0);
		}
		return domainApplication;
	}
	
	public User getUser( String name, Domain domain ) throws ManagerBeanException {
		User user = null;
		Criteria criteria = new Criteria();
		String loginField = userBean.getFieldName(USER_LOGIN);		
		criteria.addEqualExpression( loginField, name );
		String domainField = userBean.getFieldName(USER_DOMAIN_ID);		
		criteria.addEqualExpression( domainField, domain.getId() );
		List<ITransferObject> list = userBean.getList(criteria);
		if ( list.isEmpty() ) {
			user = new User();
			user.setLogin( name );
			user.setDomain( domain );
			userBean.insert( user );
		} else {
			user = (User) list.get(0);
		}
		return user;
	}
	
	public void insertSession( Session session ) throws ManagerBeanException {
		sessionBean.insert( session );
	}

	public void closeLoginAudit( Session session ) throws ManagerBeanException {
		session.setEndDate( new Date() );
		sessionBean.update( session );
	}

	public Action getAction( String name, Application application ) throws ManagerBeanException {
		Action action = null;
		Criteria criteria = new Criteria();
		String nameField = actionBean.getFieldName(ACTION_NAME);		
		criteria.addEqualExpression( nameField, name );
		String applicationField = actionBean.getFieldName(ACTION_APPLICATION_ID);		
		criteria.addEqualExpression( applicationField, application.getId() );
		List<ITransferObject> list = actionBean.getList(criteria);
		if ( list.isEmpty() ) {
			action = new Action();
			action.setName( name );
			action.setApplication(application);
			actionBean.insert( action );
		} else {
			action = (Action) list.get(0);
		}
		return action;
	}	

	public void createActionExecution( Session session, Action action ) throws ManagerBeanException {
		ActionExecution ae = new ActionExecution();
		ae.setSession( session );
		ae.setAction(action);
		ae.setExecutionDate( new Date() );
		actionExecutionBean.insert( ae );
	}

}
