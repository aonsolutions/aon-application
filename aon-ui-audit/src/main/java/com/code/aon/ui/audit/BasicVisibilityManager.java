package com.code.aon.ui.audit;

import static com.code.aon.ui.audit.controller.IAuditConstants.ACTION_DENIED_CONTROLLER_NAME;
import static com.code.aon.ui.audit.controller.IAuditConstants.ENTERPRISE_CATEGORY;
import static com.code.aon.ui.audit.controller.IAuditConstants.MODULES_ENABLED;
import static com.code.aon.ui.audit.controller.IAuditConstants.PROFILE_DENIED_ACTIONS_ENABLED;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.hibernate.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
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
import com.code.aon.config.User;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.audit.controller.ApplicationOptionController;
import com.code.aon.ui.common.role.IAonRole;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public abstract class BasicVisibilityManager implements Serializable, IVisibilityManager {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final static String[] SKIP_CATEGORIES = new String[]{ENTERPRISE_CATEGORY};

	private final static Logger LOGGER = LoggerFactory.getLogger(BasicVisibilityManager.class);
	
	private Map<String,ApplicationCategory> deniedModulesMap;
	
	private Map<String,ApplicationOption> deniedActionsMap;
	
	public BasicVisibilityManager() {
		User user = UserUtils.getInstance().getLoggedUser();
		initDeniedModules(user);
		this.deniedActionsMap = new HashMap<String, ApplicationOption>();
		for( ApplicationOption option : getOptions(getDeniedActions(user), getDeniedModulesMap()) ) {
			this.deniedActionsMap.put(option.getAction(), option);
		}				
	}

	protected Map<String, ApplicationOption> getDeniedActionsMap() {
		return deniedActionsMap;
	}

	protected Map<String, ApplicationCategory> getDeniedModulesMap() {
		return deniedModulesMap;
	}

	private Map<String,IAction> getDeniedActions( User user ) {
		Map<String,IAction> map = new HashMap<String, IAction>();
		map.putAll(getUserDeniedActions(user));
		map.putAll(getProfileDeniedActions(user));
		return map;
	}
	
	private void initDeniedModules( User user ) {
		this.deniedModulesMap = getDeniedModules(user, false);
		if (! isDeniedModule(Module.DOCUMENT.getName()) ) {
			AonUtil.getRoleManager().setUserInRole(IAonRole.DOCUMENT, true);
		}
	}
	
	@Override
	public boolean isDeniedModule( String name ) {
		return getDeniedModulesMap().containsKey(name);
	}		
	
	@Override
	public boolean isDenied( String action ) {
		return getDeniedActionsMap().containsKey(action);
	}

	@Override
	public boolean isDenied( ApplicationOption option ) {
		if ( isDeniedOption(getDeniedModulesMap(), option) ) {
			return true;
		}
		return isDenied(option.getAction());
	}
	
	@Override
	public Collection<ApplicationOption> getDeniedOptions() {
		return getDeniedActionsMap().values();
	}	
	
	@Override
	public List<Module> getVisibleModules() {
		List<Module> list = new ArrayList<Module>();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if (category.isRendered() && !getDeniedModulesMap().containsValue(category) ) {
				Module module = Module.get(category.getAlias());
				if ( module != null ) {
					list.add(module);	
				}
			}
		}
		return list;		
	}		
	
	@Override
	public Map<String,IAction> getUserDeniedActions( User user ) {
		Map<String, IAction> map = new HashMap<String, IAction>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(ActionDenied.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(true);
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_USER_ID), user.getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACTION_DENIED_ACTION_APPLICATION_ID), getApplicationId());			
			for( ITransferObject to :  bean.getList(criteria) ) {
				IAction ad = (IAction) to;
				map.put(ad.getAction().getName(), ad);
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading actions denied", e);
		}
		return map;		
	}

	@Override
	public List<ApplicationOption> getOptions( Map<String,? extends IAction> deniedActions, Map<String,ApplicationCategory> deniedModules ) {
		List<ApplicationOption> list = new ArrayList<ApplicationOption>();
		if (! deniedActions.isEmpty() ) {
			Map<String,ApplicationOption> options = getOptionController().getOptionMap();
			for( Map.Entry<String,? extends IAction> entry : deniedActions.entrySet() ) {
				String action = entry.getKey();
				ApplicationOption option = options.get(action);
				if ( option != null ) {
					if ( !isDeniedOption(deniedModules, option) ) {
						list.add(option);	
					}	
				} else {
					AuditManager.removeAction(entry.getValue().getAction());
				}
			}
		}
		return list;		
	}	
	
	@Override
	public Map<String,ProfileActionDenied> getProfileDeniedActions( User user ) {
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
	
	
	@SuppressWarnings("unchecked")
	public static List<Module> getProfileDeniedModules( Integer profile ) {
		Query query = AdminUtil.getQuery("SELECT pmd.module FROM ProfileModuleDenied pmd WHERE pmd.profile = ?");
		query.setInteger(0, profile );
		List<Module> modules = query.list();
		AdminUtil.closeSession();
		return modules;
	}	
	
	protected Set<Module> getProfileDeniedModules( User user ) {
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
	
	protected boolean contains( Set<Module> modules, String name ) {
		for( Module module : modules ) {
			if ( module.getName().equals(name) ) {
				return true;
			}
		}
		return false;
	}	
	
	protected Integer getApplicationId() {
		return AonUtil.getAuthPrincipal().getApplicationId();
	}
	
	protected ApplicationOptionController getOptionController() {
		return ApplicationOptionController.getInstance();
	}	
		
	protected boolean isDeniedOption( Map<String,ApplicationCategory> deniedModules, ApplicationOption option ) {
		return deniedModules.containsValue(option.getGroup().getCategory());
	}	

	protected List<Integer> getProfiles( User user ) {
		Integer applicationUser = AdminUtil.getApplicationUser(user.getDomain(), user.getId(), getApplicationId());
		if ( applicationUser != null ) {
			List<Integer> profiles = AdminUtil.getProfiles(applicationUser);
			if ( (profiles != null) && (!profiles.isEmpty()) ) {
				return profiles;
			}
		}
		return null;
	}	
	
	protected Set<Module> getEnabledModuleList( boolean fromDomain, boolean fromParent ) {
		Set<Module> enabledModules = new HashSet<Module>();
		try {
			IManagerBean bean = BeanManager.getManagerBean(DomainApplicationModule.class);
			Criteria criteria = new Criteria();
			Integer appId = getApplicationId(); 
			Integer domainId = DomainManager.getCurrentDomain();
			String alias = bean.getFieldName(IEntityAlias.DOMAIN_APPLICATION_MODULE_DOMAIN_APPLICATION_ID);
			Expression expression = null;
			if ( fromDomain ) {
				Integer domainApplication = AdminUtil.getDomainApplication(domainId, appId);
				expression = ExpressionUtilities.getEqualExpression(alias, domainApplication);				
			}
			if ( fromParent ) {
		    	Integer parentDomainId = AdminUtil.getParentDomain(domainId);
		    	if ( parentDomainId != null ) {
		    		Integer domainApplication = AdminUtil.getDomainApplication(parentDomainId, appId);
		    		if ( domainApplication != null ) {
			    		Expression expr2 = ExpressionUtilities.getEqualExpression(alias, domainApplication);
			    		expression = ExpressionUtilities.getOrExpression(expression, expr2);
						criteria.setSkipDomainFilter(true);			    		
		    		}
		    	}							
			}
			if ( expression != null ) {
				criteria.addExpression(expression);	
			}
			for( ITransferObject to : bean.getList(criteria) ) {
				DomainApplicationModule dam = (DomainApplicationModule) to;
				enabledModules.add( dam.getModule() );	
			}
		} catch (ManagerBeanException e) {
			LOGGER.error( "Error loading modules denied", e);
		}
		return enabledModules;		
	}
	
	@Override
	public Map<String, ApplicationCategory> getDeniedModules( User user, boolean addExtraModules ) {
		Map<String, ApplicationCategory> map = new HashMap<String, ApplicationCategory>();
		if ( AonUtil.isBeanValue(ACTION_DENIED_CONTROLLER_NAME, MODULES_ENABLED) ) {
			Set<Module> enabledModules = getEnabledModules(user, addExtraModules);
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
	
	@Override
	public void enableOnly( String[] categories, String[] groups, String ... disableOptionIds ) {
		getDeniedActionsMap().clear();
		getDeniedModulesMap().clear();
		for( ApplicationCategory category : getOptionController().getCategories() ) {
			if ( ArrayUtils.contains(categories, category.getAlias()) ) {
				if (! ArrayUtils.isEmpty(groups) ) {
					for( OptionGroup group : category.getGroups() ) {
						if (! ArrayUtils.contains(groups, group.getId()) ) {
							for( ApplicationOption option : group.getOptions() ) {
								getDeniedActionsMap().put(option.getAction(), option);
							}
						}
					}													
				}
			} else {
				getDeniedModulesMap().put(category.getAlias(), category);
			}
		}
		for( String optionId : disableOptionIds ) {
			ApplicationOption option = getOptionController().getOptionMap().get(optionId);
			if ( option != null ) {
				getDeniedActionsMap().put(option.getAction(), option);
			}
		}
	}	
	
}
