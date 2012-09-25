package com.code.aon.ui.audit;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.config.Application;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.controller.DomainResolver;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditManager implements IAuditConstants {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditManager.class);
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.session";	
	
	public static final String AUDIT_LEVEL_PROPERTY = "com.code.aon.audit.level";
	
	public static final String AUDIT_SESSION_MANAGER_BEAN = "com.code.aon.audit.session.managerBean";
	
	public static Application getApplication( String context ) throws ManagerBeanException {
		String name = DomainResolver.getApplication( context );
		IManagerBean applicationBean = BeanManager.getManagerBean(Application.class);
		String field = applicationBean.getFieldName(IEntityAlias.APPLICATION_NAME);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression( field, name );
		List<ITransferObject> list = applicationBean.getList(criteria);
		if (! list.isEmpty() ) {
			return (Application) list.get(0);
		}
		return null;
	}
	
	private static User getUser( Integer userId ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		return (User) HibernateUtil.getSession(sessionFactoryName).get(User.class, userId);
	}
	
	private static void insertSession( HttpSession httpSession, Session session, AuditLevel level ) throws ManagerBeanException {
		IManagerBean sessionBean = BeanManager.getManagerBean(Session.class);
		sessionBean.insert( session );
		LOGGER.info( "Session inserted {}", session );
		httpSession.setAttribute( AuditManager.AUDIT_LEVEL_PROPERTY, level );
		httpSession.setAttribute( AuditManager.AUDIT_SESSION_PROPERTY, session );
		httpSession.setAttribute( AuditManager.AUDIT_SESSION_MANAGER_BEAN, sessionBean );
	}

	public static void closeLoginAudit( HttpSession httpSession ) throws ManagerBeanException {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {
			IManagerBean sessionBean = (IManagerBean) httpSession.getAttribute( AuditManager.AUDIT_SESSION_MANAGER_BEAN );
			session.setEndDate( new Date() );
			sessionBean.update( session );
			LOGGER.info( "Session finished {}", session.getId() );
			httpSession.removeAttribute( AuditManager.AUDIT_LEVEL_PROPERTY );
			httpSession.removeAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
			httpSession.removeAttribute( AuditManager.AUDIT_SESSION_MANAGER_BEAN );
		}
	}
	
	private static boolean isMenuAction( String name ) {
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		return aoc.getOptionMap().containsKey(name);
	}

	public  static Action getAction( String name, Application application ) throws ManagerBeanException {
		Action action = null;
		IManagerBean actionBean = BeanManager.getManagerBean(Action.class);
		Criteria criteria = new Criteria();
		String nameField = actionBean.getFieldName(IEntityAlias.ACTION_NAME);		
		criteria.addEqualExpression( nameField, name );
		String applicationField = actionBean.getFieldName(IEntityAlias.ACTION_APPLICATION_ID);		
		criteria.addEqualExpression( applicationField, application.getId() );
		List<ITransferObject> list = actionBean.getList(criteria);
		if ( list.isEmpty() ) {
			action = new Action();
			action.setName( name );
			action.setApplication(application);
			action.setMenu(isMenuAction(name));
			actionBean.insert( action );
		} else {
			action = (Action) list.get(0);
		}
		return action;
	}	
	
	public static void createActionEntry( Session session, Action action ) throws ManagerBeanException {
		ActionEntry ae = new ActionEntry();
		ae.setSession( session );
		ae.setAction(action);
		ae.setExecutionDate( new Date() );
		ae.setDomain( session.getDomain() );
		IManagerBean actionEntryBean = BeanManager.getManagerBean(ActionEntry.class);
		actionEntryBean.insert( ae );
		LOGGER.debug( "ActionEntry inserted {}", ae );
	}

	private static AuditLevel getAuditLevel( Application application, int domain ) throws ManagerBeanException {
		AuditLevel level = AuditLevel.NONE;
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		String q = "SELECT dp FROM DomainApplication dp  "
			+ " WHERE dp.application = " + application.getId()
			+ " AND dp.domain = " + domain;
		Query query = HibernateUtil.getSession(sessionFactoryName).createQuery(q);
		List<?> queryList = query.list();
		Iterator<?> iterator = queryList.iterator();
		if (iterator.hasNext()) {
			DomainApplication dp = (DomainApplication) iterator.next();
			level = dp.getAuditLevel();
		}
		return level;
	}	
	
	public static void insertLoginAudit( HttpSession httpSession, HttpServletRequest request, Integer domain, AuthPrincipal principal ) {
		try {
			LOGGER.info( "Domain {}", domain );
			LOGGER.info( "Principal {}", principal );
			Application application = AuditManager.getApplication(request.getContextPath());
			LOGGER.info( "Application {}", application );
			User user = AuditManager.getUser( principal.getUserId() );
			if ( user != null ) {
				LOGGER.info( "User {}", user );		
				AuditLevel level = AuditManager.getAuditLevel(application, domain );
				if ( level != AuditLevel.NONE ) {
					Session session = new Session();
					session.setDomain( domain );
					session.setApplication( application );
					session.setUser( user );
					session.setSessionId( httpSession.getId() );
					session.setStartDate( new Date(httpSession.getCreationTime()) );
					session.setRemoteAddress( request.getRemoteAddr() );
					session.setRemoteHost( request.getRemoteHost() );
					insertSession( httpSession, session, level );				
				}
			} else {
				LOGGER.error( "User {} not found", principal.getShortName() );
			}
		} catch ( Throwable th ) {
			LOGGER.error( "Error login audit", th );
		}
	}	
}
