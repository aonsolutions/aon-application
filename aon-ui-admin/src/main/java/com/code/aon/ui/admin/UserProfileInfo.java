package com.code.aon.ui.admin;

import com.code.aon.admin.ApplicationUserProfile;
import com.code.aon.admin.Profile;

public class UserProfileInfo {

	private boolean checked;
	
	private Profile profile;
	
	private ApplicationUserProfile userProfile;

	public UserProfileInfo(Profile profile) {
		this.profile = profile;
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
	
}
