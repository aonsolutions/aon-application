package com.code.aon.ui.admin;

import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_FORM_TEMPLATE;
import static com.code.aon.ui.admin.controller.IAdminConstants.DOMAIN_LIST_TEMPLATE;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.admin.controller.AdminMainController;

public enum UserType {

	ESFERALIA(DOMAIN_LIST_TEMPLATE),
	
	NORMAL(DOMAIN_FORM_TEMPLATE),
	
	PARENT(DOMAIN_LIST_TEMPLATE);
	
	private String template;
	
	UserType( String template ) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
	
	public String getResource() {
		return AdminMainController.PROPERTIES_PATH + StringUtils.lowerCase(toString()) + ".properties";
	}
	
}
