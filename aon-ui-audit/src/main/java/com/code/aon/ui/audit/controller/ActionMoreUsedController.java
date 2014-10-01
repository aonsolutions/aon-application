package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.ActionEntry;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;

public class ActionMoreUsedController extends DataScrollerState implements IAuditConstants, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionMoreUsedController.class);
	
	private static final int MORE_USED_COUNT = 5;
	
	public void onSearch( ActionEvent event ) {
		List<ActionMoreUsed> list = getMoreUsed(-1);
		setModel(new SerializableListDataModel(list));
	}

	private ApplicationOptionController getOptionController() {
		return ApplicationOptionController.getInstance();
	}
	
	private ActionDeniedController getDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
	}	
	
	protected List<ActionMoreUsed> getMoreUsed( int maxResults ) {
		List<ActionMoreUsed> list = new LinkedList<ActionMoreUsed>();
		try {
			Integer userId = UserUtils.getInstance().getLoggedUser().getId();
			AuditController ac = (AuditController) AonUtil.getRegisteredBean(AUDIT_CONTROLLER_NAME);
			Integer appId = ac.getApplication().getId();
	    	String sessionFactoryName = HibernateUtil.getSessionFactoryName();
	        Session session = HibernateUtil.getSession(sessionFactoryName);
	        Criteria criteria = session.createCriteria(ActionEntry.class);
	        criteria
	        	.createAlias("action", "aeAction" )
	        	.createAlias("session", "aeSession" )
	        	.add( Restrictions.eq("aeSession.user.id", userId) )
	        	.add( Restrictions.eq("aeSession.application.id", appId) )
	        	.add( Restrictions.eq("aeAction.menu", Boolean.TRUE) )
	        	.setProjection( Projections.projectionList()
	        		.add( Projections.countDistinct("id").as("aeRowCount") )
	        		.add( Projections.groupProperty("aeAction.id") ) 
	        		.add( Projections.groupProperty("aeAction.name") ) )
	        		.addOrder( Order.desc("aeRowCount") );
	        if ( maxResults > 0 ) {
	        	criteria.setMaxResults(maxResults);
	        }
	        LOGGER.info( "Criteria: " + criteria );
	        List<?> actions = criteria.list();
	        HibernateUtil.closeSession(sessionFactoryName);
	        if (! actions.isEmpty() ) {
	        	Map<String,ApplicationOption> options = getOptionController().getOptionMap();
		        for( Object o : actions ) {
		        	Object[] array = (Object[]) o; 
		        	String actionName = (String) array[2];
					ApplicationOption option = options.get(actionName);
					if ( option != null ) {
						if (option.isRendered() && (!getDeniedController().isDenied(option)) ) {
							ActionMoreUsed ams = new ActionMoreUsed( (Integer) array[0], option );
							list.add( ams );
						}
					} else {
						AuditManager.removeAction( (Integer) array[1] );
					}
		        }	        	
	        }
		} catch ( Throwable th ) {
			LOGGER.error( "Error loading more Used", th);
		}
        return list;
	}
	
	public String getTemplate() throws IOException {
		List<ActionMoreUsed> list = getMoreUsed(MORE_USED_COUNT);
		ApplicationOption[] options = new ApplicationOption[list.size()];
		for( int i = 0; i < options.length; i++ ) {
			options[i] = list.get(i).getOption();
		}
		return getOptionController().getTemplate(OPTIONS_TEMPLATE,
					PREFFIX_VM, MORE_USED_PREFFIX,
					OPTIONS_VM, options);
	}

	public class ActionMoreUsed {
		
		private Integer count;
		
		private ApplicationOption option;

		public ActionMoreUsed(Integer count, ApplicationOption option) {
			this.count = count;
			this.option = option;
		}

		public Integer getCount() {
			return count;
		}

		public ApplicationOption getOption() {
			return option;
		}
		
	}
	
}
