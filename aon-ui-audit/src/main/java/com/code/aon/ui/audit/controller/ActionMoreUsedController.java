package com.code.aon.ui.audit.controller;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.ActionEntry;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.audit.IVisibilityManager;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class ActionMoreUsedController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionMoreUsedController.class);
	
	public List<ActionMoreUsed> getMoreUsed( int maxResults ) {
		List<ActionMoreUsed> list = new LinkedList<ActionMoreUsed>();
		try {
			Integer userId = UserUtils.getInstance().getLoggedUser().getId();
			AuditController ac = (AuditController) AonUtil.getRegisteredBean(IAuditConstants.AUDIT_CONTROLLER_NAME);
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
	        HibernateUtil.closeSession(sessionFactoryName, false);
	        if (! actions.isEmpty() ) {
	        	IVisibilityManager visibilityManager = ((ActionDeniedController)
	        			AonUtil.getRegisteredBean(IAuditConstants.ACTION_DENIED_CONTROLLER_NAME)).getManager();
	        	Map<String,ApplicationOption> options = ApplicationOptionController.getInstance().getOptionMap();
		        for( Object o : actions ) {
		        	Object[] array = (Object[]) o; 
		        	String actionName = (String) array[2];
					ApplicationOption option = options.get(actionName);
					if ( option != null ) {
						if (option.isRendered() && (!visibilityManager.isDenied(option)) ) {
							ActionMoreUsed ams = new ActionMoreUsed( (Integer) array[0], option );
							list.add( ams );
						}
					} else {
						AuditManager.removeAction( (Integer) array[1], actionName );
					}
		        }	        	
	        }
		} catch ( Throwable th ) {
			LOGGER.error( "Error loading more Used", th);
		}
        return list;
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
