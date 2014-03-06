package com.code.aon.ui.admin.controller;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_APPLICATION_CONTROLLER_NAME;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationUser;
import com.code.aon.config.Domain;
import com.code.aon.config.DomainApplication;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.admin.SelectTransferObject;
import com.code.aon.ui.admin.UserApplicationInfo;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationUserController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private List<SelectTransferObject<Profile,ApplicationUserProfile>> userProfiles;
	
	private boolean showParentDomainUsers;
	
	public boolean isShowParentDomainUsers() {
		return showParentDomainUsers;
	}

	public void setShowParentDomainUsers(boolean showParentDomainUsers) {
		this.showParentDomainUsers = showParentDomainUsers;
	}

	public List<SelectTransferObject<Profile, ApplicationUserProfile>> getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(
			List<SelectTransferObject<Profile, ApplicationUserProfile>> userProfiles) {
		this.userProfiles = userProfiles;
	}

	public ApplicationUser getApplicationUser() {
		return (ApplicationUser) getTo();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static List<ApplicationUserProfile> getApplicationUserProfiles( ApplicationUser user, boolean skipDomain ) throws ManagerBeanException  {
		if ( user != null ) {
			IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
			Criteria criteria = new Criteria();
			criteria.setSkipDomainFilter(skipDomain);
			String alias = bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID);
			criteria.addEqualExpression(alias, user.getId());
			return (List) bean.getList(criteria);			
		}
		return Collections.emptyList();
	}
	
	private static ApplicationUserProfile get( List<ApplicationUserProfile> list, Profile profile ) {
		if ( list != null ) {
			for( ApplicationUserProfile aup : list ) {
				if ( profile.equals(aup.getProfile()) ) {
					return aup;
				}
			}			
		}
		return null;
	}
	
	public void updateUserProfiles() throws ManagerBeanException {
		DomainApplicationController dac = (DomainApplicationController) AonUtil.getRegisteredBean(DOMAIN_APPLICATION_CONTROLLER_NAME);
		this.userProfiles = loadUserProfiles( dac.getDomainApplication(), getApplicationUser() );
	}

	public static List<SelectTransferObject<Profile,ApplicationUserProfile>> loadUserProfiles( DomainApplication da, ApplicationUser applicationUser ) throws ManagerBeanException {
		List<SelectTransferObject<Profile,ApplicationUserProfile>> list = new LinkedList<SelectTransferObject<Profile,ApplicationUserProfile>>();
		List<ApplicationUserProfile> profiles = null;
		if ( applicationUser != null && applicationUser.getId() != null ) {
			profiles = getApplicationUserProfiles(applicationUser, false);
		}
		for( ITransferObject to : UserApplicationInfo.getProfiles(da) ) {
			Profile profile = (Profile) to;
			SelectTransferObject<Profile,ApplicationUserProfile> item = new SelectTransferObject<Profile, ApplicationUserProfile>(profile);
			item.setTo( get(profiles, profile) );
			list.add(item);
		}		
		return list;
	}
	
	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			return getProfileList( (ApplicationUser) getSelectedTO() );
		}
		return null;
	}
	
	public static String getProfileList( ApplicationUser user ) throws ManagerBeanException {
		List<ApplicationUserProfile> profiles = getApplicationUserProfiles(user, false);
		if (! profiles.isEmpty() ) {
			String[] profileNames = new String[profiles.size()];
			for( int i = 0; i < profileNames.length; i++ ) {
				profileNames[i] = profiles.get(i).getProfile().getName();
			}
			return StringUtils.join(profileNames, ", ");
		}
		return null;
	}

	public Domain getUserDomain() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			ApplicationUser user = (ApplicationUser) getSelectedTO();
			IManagerBean bean = BeanManager.getManagerBean(Domain.class);
			return (Domain) bean.get(user.getUser().getDomain());
		}
		return null;
	}
	
	public void insertUserProfiles() throws ManagerBeanException {
		insertUserProfiles( getApplicationUser(), this.userProfiles );
	}

	public static void insertUserProfiles( ApplicationUser user, List<SelectTransferObject<Profile,ApplicationUserProfile>> userProfiles ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		for( SelectTransferObject<Profile,ApplicationUserProfile> item : userProfiles ) {
			if ( item.isChecked() ) {
				if ( item.getTo() == null ) {
					ApplicationUserProfile aup = new ApplicationUserProfile();
					aup.setApplicationUser(user);
					aup.setProfile(item.getValue());
					bean.insert(aup);
					item.setTo(aup);
				}
			} else {
				item.unregister();
			}			
		}
	}
	
	public static void removeUserProfiles( ApplicationUser appUser ) throws ManagerBeanException {
		List<ApplicationUserProfile> profiles = getApplicationUserProfiles( appUser, true );
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		for( ApplicationUserProfile aup : profiles ) {
			bean.remove(aup);
		}
	}
	
	public void removeApplicationUsers( String alias, Serializable id ) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUser.class);
		Criteria criteria = new Criteria();
		criteria.setSkipDomainFilter(true);
		criteria.addEqualExpression(bean.getFieldName(alias), id);
		for( ITransferObject to : bean.getList(criteria) ) {
			removeUserProfiles( (ApplicationUser) to );
			bean.remove( to );
		}	
	}
	
}
