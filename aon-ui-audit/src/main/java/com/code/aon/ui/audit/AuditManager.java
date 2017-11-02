package com.code.aon.ui.audit;

import static com.esferalia.aon.jooq.tables.Action.ACTION;
import static com.esferalia.aon.jooq.tables.ActionEntry.ACTION_ENTRY;
import static com.esferalia.aon.jooq.tables.DomainApplication.DOMAIN_APPLICATION;
import static com.esferalia.aon.jooq.tables.Session.SESSION;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.Record1;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.ActionFavorite;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.jooq.tables.records.ActionRecord;
import com.esferalia.aon.jooq.tables.records.SessionRecord;
import com.esferalia.aon.occam.api.AONContext;

public class AuditManager implements IAuditConstants {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditManager.class);
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.sessionId";	
	
	public static final String AUDIT_DOMAIN_PROPERTY = "com.code.aon.audit.domainId";
	
	public static final String AUDIT_LEVEL_PROPERTY = "com.code.aon.audit.level";
	
	public static Application getApplication( AuthPrincipal principal ) throws ManagerBeanException {
		IManagerBean applicationBean = BeanManager.getManagerBean(Application.class);
		return (Application) applicationBean.get(principal.getApplicationId());
	}
	
	private static void insertSession( HttpSession httpSession, SessionRecord session, AuditLevel level ) {
		AONContext ctx = getContext(httpSession, session.getDomain());
		try {
			SessionRecord _session = ctx.getDslContext()
					.insertInto(SESSION).set(session).returning(SESSION.ID).fetchOne();
			if ( _session != null ) {
				LOGGER.info( "Session inserted {}", _session.getId() );		
				httpSession.setAttribute( AuditManager.AUDIT_SESSION_PROPERTY, _session.getId() );
				httpSession.setAttribute( AuditManager.AUDIT_DOMAIN_PROPERTY, session.getDomain() );
			}
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();
		}
	}

	public static void closeLoginAudit( HttpSession httpSession ) {
		Integer sessionId = AuditManager.getSessionId(httpSession);
		if ( sessionId != null ) {
			LOGGER.info( "Session finished {}", sessionId );
			AONContext ctx = getContext(httpSession, getDomainId(httpSession));
			try {
				ctx.getDslContext().update(SESSION)
				.set(SESSION.ENDDATE, new java.sql.Timestamp(new Date().getTime()) )
				.where(SESSION.ID.eq(sessionId))
				.execute();	
			} catch ( Throwable th ) {
				LOGGER.error(th.getMessage(), th);
			} finally {
				ctx.finalize();
			}
		}
		httpSession.removeAttribute( AuditManager.AUDIT_LEVEL_PROPERTY );
		httpSession.removeAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
		httpSession.removeAttribute( AuditManager.AUDIT_DOMAIN_PROPERTY );
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
	
	public  static Integer getActionId( String name, Integer domainId, Integer applicationId ) {
		Integer actionId = null;
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), domainId);
		try {
			actionId = ctx.getDslContext()
					.select(ACTION.ID)
					.from(ACTION)
					.where(ACTION.APPLICATION.eq(applicationId).and(
							ACTION.NAME.eq(name)))
					.fetchOne(0, Integer.class);	
			if ( actionId == null ) {
				ActionRecord action = ctx.getDslContext().insertInto(ACTION)
					.set(ACTION.NAME, name)
					.set(ACTION.APPLICATION, applicationId)
					.set(ACTION.MENU, (byte) (isMenuAction(name) ? 1 : 0) )
					.returning(ACTION.ID).fetchOne();
				actionId = (action != null) ? action.getId() : null;
			}
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();	
		}				
		return actionId;
	}	
	
	public static void createActionEntry( Integer sessionId, Integer domainId, Integer actionId ) {
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), domainId);
		try {
			ctx.getDslContext().insertInto(ACTION_ENTRY)
				.set(ACTION_ENTRY.SESSION_ID, sessionId)
				.set(ACTION_ENTRY.ACTION_ID, actionId)
				.set(ACTION_ENTRY.EXECUTIONDATE, new java.sql.Timestamp(new Date().getTime()) )
				.set(ACTION_ENTRY.DOMAIN, domainId)
				.execute();			
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();	
		}				
	}

	private static AuditLevel getAuditLevel( Integer applicationId, int domain ) {
		AuditLevel level = AuditLevel.NONE;
		AONContext ctx = AONContext.getAONContext(AonUtil.getDomainName(), domain);
		try { 
			Record1<Byte> value = ctx.getDslContext()
				.select(DOMAIN_APPLICATION.AUDIT_LEVEL)
				.from(DOMAIN_APPLICATION)
				.where(DOMAIN_APPLICATION.APPLICATION.eq(applicationId))
				.and(DOMAIN_APPLICATION.DOMAIN.eq(domain))
				.fetch()
				.stream()
				.findFirst()
				.orElse(null);
			if (value != null) {
				level = AuditLevel.values()[value.getValue(DOMAIN_APPLICATION.AUDIT_LEVEL)];
			}
		} catch ( Throwable th ) {
			LOGGER.error(th.getMessage(), th);
		} finally {
			ctx.finalize();	
		}				
		return level;
	}	
	
	public static void insertLoginAudit( HttpSession httpSession, HttpServletRequest request, Integer domain, AuthPrincipal principal ) {
		try {
			LOGGER.info( "Domain {}", domain );
			LOGGER.info( "Principal {}", principal );
			Integer applicationId = principal.getApplicationId();
			LOGGER.info( "Application {}", applicationId );
			LOGGER.info( "User {}", principal.getUserId() );		
			AuditLevel level = AuditManager.getAuditLevel(applicationId, domain );
			httpSession.setAttribute( AuditManager.AUDIT_LEVEL_PROPERTY, level );			
			if ( level != AuditLevel.NONE ) {
				SessionRecord session = new SessionRecord();
				session.setDomain( domain );
				session.setApplication( applicationId );
				session.setUserId( principal.getUserId() );
				session.setSessionId( httpSession.getId() );
				session.setStartdate( new Timestamp(httpSession.getCreationTime()) );
				session.setRemoteAddress( request.getRemoteAddr() );
				session.setRemoteHost( request.getRemoteHost() );
				insertSession( httpSession, session, level );				
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
	
	public static void removeAction( Action action ) {
		removeAction(action.getId(), action.getName());
	}
	
	public static void removeAction( Integer id, String actionName ) {
		try {
			FormUtil.remove(ActionFavorite.class, id, true, IEntityAlias.ACTION_FAVORITE_ACTION_ID);
			FormUtil.remove(ActionDenied.class, id, true, IEntityAlias.ACTION_DENIED_ACTION_ID);
			FormUtil.remove(ProfileActionDenied.class, id, true, IEntityAlias.PROFILE_ACTION_DENIED_ACTION_ID);
			FormUtil.remove(ActionEntry.class, id, true, IEntityAlias.ACTION_ENTRY_ACTION_ID);
			IManagerBean bean = BeanManager.getManagerBean(Action.class);
			bean.remove(id);
			LOGGER.warn( "Action not in menu, removed: {} ({})", id, actionName );
		} catch ( Throwable th ) {
			LOGGER.error( "Error deleting action "+ id + ". " + th.getMessage(), th );
		}
	}

	public static AuditLevel getAuditLevel( HttpSession httpSession ) {
		return (AuditLevel) httpSession.getAttribute( AuditManager.AUDIT_LEVEL_PROPERTY );
	}
	
	public static Integer getSessionId( HttpSession httpSession ) {
		return (Integer) httpSession.getAttribute( AuditManager.AUDIT_SESSION_PROPERTY );
	}

	public static Integer getDomainId( HttpSession httpSession ) {
		return (Integer) httpSession.getAttribute( AuditManager.AUDIT_DOMAIN_PROPERTY );
	}
	
	public static AuthPrincipal getAuthPrincipal( HttpSession httpSession ) {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		if ( principal == null ) {
			principal = (AuthPrincipal) httpSession.getAttribute(ICommonConstants.PRINCIPAL_SESSION_PROPERTY);
		}			
		return principal;
	}
	
	private static AONContext getContext( HttpSession httpSession, Integer domainId ) {
		AONContext ctx = null;
		AuthPrincipal principal = getAuthPrincipal(httpSession);
		if ( principal != null ) {
			ctx = AONContext.getAONContext(principal.getDomain(), domainId,principal.getShortName());
		}
		return ctx;
	}
	
}