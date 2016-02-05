package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.users.JsUser;

public class DefaultAonUserIssueSelected implements UserSelected {
	
	protected JsUser user;
	
	public DefaultAonUserIssueSelected(JsUser user) {
		this.user = user;
	}

	@Override
	public Integer getUserId() {
		return user.getId();
	}

	@Override
	public String getLogin() {
		return user.getLogin();
	}

	@Override
	public String getName() {
		return user.getName();
	}
}
