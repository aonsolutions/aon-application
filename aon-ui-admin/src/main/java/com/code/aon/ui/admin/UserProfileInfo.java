package com.code.aon.ui.admin;

import static com.code.aon.ui.common.ICommonMessages.SYSTEM;

import java.io.Serializable;

import org.apache.commons.lang.StringUtils;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;
import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.admin.controller.ApplicationProfileController;
import com.code.aon.ui.util.AonUtil;

public class UserProfileInfo implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean checked;
	
	private String name;
	
	private String roleList;
	
	private String moduleDeniedList;
	
	private Profile profile;
	
	private ApplicationUserProfile userProfile;

	public UserProfileInfo(Profile profile) throws ManagerBeanException {
		this.profile = profile;
		updateName();
		this.roleList = ApplicationProfileController.getRoleList(profile);
		this.moduleDeniedList = ApplicationProfileController.getModuleDeniedList(profile);
	}
	
	public boolean isChecked() {
		return checked;
	}

	public void setChecked(boolean checked) {
		this.checked = checked;
	}

	public Profile getProfile() {
		return profile;
	}

	public ApplicationUserProfile getUserProfile() {
		return userProfile;
	}

	public void setUserProfile(ApplicationUserProfile userProfile) {
		this.userProfile = userProfile;
	}

	private void updateName() {
		String suffix = null;
		this.name = profile.getName();
		if ( profile.getDomain() == null ) {
			suffix = AonUtil.getMessage(SYSTEM); 
		} else if (! DomainManager.getCurrentDomain().equals(profile.getDomain().getId()) ) {
			suffix = profile.getDomain().getDescription();
		}
		if (! StringUtils.isEmpty(suffix) ) {
			this.name += " (" + suffix + ")";
		}
	}

	public String getName() {
		return name;
	}

	public String getRoleList() {
		return roleList;
	}

	public String getModuleDeniedList() {
		return moduleDeniedList;
	}
	
}
