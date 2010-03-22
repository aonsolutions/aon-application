package com.code.aon.ui.audit.controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.dao.IAuditAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class FavoriteOptionController.
 */
public class ActionDeniedController implements IAuditConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ActionDeniedController.class);
	
	private Map<String,ApplicationOption> deniedActionsMap;
	
	private User user;
	
	private  List<ActionDenied> deniedActions;
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> selected;
	
	public ActionDeniedController() {
		User user = UserUtils.getInstance().getLoggedUser();
		this.deniedActionsMap = new HashMap<String, ApplicationOption>();
		for( ApplicationOption option : getOptions(getDeniedActions(user)) ) {
			this.deniedActionsMap.put(option.getAction(), option);
		}
	}

	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
	}
	
	public Map<String, ApplicationOption> getDeniedActionsMap() {
		return deniedActionsMap;
	}

	public void onInit( ActionEvent event ) {
		reset();
	}
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<ApplicationOption> getOptions() {
		return options;
	}

	public void setOptions(List<ApplicationOption> options) {
		this.options = options;
	}

	public List<ApplicationOption> getSelected() {
		return selected;
	}

	public void setSelected(List<ApplicationOption> selected) {
		this.selected = selected;
	}
	
	public void accept( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Map<String,ApplicationOption> map = new HashMap<String, ApplicationOption>();
			for( ApplicationOption option : this.selected ) {
				map.put( option.getAction(), option );
			}
			for( ActionDenied actionDenied : this.deniedActions ) {
				String action = actionDenied.getAction().getName();
				if ( map.containsKey(action) ) {
					map.remove(action);
				} else {
					bean.remove( actionDenied );
				}
			}
			for( ApplicationOption option : map.values() ) {
				ActionDenied actionDenied = new ActionDenied();
				Action action = getOptionController().getAction(option.getAction());
				actionDenied.setAction(action);
				actionDenied.setUser(user);
				bean.insert(actionDenied);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating favorite action list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
	}
	
	@SuppressWarnings("unchecked")
	private List<ActionDenied> getDeniedActions( User user ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IAuditAlias.ACTION_DENIED_USER_ID), user.getId());
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading actions denied", e);
		}
		return null;		
	}
	
	private List<ApplicationOption> getOptions( List<ActionDenied> deniedActions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( ITransferObject to : deniedActions ) {
				String action = ((ActionDenied) to).getAction().getName();
				ApplicationOption option = options.get(action);
				if ( option != null ) {
					list.add(option);
				} else {
					try {
						IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
						bean.remove(to);
						LOGGER.warn( "ActionDenied removed, action {} not found", action );
					} catch (ManagerBeanException e) {
						LOGGER.error( "Error removing action denied " + to, e);
					}
				}
			}
		}
		return list;		
	}
	
	private void reset() {
		this.user = new User();
		this.selected = Collections.emptyList();
		this.options = Collections.emptyList();		
	}
	
	public void userChanged( LookupChangeEvent event ) {
		if ( event.getNewValue() == null ) {
			reset();
		} else {
			this.deniedActions = getDeniedActions( (User) event.getNewValue() );
			this.selected = getOptions( this.deniedActions );
			this.options = new ArrayList<ApplicationOption>( getOptionController().getOptions() );
			this.options.removeAll(this.selected);			
		}
	}

	public void renderedCommand( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			UICommand command = (UICommand) component;
			MethodExpression expression = command.getActionExpression();
			if ( expression != null ) {
				String action = expression.getExpressionString();
				if ( this.deniedActionsMap.containsKey(action) ) {
					parent.setRendered(false);
					component.setRendered(false);
				}				
			}
		}
	}
	
}
