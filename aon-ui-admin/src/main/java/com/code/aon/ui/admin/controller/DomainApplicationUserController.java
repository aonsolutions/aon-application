package com.code.aon.ui.admin.controller;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.ApplicationUser;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.entity.IEntityAlias;

public class DomainApplicationUserController extends LinesController {
	
	private Profile[] userProfiles;
	
	public Profile[] getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(Profile[] userProfiles) {
		this.userProfiles = userProfiles;
	}
	
	public ApplicationUser getApplicationUser() {
		return (ApplicationUser) getTo();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<ApplicationUserProfile> getApplicationUserProfiles( ApplicationUser user ) throws ManagerBeanException  {
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		Criteria criteria = new Criteria();
		String alias = bean.getFieldName(IEntityAlias.APPLICATION_USER_PROFILE_APPLICATION_USER_ID);
		criteria.addEqualExpression(alias, user.getId());
		return (List) bean.getList(criteria);
	}
	
	private Profile[] getUserProfiles( ApplicationUser user ) throws ManagerBeanException  {
		List<ApplicationUserProfile> list = getApplicationUserProfiles(user);
		if (! list.isEmpty() ) {
			Profile[] profiles = new Profile[list.size()];
			for( int i = 0; i < profiles.length; i++ ) {
				profiles[i] = list.get(i).getProfile();
			}
			return profiles;
		}		
		return null;
	}
	
	public void updateUserProfiles() throws ManagerBeanException {
		this.userProfiles = getUserProfiles( getApplicationUser() );
	}
	
	public String getProfileList() throws ManagerBeanException {
		if ( getModel().isRowAvailable() ) {
			ApplicationUser user = (ApplicationUser) getSelectedTO();
			Profile[] profiles = getUserProfiles(user);
			if (! ArrayUtils.isEmpty(profiles) ) {
				String[] profileNames = new String[profiles.length];
				for( int i = 0; i < profileNames.length; i++ ) {
					profileNames[i] = profiles[i].getName();
				}
				return StringUtils.join(profileNames, ", ");
			}
		}
		return null;
	}
	
	public void insertUserProfiles( boolean _new) throws ManagerBeanException {
		List<Profile> profiles = new LinkedList<Profile>(Arrays.asList(this.userProfiles));
		ApplicationUser user = getApplicationUser();
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		if (! _new ) {
			List<ApplicationUserProfile> oldProfiles = getApplicationUserProfiles(user);
			if (! oldProfiles.isEmpty() ) {
				for( ApplicationUserProfile aup : oldProfiles ) {
					if ( profiles.contains(aup.getProfile()) ) {
						profiles.remove(aup.getProfile());
					} else {
						bean.remove(aup);
					}
				}
			}
		}
		if (! profiles.isEmpty() ) {
			for( Profile profile : profiles ) {
				ApplicationUserProfile aup = new ApplicationUserProfile();
				aup.setApplicationUser(user);
				aup.setProfile(profile);
				bean.insert(aup);
			}			
		}
	}

	public void removetUserProfiles() throws ManagerBeanException {
		ApplicationUser user = getApplicationUser();
		List<ApplicationUserProfile> profiles = getApplicationUserProfiles(user);
		IManagerBean bean = BeanManager.getManagerBean(ApplicationUserProfile.class);
		for( ApplicationUserProfile aup : profiles ) {
			bean.remove(aup);
		}
	}
	
}
