package com.code.aon.ui.audit.controller;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.AUDIT_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ENTERPRISE_CATEGORY;
import static com.code.aon.ui.audit.controller.IAuditConstants.MODULES_ENABLED;
import static com.code.aon.ui.audit.controller.IAuditConstants.PROFILE_DENIED_ACTIONS_ENABLED;
import static com.code.aon.ui.common.ICommonMessages.MENU;

import java.io.Serializable;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.DomainApplicationModule;
import com.code.aon.audit.IAction;
import com.code.aon.audit.ProfileActionDenied;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.config.User;
import com.code.aon.config.enumeration.DomainType;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.AuditManager;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.role.IAonRole;
import com.code.aon.ui.config.controller.DomainSwitcher;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * The Class FavoriteOptionController.
 */
public class ActionDeniedController implements Serializable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ActionDeniedController.class);
	
	private final static String[] SKIP_CATEGORIES = new String[]{ENTERPRISE_CATEGORY};
	
	private Map<String,ApplicationOption> deniedActionsMap;
	
	private Map<String,ApplicationCategory> deniedModulesMap;
	
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
		User user = UserUtils.getInstance().getLoggedUser();		
		initDeniedModules(user);
		this.deniedActionsMap = new HashMap<String, ApplicationOption>();
		for( ApplicationOption option : getOptions(getDeniedActions(user), this.deniedModulesMap) ) {
			this.deniedActionsMap.put(option.getAction(), option);
		}		
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
		return this.deniedModulesMap.containsKey(name);
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
	
	private Map<String,IAction> getUserDeniedActions( User user ) {
		Map<String, IAction> map = new HashMap<String, IAction>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_USER_ID), user.getId());
			Application application = getAuditController().getApplication();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_ACTION_APPLICATION_ID), application.getId());			
			for( ITransferObject to :  bean.getList(criteria) ) {
				IAction ad = (IAction) to;
				map.put(ad.getAction().getName(), ad);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading actions denied", e);
		}
		return map;		
	}

	private Map<String,ProfileActionDenied> getProfileDeniedActions( User user ) {
		Map<String,ProfileActionDenied> map = new HashMap<String, ProfileActionDenied>();
		if ( AonUtil.isBeanValue(ACTION_DENIED_CONTROLLER_NAME, PROFILE_DENIED_ACTIONS_ENABLED) ) {
			List<Integer> profiles = getProfiles(user);
			if ( profiles != null ) {
				try {			
					IManagerBean bean = BeanManager.getManagerBean(ProfileActionDenied.class);
					Criteria criteria = new Criteria();
					criteria.setSkipDomainFilter(true);
					criteria.addInExpression(bean.getFieldName(IEntityAlias.PROFILE_ACTION_DENIED_PROFILE_ID), profiles);
					for( ITransferObject to :  bean.getList(criteria) ) {
						ProfileActionDenied pad = (ProfileActionDenied) to;
						map.put(pad.getAction().getName(), pad);
					}
				} catch (ManagerBeanException e) {
					LOGGER.error( "Error loading profile actions denied", e);
				}
			}
		}
		return map;		
	}
	
	private Map<String,IAction> getDeniedActions( User user ) {
		Map<String,IAction> map = new HashMap<String, IAction>();
		map.putAll(getUserDeniedActions(user));
		map.putAll(getProfileDeniedActions(user));
		return map;
	}
	
	private Set<Module> getEnabledModuleList( boolean skipParentModules ) {
		Set<Module> enabledModules = new HashSet<Module>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			Integer appId = getAuditController().getApplication().getId();
			Integer domainId = DomainManager.getCurrentDomain();
			Integer domainApplication = AdminUtil.getDomainApplication(domainId, appId);
			String alias = bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID);
			Expression expression = ExpressionUtilities.getEqualExpression(alias, domainApplication);
			if (! skipParentModules) {
		    	Integer parentDomainId = AdminUtil.getParentDomain(domainId);
		    	if ( parentDomainId != null ) {
		    		domainApplication = AdminUtil.getDomainApplication(parentDomainId, appId);
		    		if ( domainApplication != null ) {
			    		Expression expr2 = ExpressionUtilities.getEqualExpression(alias, domainApplication);
			    		expression = ExpressionUtilities.getOrExpression(expression, expr2);
						criteria.setSkipDomainFilter(true);			    		
		    		}
		    	}							
			}
			criteria.addExpression(expression);
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				enabledModules.add( dam.getModule() );	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading modules denied", e);
		}
		return enabledModules;		
	}
	
	public Set<Module> getEnabledModules( User user, boolean addConsultancyWithFiscal ) {
		Set<Module> enabledModules = new HashSet<Module>();
		try {
			boolean skipParentModules = true;
			Integer domainId = DomainManager.getCurrentDomain();
			Integer applicationId = getAuditController().getApplication().getId();
			Integer parentDomainId = AdminUtil.getParentDomain(domainId);
			boolean consultancyParent = false;
			if ( parentDomainId != null ) {
				consultancyParent = (DomainSwitcher.getDomainType(parentDomainId) == DomainType.CONSULTANCY);
			}
			if ( user != null ) {
				if ( consultancyParent ) {
					boolean domainParentUser = ObjectUtils.equals(user.getDomain(), parentDomainId);
					if ( domainParentUser &&
						AuditManager.hasModule(parentDomainId, applicationId, Module.FISCAL) ) {
							enabledModules.add(Module.ACCOUNTING);
							enabledModules.add(Module.MANAGEMENT);
							enabledModules.add(Module.TREASURY);
					}
					skipParentModules = consultancyParent && (!domainParentUser);
				}
			}
			if ( addConsultancyWithFiscal ) {
				if ( (DomainSwitcher.getDomainType(domainId) == DomainType.CONSULTANCY) && 
						AuditManager.hasModule(domainId, applicationId, Module.FISCAL) ) {
					enabledModules.add(Module.ACCOUNTING);
					enabledModules.add(Module.MANAGEMENT);
					enabledModules.add(Module.TREASURY);
				}
			}
			enabledModules.addAll( getEnabledModuleList(skipParentModules) );
			if ( consultancyParent && enabledModules.contains(Module.DOCUMENT_PORTAL) ) {
				enabledModules.remove(Module.DOCUMENT_PORTAL);
				enabledModules.add(Module.DOCUMENT);
			}
			enabledModules.add(Module.CONFIGURATION);
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading enabled modules for " + user, e);
		}			
		return enabledModules;		
	}
	
	private boolean isDeniedOption( Map<String,ApplicationCategory> deniedModules, ApplicationOption option ) {
		return deniedModules.containsValue(option.getGroup().getCategory());
	}
	
	public boolean isDenied( ApplicationOption option ) {
		if ( isDeniedOption(this.deniedModulesMap, option) ) {
			return true;
		}
		return deniedActionsMap.containsKey(option.getAction());
	}
	
	public Collection<ApplicationOption> getDeniedOptions() {
		return this.deniedActionsMap.values();
	}
	
	private List<ApplicationOption> getOptions( Map<String,? extends IAction> deniedActions, Map<String,ApplicationCategory> deniedModules ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( Map.Entry<String,? extends IAction> entry : deniedActions.entrySet() ) {
				String action = entry.getKey();
				ApplicationOption option = options.get(action);
				if ( (option != null) && !isDeniedOption(deniedModules, option) ) {
					list.add(option);	
				} else {
					AuditManager.removeAction(entry.getValue().getAction().getId());
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
			initEdit( (User) event.getNewValue() );
		}
	}
	
	public void initEdit( User user ) {
		setUser(user);
		this.deniedActions = getUserDeniedActions(user);
		Map<String, ApplicationCategory> deniedModules = getDeniedModules(user, true);
		List<ApplicationOption> profileDeniedOptions = getOptions( getProfileDeniedActions(user), deniedModules );
		this.selected = getOptions( this.deniedActions, deniedModules );
		this.selected.removeAll(profileDeniedOptions);
		List<ApplicationCategory> categories = getCategories(deniedModules);
		this.options = new ArrayList<ApplicationOption>( getOptions(categories, true) );
		this.options.removeAll(profileDeniedOptions);
		this.options.removeAll(this.selected);		
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

	public void renderedMenuItem( UIComponent component, UIComponent parent ) {
		if ( component.isRendered() ) {
			String action = getAction( (UICommand) component );
			if ( this.deniedActionsMap.containsKey(action) ) {
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
			String id = StringUtils.removeStart(component.getId(), MenuParser.MENU_ACTION_PREFFIX);
			if ( isDeniedModule(id) ) {
				component.setRendered(false);
			}				
		}
	}
	
	@SuppressWarnings("unchecked")
	public static List<Module> getProfileDeniedModules( Integer profile ) {
		Query query = AdminUtil.getQuery("SELECT pmd.module FROM ProfileModuleDenied pmd WHERE pmd.profile = ?");
		query.setInteger(0, profile );
		List<Module> modules = query.list();
		AdminUtil.closeSession();
		return modules;
	}
	
	private List<Integer> getProfiles( User user ) {
		Integer appId = getAuditController().getApplication().getId();
		Integer applicationUser = AdminUtil.getApplicationUser(user.getDomain(), user.getId(), appId);
		if ( applicationUser != null ) {
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				return profiles;
			}
		}
		return null;
	}
	
	private Set<Module> getProfileDeniedModules( User user ) {
		Set<Module> deniedModules = new HashSet<Module>();
		try {			
			List<Integer> profiles = getProfiles(user);
			if ( profiles != null ) {
				for( Integer profile : profiles ) {
					List<Module> list = getProfileDeniedModules(profile);
					if ( list != null ) {
						deniedModules.addAll(list);
					}
				}
			}			
		} catch ( Throwable th ) {
			LOGGER.error( "Error getting profile denied modules", th );
		}		
		return deniedModules;
	}

	private boolean contains( Set<Module> modules, String name ) {
		for( Module module : modules ) {
			if ( module.getName().equals(name) ) {
				return true;
			}
		}
		return false;
	}
	
	public Map<String, ApplicationCategory> getDeniedModules( User user, boolean addConsultancyWithFiscal ) {
		Map<String, ApplicationCategory> map = new HashMap<String, ApplicationCategory>();
		if ( AonUtil.isBeanValue(ACTION_DENIED_CONTROLLER_NAME, MODULES_ENABLED) ) {
			Set<Module> enabledModules = getEnabledModules(user, addConsultancyWithFiscal);
			Set<Module> deniedModules = getProfileDeniedModules(user);
			for( ApplicationCategory category : getOptionController().getCategories() ) {
				if (! ArrayUtils.contains(SKIP_CATEGORIES, category.getAlias()) ) {
					boolean denied = true;
					if (! contains(deniedModules, category.getAlias()) ) {
						denied = ! contains(enabledModules, category.getAlias());
					}
					if ( denied ) {
						map.put(category.getAlias(), category);	
					}					
				}
			}
		}
		return map;
	}
	
	private void initDeniedModules( User user ) {
		this.deniedModulesMap = getDeniedModules(user, false);
		if (! isDeniedModule(Module.DOCUMENT.getName()) ) {
			AonUtil.getRoleManager().setUserInRole(IAonRole.DOCUMENT, true);
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

	public List<ApplicationCategory> getCategories( Map<String,ApplicationCategory> deniedModules ) {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if (! deniedModules.containsValue(category) ) {
				list.add(category);
			}
		}
		return list;
	}

	public List<ApplicationCategory> getCategories( Set<Module> enabledModules ) {
		List<ApplicationCategory> list = new ArrayList<ApplicationCategory>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
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
	
	private String getManagedBean( ApplicationOption option ) {
		String managedBean = StringUtils.substringBefore(option.getAction(), "-");
		return StringUtils.substringBefore(managedBean, "_");
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
		for( ApplicationOption option : this.deniedActionsMap.values() ) {
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
	
	public void enableOnly( String[] categories, String[] groups, String ... disableOptionIds ) {
		this.deniedActionsMap.clear();
		this.deniedModulesMap.clear();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if ( ArrayUtils.contains(categories, category.getAlias()) ) {
				if (! ArrayUtils.isEmpty(groups) ) {
					for( OptionGroup group : category.getGroups() ) {
						if (! ArrayUtils.contains(groups, group.getId()) ) {
							for( ApplicationOption option : group.getOptions() ) {
								this.deniedActionsMap.put(option.getAction(), option);
							}
						}
					}													
				}
			} else {
				this.deniedModulesMap.put(category.getAlias(), category);
			}
		}
		for( String optionId : disableOptionIds ) {
			ApplicationOption option = getOptionController().getOptionMap().get(optionId);
			if ( option != null ) {
				this.deniedActionsMap.put(option.getAction(), option);
			}
		}
	}
	
	public void updateActionList() {
		actionList = new LinkedList<SelectItem>();
		for( ApplicationOption option : getOptions() ) {
			String name = StringUtils.abbreviate(option.getDescription(), 60) + " (" + option.getGroup().getCategory().getDescription() + ")";
			SelectItem item = new SelectItem(option.getAction(), name);
			actionList.add(item);
		}
		ActionDeniedController adc = (ActionDeniedController) AonUtil.getRegisteredBean(ACTION_DENIED_CONTROLLER_NAME);
		for( ApplicationCategory category : adc.getCategories() ) {
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
	
	public List<Module> getVisibleModules() {
		List<Module> list = new ArrayList<Module>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if (category.isRendered() && !deniedModulesMap.containsValue(category) ) {
				Module module = Module.get(category.getAlias());
				if ( module != null ) {
					list.add(module);	
				}
			}
		}
		return list;		
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