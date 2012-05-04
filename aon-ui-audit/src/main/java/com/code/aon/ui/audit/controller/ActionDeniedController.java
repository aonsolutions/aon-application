package com.code.aon.ui.audit.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.DomainApplication;
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.audit.event.UserLoookupListener;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * The Class FavoriteOptionController.
 */
public class ActionDeniedController implements IAuditConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ActionDeniedController.class);
	
	private final static String UTILITIES_CATEGORY = "utilities";
	
	private final static String[] SKIP_CATEGORIES = new String[]{UTILITIES_CATEGORY};
	
	private Map<String,ApplicationOption> deniedActionsMap;
	
	private Map<String,ApplicationCategory> deniedModulesMap;
	
	private User user;
	
	private  List<ActionDenied> deniedActions;
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> selected;
	
	private IControllerListener listener;
	
	public ActionDeniedController() {
		initDeniedModules();
		User user = UserUtils.getInstance().getLoggedUser();
		this.deniedActionsMap = new HashMap<String, ApplicationOption>();
		for( ApplicationOption option : getOptions(getDeniedActions(user)) ) {
			this.deniedActionsMap.put(option.getAction(), option);
		}
		if ( AonUtil.isSkipLdap() ) {
			this.listener = new UserLoookupListener();	
		}
	}

	private AuditController getAuditController() {
		return (AuditController) AonUtil.getRegisteredBean(AUDIT_CONTROLLER_NAME);
	}
	
	private ApplicationOptionController getOptionController() {
		return (ApplicationOptionController) AonUtil.getRegisteredBean(APPLICATION_OPTION_CONTROLLER_NAME);
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
				Action action = getAuditController().getAction(option.getAction());
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
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ActionDenied> getDeniedActions( User user ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_USER_ID), user.getId());
			Application application = getAuditController().getApplication();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_ACTION_APPLICATION_ID), application.getId());			
			return (List) bean.getList(criteria);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading actions denied", e);
		}
		return null;		
	}

	private Map<String,Module> getEnabledModules() {
		Map<String,Module> enabledModules = new HashMap<String, Module>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			DomainApplication da = getAuditController().getDomainApplication();
			String filed = bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID);
			criteria.addEqualExpression( filed, da.getId());
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				enabledModules.put( dam.getModule().getName(), dam.getModule() );
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading modules denied", e);
		}
		return enabledModules;		
	}
	
	private boolean isDeniedOption( ApplicationOption option ) {
		return this.deniedModulesMap.containsValue(option.getGroup().getCategory());
	}
	
	public boolean isDenied( ApplicationOption option ) {
		if ( isDeniedOption(option) ) {
			return true;
		}
		return deniedActionsMap.containsKey(option.getAction());
	}
	
	public Collection<ApplicationOption> getDeniedOptions() {
		return this.deniedActionsMap.values();
	}
	
	private List<ApplicationOption> getOptions( List<ActionDenied> deniedActions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( ITransferObject to : deniedActions ) {
				String action = ((ActionDenied) to).getAction().getName();
				ApplicationOption option = options.get(action);
				if ( (option != null) && (!isDeniedOption(option)) ) {
					list.add(option);
				} else {
					try {
						IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
						bean.remove(to);
						LOGGER.warn( "ActionDenied removed, action {}", action );
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
			List<ApplicationOption> deniedList = getOptions( this.deniedActions );
			this.options = new ArrayList<ApplicationOption>( getOptions(true) );
			this.selected = new LinkedList<ApplicationOption>();
			for( ApplicationOption option : this.options ) {
				if ( deniedList.contains(option) ) {
					this.selected.add(option);
				}
			}
			this.options.removeAll(deniedList);
		}
	}
	
	private String getAction( UICommand command ) {
		String action = null;
		MethodExpression expression = command.getActionExpression();
		if ( expression != null ) {
			action = expression.getExpressionString();
			if ( MenuParser.isReference(action) ) {
				action = command.getId();
			}
		}		
		return action;
	}

	public void renderedCommand( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			String action = getAction( (UICommand) component );
			if ( this.deniedActionsMap.containsKey(action) ) {
				parent.setRendered(false);
				component.setRendered(false);
			}				
		}
	}
	
	public void renderedGroup( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			String id = component.getId();
			OptionGroup group = getOptionController().getGroupMap().get(id);
			if ( group != null ) {
				if ( group.isRendered() ) {
					for( ApplicationOption option : group.getOptions() ) {
						boolean denied = this.deniedActionsMap.containsKey(option.getAction()); 
						if ( (!denied) && option.isRendered() ) {
							return;
						}
					}
				}
				component.setRendered(false);
			}
		}
	}

	public void renderedModule( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			String id = component.getId();
			if ( this.deniedModulesMap.containsKey(id) ) {
				component.setRendered(false);
			}				
		}
	}
	
	public IControllerListener getListener() {
		return listener;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Module> getProfileDeniedModules( Integer profile ) {
		Query query = AdminUtil.getQuery("SELECT pmd.module FROM ProfileModuleDenied pmd WHERE pmd.profile = ?");
		query.setInteger(0, profile );
		return query.list();
	}
	
	private Map<String,Module> getDeniedModules() {
		Map<String,Module> deniedModules = new HashMap<String, Module>();
		try {			
			Integer applicationUser = AdminUtil.getApplicationUser(DomainManager.getCurrentDomain());
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				for( Integer profile : profiles ) {
					List<Module> list = getProfileDeniedModules(profile);
					if ( list != null ) {
						for( Module module : list ) {
							deniedModules.put(module.getName(), module);	
						}
					}
				}
			}			
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting profile denied modules", th );
		}		
		return deniedModules;
	}
	
	private void initDeniedModules() {
		this.deniedModulesMap = new HashMap<String, ApplicationCategory>();
		if ( AonUtil.isBeanValue(ACTION_DENIED_CONTROLLER_NAME, MODULES_ENABLED) ) {
			Map<String,Module> enabledModules = getEnabledModules();
			Map<String,Module> deniedModules = getDeniedModules();
			for( ApplicationCategory category : getOptionController().getCategories() ) {
				if (! ArrayUtils.contains(SKIP_CATEGORIES, category.getAlias()) ) {
					boolean denied = true;
					if (! deniedModules.containsKey(category.getAlias()) ) {
						denied = ! enabledModules.containsKey(category.getAlias());
					}
					if ( denied ) {
						this.deniedModulesMap.put(category.getAlias(), category);	
					}					
				}
			}
		}
	}
	
	public List<ApplicationCategory> getCategories() {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if (! this.deniedModulesMap.containsValue(category) ) {
				list.add(category);
			}
		}
		return list;
	}
	
	public List<ApplicationOption> getOptions( boolean allOptions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		for( ApplicationCategory category : getCategories() ) {
			if ( allOptions || category.isRendered() ) {
				for( OptionGroup group : category.getGroups() ) {
					if ( allOptions || group.isRendered() ) {
						for( ApplicationOption option : group.getOptions() ) {
							if ( allOptions || option.isRendered() ) {
								list.add(option);	
							}
						}
					}
				}
			}				
		}
		return list;
	}		
	
}