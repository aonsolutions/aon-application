package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.util.AonUtil;

public class AdminController {

	private String user_ = "esferalia";

	private String passwd_ = "password";

	private String user = "";

	private String passwd = "";

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPasswd() {
		return passwd;
	}

	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}

	public void onAccept(ActionEvent event) {
		if (user_.equals(user) && passwd_.equals(passwd)){
			DomainUtilities domainUtilities = (DomainUtilities)AonUtil.getRegisteredBean(DomainUtilities.NAME);
			domainUtilities.assignAdminProfile();
			CmsController cms = (CmsController)AonUtil.getRegisteredBean(CmsController.NAME);
			cms.assignAdminProfile();
		}
		user = "";
		passwd = "";
	}

}
