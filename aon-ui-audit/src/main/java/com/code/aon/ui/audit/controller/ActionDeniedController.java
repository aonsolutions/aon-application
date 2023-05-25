package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.AUDIT_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.MENU;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jakarta.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.IAction;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.User;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.IVisibilityManager;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.audit.VisibilityManager;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;

/**
 * The Class FavoriteOptionController.
 */
public class ActionDeniedController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionDeniedController.class);
	
	private IVisibilityManager manager;
	
	private User user;
	
	private Map<String,IAction> deniedActions;
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> selected;
	
	private List<SelectItem> actionList;
	
	private SkipManagedBeanMap skipManagedBean;
	
	private ModuleEnabledMap moduleEnabled;
	
	private Map<String,ApplicationOption> enabledManagedBeans;
	
	public ActionDeniedController() {
		this.moduleEnabled = new ModuleEnabledMap(this);
		this.skipManagedBean = new SkipManagedBeanMap(this);
		init();
	}
	
	public void init() {
		this.manager = new VisibilityManager();
		initEnabledManagedBeans();
	}
	
	public void initCurrentUser() {
		boolean sysAdmin = AonUtil.getRoleManager().isSysAdmin();
		AonUtil.getRoleManager().init();
		if ( sysAdmin ) {
			AonUtil.getRoleManager().setSysAdmin();	
		}
		init();
	}
	
	private AuditController getAuditController() {
		return (AuditController) AonUtil.getRegisteredBean(AUDIT_CONTROLLER_NAME);
	}
	
	private ApplicationOptionController getOptionController() {
		return ApplicationOptionController.getInstance();
	}
	
	public void onInit( ActionEvent event ) {
		reset();
	}
	
	public IVisibilityManager getManager() {
		return manager;
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
	
	public boolean isDeniedModule( String name ) {
		return this.manager.isDeniedModule(name);
	}
	
	public void accept( ActionEvent event ) {
		boolean changed = false;
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Map<String,ApplicationOption> map = new HashMap<String, ApplicationOption>();
			for( ApplicationOption option : this.selected ) {
				map.put( option.getAction(), option );
			}
			for( IAction actionDenied : this.deniedActions.values() ) {
				String action = actionDenied.getAction().getName();
				if ( map.containsKey(action) ) {
					map.remove(action);
				} else {
					bean.remove( (ITransferObject) actionDenied );
					changed = true;
				}
			}
			for( ApplicationOption option : map.values() ) {
				ActionDenied actionDenied = new ActionDenied();
				Action action = getAuditController().getAction(option.getAction());
				actionDenied.setAction(action);
				actionDenied.setUser(user);
				bean.insert(actionDenied);
				changed = true;
			}
		} catch (ManagerBeanException e) {
			LOGGER.error("Error updating favorite action list", e);
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}		
		if ( changed ) {
			User currentUser = UserUtils.getInstance().getLoggedUser();
			if ( ObjectUtils.equals(user, currentUser) ) {
				initCurrentUser();
			}
		}
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
			initEdit( (User) event.getNewValue() );
		}
	}

	private List<ApplicationOption> sort( List<ApplicationOption> list ) {
		List<ApplicationOption> sorted = new ArrayList<ApplicationOption>();
		Map<ApplicationCategory,List<ApplicationOption>> map = new HashMap<ApplicationCategory, List<ApplicationOption>>();
		for( ApplicationOption option : list ) {
			ApplicationCategory category = option.getGroup().getCategory();
			List<ApplicationOption> options = map.get(category);
			if ( options == null ) {
				options = new ArrayList<ApplicationOption>();
				map.put(category, options);
			}
			options.add(option);
		}
		for( ApplicationCategory category : getOptionController().getCategories(true) ) {
			List<ApplicationOption> options = map.get(category);
			if ( (options != null) && !options.isEmpty() ) {
				Collections.sort(options);
				sorted.addAll(options);
			}
		}
		return sorted;
	}
	
	public Map<String, ApplicationCategory> initEdit( User user ) {
		setUser(user);
		this.deniedActions = getManager().getUserDeniedActions(user);
		Map<String, ApplicationCategory> deniedModules = getManager().getDeniedModules(user, true);
		Map<String,ProfileActionDenied> profileDeniedActions = getManager().getProfileDeniedActions(user);
		List<ApplicationOption> profileDeniedOptions = getManager().getOptions( profileDeniedActions, deniedModules );
		this.selected = getManager().getOptions( this.deniedActions, deniedModules );
		this.selected.removeAll(profileDeniedOptions);
		this.selected = sort(this.selected);
		List<ApplicationCategory> categories = getCategories(deniedModules);
		this.options = new ArrayList<ApplicationOption>( getOptions(categories, true) );
		this.options.removeAll(profileDeniedOptions);
		this.options.removeAll(this.selected);
		return deniedModules;
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
			if ( getManager().isDenied(action) ) {
				parent.setRendered(false);
				component.setRendered(false);
			}				
		}
	}

	public void renderedMenuItem( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() && (UICommand.class.isAssignableFrom(component.getClass())) ) {
			String action = getAction( (UICommand) component );
			if ( getManager().isDenied(action) ) {
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
						boolean denied = getManager().isDenied(option.getAction()); 
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
			String id = StringUtils.removeStart(component.getId(), MenuParser.MENU_ACTION_PREFFIX);
			if ( isDeniedModule(id) ) {
				component.setRendered(false);
			}				
		}
	}
	
	public List<ApplicationCategory> getCategories() {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if (! getManager().isDeniedModule(category.getAlias()) ) {
				list.add(category);
			}
		}
		return list;
	}

	public List<ApplicationCategory> getCategories( Map<String,ApplicationCategory> deniedModules ) {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories(true) ) {
			if (! deniedModules.containsValue(category) ) {
				list.add(category);
			}
		}
		return list;
	}

	public List<ApplicationCategory> getCategories( Set<Module> enabledModules ) {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories(true) ) {
			Module module = Module.get(category.getAlias());
			if ( enabledModules.contains(module) ) {
				list.add(category);
			}
		}
		return list;
	}
	
	public List<ApplicationOption> getOptions( boolean allOptions ) {
		return getOptions(getCategories(), allOptions);
	}

	public List<ApplicationOption> getOptions( List<ApplicationCategory> categories, boolean allOptions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		for( ApplicationCategory category : categories ) {
			List<ApplicationOption> categoryList = new ArrayList<ApplicationOption>();
			if ( allOptions || category.isRendered() ) {
				for( OptionGroup group : category.getGroups() ) {
					if ( allOptions || group.isRendered() ) {
						for( ApplicationOption option : group.getOptions() ) {
							if ( allOptions || option.isRendered() ) {
								categoryList.add(option);	
							}
						}
					}
				}
			}	
			Collections.sort(categoryList);
			list.addAll(categoryList);
		}
		return list;
	}
	
	private String getManagedBean( ApplicationOption option ) {
		String managedBean = StringUtils.substringBefore(option.getAction(), "-");
		return StringUtils.substringBeforeLast(managedBean, "_");
	}
	
	private boolean isMainOption( ApplicationOption option ) {
		return ! StringUtils.contains(option.getAction(), "-");
	}
	
	public void initEnabledManagedBeans() {
		this.enabledManagedBeans = new HashMap<String, ApplicationOption>();
		List<ApplicationOption> options = new ArrayList<ApplicationOption>( getOptions(true) );	
		for( ApplicationOption option : options ) {
			String managedBean = getManagedBean(option);
			if ( isMainOption(option) || !this.enabledManagedBeans.containsKey(managedBean) ) {
				this.enabledManagedBeans.put(managedBean, option);				
			}
		}
		for( ApplicationOption option : getManager().getDeniedOptions() ) {
			String managedBean = getManagedBean(option);
			this.enabledManagedBeans.remove(managedBean);
		}
	}

	public SkipManagedBeanMap getSkip() {
		return skipManagedBean;
	}

	public ModuleEnabledMap getModuleEnabled() {
		return moduleEnabled;
	}
	
	public void updateActionList( Map<String, ApplicationCategory> deniedModules ) {
		actionList = new LinkedList<SelectItem>();
		for( ApplicationOption option : getOptions() ) {
			String name = StringUtils.abbreviate(option.getDescription(), 60) + " (" + option.getGroup().getCategory().getDescription() + ")";
			SelectItem item = new SelectItem(option.getAction(), name);
			actionList.add(item);
		}
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		for( ApplicationCategory category : adc.getCategories(deniedModules) ) {
			if ( category.isRendered() ) {
				String name = category.getDescription() + " (" + AonUtil.getMessage(MENU) + ")";
				SelectItem item = new SelectItem(category.getAction(), name);
				actionList.add(item);				
			}
		}
		AonUtil.sortSelectItems(actionList);
	}
	
	public List<SelectItem> getActionList() {
        return actionList;
	}	
	
	private Map<String, ApplicationOption> getEnabledManagedBeans() {
		return enabledManagedBeans;
	}

	public static class SkipManagedBeanMap extends AbstractMap<String,Boolean> implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ActionDeniedController controller;
		
		public SkipManagedBeanMap(ActionDeniedController controller) {
			this.controller = controller;
		}
		
		@Override
		public Boolean get(Object key) {
			boolean skip = true;
			ApplicationOption option = controller.getEnabledManagedBeans().get(key);
			if ( option != null ) {
				skip = ! option.isRendered();
			}
			return skip;
		}

		@Override
		public Set<Entry<String, Boolean>> entrySet() {
			return null;
		}
		
	}

	public static class ModuleEnabledMap extends AbstractMap<String,Boolean> implements Serializable {

		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private ActionDeniedController controller;
		
		public ModuleEnabledMap(ActionDeniedController controller) {
			this.controller = controller;
		}

		@Override
		public Boolean get(Object key) {
			return ! controller.isDeniedModule(key.toString());
		}

		@Override
		public Set<Entry<String, Boolean>> entrySet() {
			return null;
		}
		
	}

}