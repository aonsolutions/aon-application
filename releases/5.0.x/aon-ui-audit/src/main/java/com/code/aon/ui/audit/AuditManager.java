package com.code.aon.ui.audit;

import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionEntry;
import com.code.aon.audit.Application;
import com.code.aon.audit.Session;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.audit.enumeration.AuditLevel;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.audit.controller.IAuditConstants;
import com.code.aon.ui.util.AonUtil;

public class AuditManager implements IAuditAlias, IAuditConstants {
	
	/** Obtiene un logger apropiado. */
	private final static Logger LOGGER = LoggerFactory.getLogger(AuditManager.class);
	
	public static final String AUDIT_SESSION_PROPERTY = "com.code.aon.audit.session";	

	private static final AuditManager SINGLETON = new AuditManager();
	
	private boolean auditConfigured;
	
	private IManagerBean applicationBean;
	
	private IManagerBean sessionBean;
	
	private IManagerBean actionBean;
	
	private IManagerBean actionEntryBean;
	
	private AuditManager() {
	}
	
	
	public static AuditManager getInstance() {
		return SINGLETON;
	}
	
	public void configureAudit() {
		if (! this.auditConfigured ) {
			try {
				applicationBean = BeanManager.getManagerBean(Application.class);
				sessionBean = BeanManager.getManagerBean(Session.class);
				actionBean = BeanManager.getManagerBean(Action.class);
				actionEntryBean = BeanManager.getManagerBean(ActionEntry.class);
				this.auditConfigured = true;
			} catch (ManagerBeanException e) {
				LOGGER.error( "Error initalizing Audit Manager Beans", e );
			}
		}
	}
	
	public boolean isAuditConfigured() {
		return auditConfigured;
	}
	
	public String getApplicationName( String context ) {
		String application = context;
		if ( application.startsWith("/") ) {
			application = application.substring(1);
		}
		int pos = application.lastIndexOf(".");
		if ( pos != -1 ) {
			application = application.substring(0, pos);
		}
		return application;
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
			application.setAuditLevel(AuditLevel.MODULE);
			applicationBean.insert( application );
		} else {
			application = (Application) list.get(0);
		}
		return application;
	}

	public Application getApplication( AuthPrincipal principal ) throws ManagerBeanException {
		String applicationName = getApplicationName(principal.getContext() );
		return getApplication(applicationName);
	}
	
	public User getUser( String shortName ) {
		try {
            IManagerBean bean = BeanManager.getManagerBean(User.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression( bean.getFieldName(IConfigAlias.USER_LOGIN), shortName );
            List<ITransferObject> list = bean.getList(criteria);
            if ( (list!=null) && (list.size() == 1) ) {
                return (User) list.get(0);
            }
        } catch (ManagerBeanException e) {
        	LOGGER.error( "Error obtaining the USER related with the logged user: " + shortName, e);
        }
        return null;		
	}
	
	
	public void insertSession( Session session ) throws ManagerBeanException {
		sessionBean.insert( session );
	}

	public void closeLoginAudit( Session session ) throws ManagerBeanException {
		session.setEndDate( new Date() );
		sessionBean.update( session );
	}
	
	private boolean isMenuAction( String name ) {
		ApplicationOptionController aoc = (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
		return aoc.getOptionMap().containsKey(name);
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
			action.setMenu(isMenuAction(name));
			actionBean.insert( action );
		} else {
			action = (Action) list.get(0);
		}
		return action;
	}	
	
	public void createActionEntry( Session session, Action action ) throws ManagerBeanException {
		ActionEntry ae = new ActionEntry();
		ae.setSession( session );
		ae.setAction(action);
		ae.setExecutionDate( new Date() );
		actionEntryBean.insert( ae );
	}

}
