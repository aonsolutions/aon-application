package com.code.aon.ui.audit;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.Session;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.form.FormUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class AuditManager implements IAuditConstants {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditManager.class);
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.session";	
	
	public static final String AUDIT_LEVEL_PROPERTY = "com.code.aon.audit.level";
	
	public static final String AUDIT_SESSION_MANAGER_BEAN = "com.code.aon.audit.session.managerBean";
	
	public static Application getApplication( AuthPrincipal principal ) throws ManagerBeanException {
		IManagerBean applicationBean = BeanManager.getManagerBean(Application.class);
		return (Application) applicationBean.get(principal.getApplicationId());
	}
	
	private static User getUser( Integer userId ) {
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(User.class.getName());
		User user = (User) HibernateUtil.getSession(sessionFactoryName).get(User.class, userId);
		HibernateUtil.closeSession(sessionFactoryName, false);
		return user;
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
		Session session = AuditManager.getSession(httpSession);
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
		ApplicationOptionController aoc = ApplicationOptionController.getInstance();
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
		HibernateUtil.closeSession(sessionFactoryName, false);
		return level;
	}	
	
	public static void insertLoginAudit( HttpSession httpSession, HttpServletRequest request, Integer domain, AuthPrincipal principal ) {
		try {
			LOGGER.info( "Domain {}", domain );
			LOGGER.info( "Principal {}", principal );
			Application application = AuditManager.getApplication(principal);
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
	
	public static boolean hasModule( Integer domainId, Integer applicationId, Module module ) throws ManagerBeanException {
		Integer da = AdminUtil.getDomainApplication(domainId, applicationId);
		IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID), da);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_MODULE), module);
		return bean.getCount(criteria) > 0;
	}	

	private static boolean isDomainManagementAvailable( Integer domainId ) throws ManagerBeanException {
		String sfn = HibernateUtil.getSessionFactoryName();
		Query query = HibernateUtil.getSession(sfn).createQuery("SELECT d.domainManagement FROM Domain d WHERE d.id = ?");
		query.setInteger(0, domainId);
		Boolean available = (Boolean) query.uniqueResult();
		HibernateUtil.closeSession(sfn, false);
		return available;
	}
	
	public static List<Module> getVisibleModules( Integer domainId, Integer applicationId ) throws ManagerBeanException {
		List<Module> list = new LinkedList<Module>();
		DomainType type = DomainSwitcher.getDomainType(domainId);
		if ( type != DomainType.ADMIN ) {
			if ( isDomainManagementAvailable(domainId) && (type == DomainType.CONSULTANCY)) {
				list.add(Module.FISCAL);
				list.add(Module.PAYROLL);
				list.add(Module.PAYROLL_PORTAL);
				list.add(Module.DOCUMENT_PORTAL);
				list.add(Module.CONTRATA);
			} else {
				list.add(Module.ACCOUNTING);
				list.add(Module.COMMERCIAL);
				list.add(Module.GROUPWARE);
				list.add(Module.MANAGEMENT);
				list.add(Module.MARKETING);
				list.add(Module.TREASURY);
				list.add(Module.INFOWEB);
				switch ( type ) {
					case ENTERPRISE:
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case GARAGE:
						list.add(Module.GARAGE);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case ACADEMY:
						list.add(Module.ACADEMY);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case HOTEL:
						list.add(Module.HOTEL);
						list.add(Module.WAREHOUSE);
						list.add(Module.POS);
						break;
					case CONSULTANCY:
						list.add(Module.FISCAL);
						list.add(Module.PAYROLL);
						break;
				}
				Integer parentDomainId = AdminUtil.getParentDomain(domainId);
				if ( parentDomainId != null)  {
					if (DomainSwitcher.getDomainType(parentDomainId) == DomainType.CONSULTANCY) {
						if ( !list.contains(Module.PAYROLL) && hasModule(parentDomainId, applicationId, Module.PAYROLL) ) {
							list.add(Module.PAYROLL);
						}
						if (hasModule(parentDomainId, applicationId, Module.PAYROLL_PORTAL) ) {
							list.add(Module.PAYROLL_PORTAL);
						}
						if (hasModule(parentDomainId, applicationId, Module.DOCUMENT_PORTAL) ) {
							list.add(Module.DOCUMENT_PORTAL);
						}
						if (hasModule(parentDomainId, applicationId, Module.CONTRATA) ) {
							list.add(Module.CONTRATA);
						}
					}
				}			
			}
			list.add(Module.DOCUMENT);
		}
		return list;
	}
	
	public static void removeAction( Integer id ) {
		try {
			FormUtil.remove(ActionFavorite.class, id, true, IEntityAlias.ACTION_FAVORITE_ACTION_ID);
			FormUtil.remove(ActionDenied.class, id, true, IEntityAlias.ACTION_DENIED_ACTION_ID);
			FormUtil.remove(ProfileActionDenied.class, id, true, IEntityAlias.PROFILE_ACTION_DENIED_ACTION_ID);
			FormUtil.remove(ActionEntry.class, id, true, IEntityAlias.ACTION_ENTRY_ACTION_ID);
			IManagerBean bean = BeanManager.getManagerBean(Action.class);
			bean.remove(id);
			LOGGER.warn( "Action not in menu, removed: {}", id );
		} catch ( Throwable th ) {
			LOGGER.error( "Error deleting action {}. " + th.getMessage(), th );
		}
	}
	
	public static Session getSession( HttpSession httpSession ) {
		Session session = (Session) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		if ( session != null ) {
			if (! session.getSessionId().equals(httpSession.getId()) ) {
				LOGGER.warn( "Session id changed, stored {}, current {}", session.getSessionId(), httpSession.getId());
			}
		}		
		return session;
	}
	
}