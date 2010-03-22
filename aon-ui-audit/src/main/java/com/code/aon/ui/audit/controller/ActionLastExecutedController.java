package com.code.aon.ui.audit.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.ActionEntry;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;

public class ActionLastExecutedController extends BasicController implements IAuditConstants {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionLastExecutedController.class);

	private static final int LAST_EXECUTED_COUNT = 5;
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}

	private ActionDeniedController getDeniedController() {
		return (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
	}
	
	@SuppressWarnings("unchecked")
	private List<ActionEntry> getLastExecutedActions( int count ) {
		try {
			IManagerBean bean = getManagerBean();
			return (List) bean.getList(getCriteria(), 0, count);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading last executed actions", e);
		}
		return null;		
	}
	
	private List<ApplicationOption> getLastExecuted( List<ActionEntry> actions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! actions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			Map<String,ApplicationOption> denied = getDeniedController().getDeniedActionsMap();
			for( ITransferObject to : actions ) {
				String action = ((ActionEntry) to).getAction().getName();
				if ( denied.containsKey(action) ) {
					LOGGER.warn( "Action {} is denied", action );
				} else {
					ApplicationOption option = options.get(action);
					if ( option != null ) {
						list.add(option);
					} else {
						LOGGER.warn( "Action {} not found in the menu", action );
					}					
				}
			}
		}
		return list;		
	}
	
	public String getTemplate() throws IOException {
		List<ActionEntry> actions = getLastExecutedActions(LAST_EXECUTED_COUNT);
		List<ApplicationOption> options = getLastExecuted(actions);
		return getOptionController().getTemplate(LAST_EXECUTED_TEMPLATE,
				OPTIONS_VM, options,
				ACTIONS_VM, actions );
	}	

	private ActionEntry getCurrentActionEntry() {
		return (ActionEntry) getSelectedTO();
	}
	
	private ApplicationOption getOption() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			String action = getCurrentActionEntry().getAction().getName();
			return getOptionController().getOptionMap().get(action);
		}
		return null;		
	}
	
	public String getDescription() throws ManagerBeanException {
		ApplicationOption option = getOption();
		return (option != null) ? option.getDescription() : getCurrentActionEntry().getAction().getName();
	}

	public ApplicationCategory getCategory() throws ManagerBeanException {
		ApplicationOption option = getOption();
		return (option != null) ? option.getCategory() : null; 
	}

}
