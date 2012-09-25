package com.code.aon.ui.audit.controller;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.el.MethodExpression;
import javax.faces.component.UICommand;
import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.audit.Action;
import com.code.aon.audit.ActionDenied;
import com.code.aon.audit.DomainApplicationModule;
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
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.ApplicationCategory;
import com.code.aon.ui.audit.ApplicationOption;
import com.code.aon.ui.audit.OptionGroup;
import com.code.aon.ui.audit.event.UserLoookupListener;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.common.role.IAonRole;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

/**
 * The Class FavoriteOptionController.
 */
public class ActionDeniedController implements IAuditConstants {

	private final static Logger LOGGER = LoggerFactory.getLogger(ActionDeniedController.class);
	
	private final static String ENTERPRISE_CATEGORY = "enterprise";
	private final static String CONFIGURATION_CATEGORY = "configuration";
	
	private final static String[] SKIP_CATEGORIES = new String[]{ENTERPRISE_CATEGORY,CONFIGURATION_CATEGORY};
	
	private Map<String,ApplicationOption> deniedActionsMap;
	
	private Map<String,ApplicationCategory> deniedModulesMap;
	
	private User user;
	
	private  Map<String,ActionDenied> deniedActions;
	
	private List<ApplicationOption> options;
	
	private List<ApplicationOption> selected;
	
	private IControllerListener listener;
	
	private FakeMap skipManagedBean;
	
	private Set<String> enabledManagedBeans;
	
	public ActionDeniedController() {
		User user = UserUtils.getInstance().getLoggedUser();
		initDeniedModules(user);
		this.deniedActionsMap = new HashMap<String, ApplicationOption>();
		for( ApplicationOption option : getOptions(getDeniedActions(user)) ) {
			this.deniedActionsMap.put(option.getAction(), option);
		}
		if ( AonUtil.isSkipLdap() ) {
			this.listener = new UserLoookupListener();	
		}
		initEnabledManagedBeans();
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
	
	public boolean isDeniedModule( String name ) {
		return this.deniedModulesMap.containsKey(name);
	}
	
	public void accept( ActionEvent event ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Map<String,ApplicationOption> map = new HashMap<String, ApplicationOption>();
			for( ApplicationOption option : this.selected ) {
				map.put( option.getAction(), option );
			}
			for( ActionDenied actionDenied : this.deniedActions.values() ) {
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
	
	private Map<String,ActionDenied> getUserDeniedActions( User user ) {
		Map<String,ActionDenied> map = new HashMap<String, ActionDenied>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_USER_ID), user.getId());
			Application application = getAuditController().getApplication();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_ACTION_APPLICATION_ID), application.getId());			
			for( ITransferObject to :  bean.getList(criteria) ) {
				ActionDenied ad = (ActionDenied) to;
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
	
	private Map<String,ITransferObject> getDeniedActions( User user ) {
		Map<String,ITransferObject> map = new HashMap<String, ITransferObject>();
		map.putAll(getUserDeniedActions(user));
		map.putAll(getProfileDeniedActions(user));
		return map;
	}
	
	private Map<String,Module> getEnabledModules( User user ) {
		Map<String,Module> enabledModules = new HashMap<String, Module>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			Integer appId = getAuditController().getApplication().getId();
			Integer domainId = DomainManager.getCurrentDomain();
			Integer domainApplication = AdminUtil.getDomainApplication(domainId, appId);
			String alias = bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID);
			Expression expression = ExpressionUtilities.getEqualExpression(alias, domainApplication);
	    	Integer parentDomainId = AdminUtil.getParentDomain(domainId);
	    	if ( parentDomainId != null ) {
	    		domainApplication = AdminUtil.getDomainApplication(parentDomainId, appId);
	    		if ( domainApplication != null ) {
		    		Expression expr2 = ExpressionUtilities.getEqualExpression(alias, domainApplication);
		    		expression = ExpressionUtilities.getOrExpression(expression, expr2);	    			
	    		}
	    	}			
			criteria.addExpression(expression);
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
	
	private List<ApplicationOption> getOptions( Map<String,? extends ITransferObject> deniedActions ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( Map.Entry<String,? extends ITransferObject> entry : deniedActions.entrySet() ) {
				String action = entry.getKey();
				ApplicationOption option = options.get(action);
				if ( (option != null) && (!isDeniedOption(option)) ) {
					list.add(option);
				} else {
					try {
						IManagerBean bean = BeanManager.getManagerBean(entry.getValue().getClass());
						bean.remove(entry.getValue());
						LOGGER.warn( "ActionDenied removed, action {}", action );
					} catch (ManagerBeanException e) {
						LOGGER.error( "Error removing action denied " + entry.getValue(), e);
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
			init( (User) event.getNewValue() );
		}
	}
	
	public void init( User user ) {
		setUser(user);
		this.deniedActions = getUserDeniedActions( getUser() );
		List<ApplicationOption> profileDeniedOptions = getOptions( getProfileDeniedActions(user) );
		this.selected = getOptions( this.deniedActions );
		this.selected.removeAll(profileDeniedOptions);
		this.options = new ArrayList<ApplicationOption>( getOptions(true) );
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
			if ( isDeniedModule(id) ) {
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
	
	private List<Integer> getProfiles( User user ) {
		AuthPrincipal principal = AonUtil.getAuthPrincipal();
		Integer applicationUser = AdminUtil.getApplicationUser(DomainManager.getCurrentDomain(), user.getId(), principal.getApplicationId());
		if (applicationUser == null ) {
			applicationUser = AdminUtil.getApplicationUser(principal, DomainManager.getCurrentDomain());
		}
		List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
		if ( (profiles != null) && (!profiles.isEmpty()) ) {
			return profiles;
		}
		return null;
	}
	
	private Map<String,Module> getDeniedModules( User user ) {
		Map<String,Module> deniedModules = new HashMap<String, Module>();
		try {			
			List<Integer> profiles = getProfiles(user);
			if ( profiles != null ) {
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
	
	private void initDeniedModules( User user ) {
		this.deniedModulesMap = new HashMap<String, ApplicationCategory>();
		if ( AonUtil.isBeanValue(ACTION_DENIED_CONTROLLER_NAME, MODULES_ENABLED) ) {
			Map<String,Module> enabledModules = getEnabledModules(user);
			Map<String,Module> deniedModules = getDeniedModules(user);
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
			if (! isDeniedModule(Module.DOCUMENT.getName()) ) {
				AonUtil.getRoleManager().setUserInRole(IAonRole.DOCUMENT, true);
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
	
	public void initEnabledManagedBeans() {
		this.skipManagedBean = new FakeMap();
		this.enabledManagedBeans = new HashSet<String>();
		List<ApplicationOption> options = new ArrayList<ApplicationOption>( getOptions(false) );
		options.removeAll(this.deniedActionsMap.values());		
		for( ApplicationOption option : options ) {
			String managedBean = StringUtils.substringBefore(option.getAction(), "-");
			this.enabledManagedBeans.add(StringUtils.substringBefore(managedBean, "_"));
		}
	}

	public FakeMap getSkip() {
		return skipManagedBean;
	}
	
	public class FakeMap extends AbstractMap<String,Boolean> {
		
		@Override
		public Boolean get(Object key) {
			return ! enabledManagedBeans.contains(key);
		}

		@Override
		public Set<Entry<String, Boolean>> entrySet() {
			return null;
		}
		
	}
	
}