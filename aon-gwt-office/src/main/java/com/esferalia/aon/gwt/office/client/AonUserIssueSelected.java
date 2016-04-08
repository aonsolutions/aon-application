package com.esferalia.aon.gwt.office.client;

import com.esferalia.aon.gwt.office.client.models.users.JsUser;
import com.google.gwt.http.client.URL;

public class DefaultAonUserIssueSelected implements UserSelected {
	
	protected JsUser user;
	private String login;
	private String name;
	
	public DefaultAonUserIssueSelected(JsUser user) {
		this.user = user;
		this.login = URL.decode(user.getLogin());
		this.name = URL.decode(user.getName());
	}

	@Override
	public Integer getUserId() {
		return user.getId();
	}

	@Override
	public String getLogin() {
		return this.login;
	}

	@Override
	public String getName() {
		return this.name;
	}
}
