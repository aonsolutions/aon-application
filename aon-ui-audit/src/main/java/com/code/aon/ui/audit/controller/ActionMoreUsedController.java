package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

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
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

public class ActionMoreUsedController implements IAuditConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionMoreUsedController.class);
	
	private static final int MORE_USED_COUNT = 5;
	
	private String beanName;
	
	private DataModel model;
	
	public void onSearch( ActionEvent event ) {
		List<ActionMoreUsed> list = getMoreUsed(-1);
		this.model = new ListDataModel( list );
	}
	
	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public DataModel getModel() {
		return model;
	}

	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}
	
	private ActionDeniedController getDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
	}	
	
	private List<ActionMoreUsed> getMoreUsed( int maxResults ) {
		List<ActionMoreUsed> list = new LinkedList<ActionMoreUsed>();
		try {
			Integer userId = UserUtils.getInstance().getLoggedUser().getId();
			Integer appId = getOptionController().getApplication().getId();
	    	String name = HibernateUtil.getSessionFactoryName();
	        Session session = HibernateUtil.getSession(name);
	        Criteria criteria = session.createCriteria(ActionEntry.class);
	        criteria
	        	.createAlias("action", "aeAction" )
	        	.createAlias("session", "aeSession" )
	        	.add( Restrictions.eq("aeSession.user.id", userId) )
	        	.add( Restrictions.eq("aeSession.application.id", appId) )
	        	.add( Restrictions.eq("aeAction.menu", Boolean.TRUE) )
	        	.setProjection( Projections.projectionList()
	        		.add( Projections.countDistinct("id").as("aeRowCount") )
	        		.add( Projections.groupProperty("aeAction.name") ) )
	        		.addOrder( Order.desc("aeRowCount") );
	        if ( maxResults > 0 ) {
	        	criteria.setMaxResults(maxResults);
	        }
	        List<?> actions = criteria.list();
	        if (! actions.isEmpty() ) {
	        	Map<String,ApplicationOption> options = getOptionController().getOptionMap();
	        	Map<String,ApplicationOption> denied = getDeniedController().getDeniedActionsMap();
		        for( Object o : actions ) {
		        	Object[] array = (Object[]) o; 
		        	String action = (String) array[1];
					if ( denied.containsKey(action) ) {
						LOGGER.warn( "Action {} is denied", action );
					} else {
						ApplicationOption option = options.get(action);
						if ( option != null ) {
							ActionMoreUsed ams = new ActionMoreUsed( (Integer) array[0], option );
							list.add( ams );
						} else {
							LOGGER.warn( "Action {} not found in the menu", action );
						}
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
