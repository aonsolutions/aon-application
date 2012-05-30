package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_APPLICATION_CONTROLLER_NAME;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

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
import com.code.aon.config.Application;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.SelectTransferObject;
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
	
	private void initRoles() throws ManagerBeanException {
		this.roles = new LinkedList<SelectTransferObject<ApplicationRole,ProfileRole>>();
		Profile profile = (Profile) getTo();
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		Application application = dac.getDomainApplication().getApplication();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationRole.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.APPLICATION_ROLE_APPLICATION_ID), application.getId());
		criteria.addOrder("ApplicationRole.role.name");
		for( ITransferObject to : bean.getList(criteria) ) {
			ApplicationRole appRole = (ApplicationRole) to;
			SelectTransferObject<ApplicationRole,ProfileRole> item = new SelectTransferObject<ApplicationRole,ProfileRole>(appRole);
			item.setTo( getProfileRole(profile, appRole) );
			this.roles.add(item);
		}
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
		for( Module module : Module.values() ) {
			SelectTransferObject<Module,ProfileModuleDenied> item = new SelectTransferObject<Module, ProfileModuleDenied>(module);
			item.setTo( getProfileModuleDenied(profile, module) );
			this.deniedModules.add(item);
		}
		final Locale locale = AonUtil.getCurrentLocale();
    	Comparator<SelectTransferObject<Module,ProfileModuleDenied>> comparator = new Comparator<SelectTransferObject<Module,ProfileModuleDenied>>() {
			@Override
			public int compare(SelectTransferObject<Module,ProfileModuleDenied> o1, SelectTransferObject<Module,ProfileModuleDenied> o2) {
				return o1.getValue().getName(locale).compareTo(o2.getValue().getName(locale));
			}	    		
		};
    	Collections.sort( this.deniedModules, comparator );				
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
	
	public String getModuleDeniedList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			Profile profile = (Profile) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(ProfileModuleDenied.class);
			Criteria criteria = new Criteria();
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
		}
		return null;
	}	
	
}
