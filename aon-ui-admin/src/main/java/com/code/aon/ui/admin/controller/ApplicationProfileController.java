package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_APPLICATION_CONTROLLER_NAME;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.admin.ApplicationRole;
import com.code.aon.admin.Profile;
import com.code.aon.admin.ProfileRole;
import com.code.aon.audit.ProfileModuleDenied;
import com.code.aon.audit.enumeration.Module;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.util.AdminUtil;
import com.code.aon.config.Application;
import com.code.aon.jaas.auth.AuthPrincipal;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.SelectTransferObject;
import com.code.aon.ui.common.role.IAonRole;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class ApplicationProfileController extends LinesController {
	
	private List<SelectTransferObject<ApplicationRole,ProfileRole>> roles;
	
	private List<SelectTransferObject<Module,ProfileModuleDenied>> deniedModules;
	
	public List<SelectTransferObject<ApplicationRole, ProfileRole>> getRoles() {
		return roles;
	}

	public List<SelectTransferObject<Module, ProfileModuleDenied>> getDeniedModules() {
		return deniedModules;
	}
	
	private ProfileRole getProfileRole( Profile profile, ApplicationRole appRole ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileRole.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_ROLE_PROFILE_ID), profile.getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_ROLE_APPLICATION_ROLE_ID), appRole.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (ProfileRole) list.get(0);
		}
		return null;
	}	
	
	private String getRoleLabel( ApplicationRole appRole) {
		String roleName = appRole.getRole().getName();
		IAonRole role = IAonRole.get(roleName);
		if ( role != null ) {
			return role.getDisplayName();
		}
		return roleName;
	}
	
	private void initRoles() throws ManagerBeanException {
		this.roles = new LinkedList<SelectTransferObject<ApplicationRole,ProfileRole>>();
		Profile profile = (Profile) getTo();
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		Application application = dac.getDomainApplication().getApplication();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationRole.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_ROLE_APPLICATION_ID), application.getId());
		for( ITransferObject to : bean.getList(criteria) ) {
			ApplicationRole appRole = (ApplicationRole) to;
			SelectTransferObject<ApplicationRole,ProfileRole> item = new SelectTransferObject<ApplicationRole,ProfileRole>(appRole);
			item.setTo( getProfileRole(profile, appRole) );
			item.setLabel( getRoleLabel(appRole) );
			this.roles.add(item);
		}
		Collections.sort( this.roles, SelectTransferObject.getComparator() );
	}

	private ProfileModuleDenied getProfileModuleDenied( Profile profile, Module module ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileModuleDenied.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_MODULE_DENIED_MODULE), module);
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROFILE_MODULE_DENIED_PROFILE_ID), profile.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			return (ProfileModuleDenied) list.get(0);
		}
		return null;
	}	
	
	private void initDeniedModules() throws ManagerBeanException {
		this.deniedModules = new LinkedList<SelectTransferObject<Module,ProfileModuleDenied>>();
		Profile profile = (Profile) getTo();
		Locale locale = AonUtil.getCurrentLocale();
		for( Module module : Module.values() ) {
			SelectTransferObject<Module,ProfileModuleDenied> item = new SelectTransferObject<Module, ProfileModuleDenied>(module);
			item.setTo( getProfileModuleDenied(profile, module) );
			item.setLabel( module.getName(locale) );
			this.deniedModules.add(item);
		}
		Collections.sort( this.deniedModules, SelectTransferObject.getComparator() );
	}
	
	public void initProfileInfos() throws ManagerBeanException {
		initRoles();
		initDeniedModules();
	}

	private void saveRoles() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileRole.class);
		Profile profile = (Profile) getTo();
		for( SelectTransferObject<ApplicationRole,ProfileRole> item : this.roles ) {
			if ( item.isChecked() ) {
				if ( item.getTo() == null ) {
					ProfileRole to = new ProfileRole();
					to.setProfile(profile);
					to.setApplicationRole(item.getValue());
					bean.insert(to);
					item.setTo(to);
				}
			} else {
				item.unregister();
			}			
		}		
	}

	private void saveDeniedModules() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileModuleDenied.class);
		Profile profile = (Profile) getTo();
		for( SelectTransferObject<Module,ProfileModuleDenied> item : this.deniedModules ) {
			if ( item.isChecked() ) {
				if ( item.getTo() == null ) {
					ProfileModuleDenied to = new ProfileModuleDenied();
					to.setProfile(profile);
					to.setModule(item.getValue());
					bean.insert(to);
					item.setTo(to);
				}
			} else {
				item.unregister();
			}			
		}		
	}
	
	public void onSaveProfile() throws ManagerBeanException {
		saveRoles();
		saveDeniedModules();
	}		
	
	public static String getRoleList( Profile profile ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileRole.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.PROFILE_ROLE_PROFILE_ID);
		criteria.addEqualExpression(alias, profile.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			Set<String> roles = new TreeSet<String>();
			for( ITransferObject to : list ) {
				String name = ((ProfileRole) to).getApplicationRole().getRole().getName();
				IAonRole _role = IAonRole.get(name);
				if ( _role != null ) {
					name = _role.getDisplayName();
				}
				roles.add( name );
			}
			return StringUtils.join(roles, ", ");
		}
		return null;
	}	

	public static String getModuleDeniedList( Profile profile ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProfileModuleDenied.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		String alias = bean.getFieldName(IEntityAlias.PROFILE_MODULE_DENIED_PROFILE_ID);
		criteria.addEqualExpression(alias, profile.getId());
		List<ITransferObject> list = bean.getList(criteria);
		if (! list.isEmpty() ) {
			Set<String> modules = new TreeSet<String>();
			Locale locale = AonUtil.getCurrentLocale();
			for( ITransferObject to : list ) {
				ProfileModuleDenied pmd = (ProfileModuleDenied) to;
				modules.add( pmd.getModule().getName(locale) );
			}
			return StringUtils.join(modules, ", ");
		}
		return null;
	}	
	
	public String getModuleDeniedList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			return getModuleDeniedList( (Profile) getSelectedTO() );
		}
		return null;
	}	

	public void onInit( ActionEvent event ) throws ManagerBeanException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		AuthPrincipal user = AonUtil.getAuthPrincipal();
		Integer domainApplication = AdminUtil.getDomainApplication(DomainManager.getCurrentDomain(), user.getApplicationId());
		dac.select(event, domainApplication);
	}
	
}