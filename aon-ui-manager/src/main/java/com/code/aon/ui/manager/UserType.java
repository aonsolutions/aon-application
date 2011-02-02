package com.code.aon.ui.manager;

import org.apache.commons.lang.StringUtils;

import com.code.aon.ui.manager.controller.ManagerController;

public enum UserType {

	ESFERALIA( "/homepage.xhtml" ),
	
	NORMAL("/com/code/aon/ui/manager/facelet/domain/form.xhtml"),
	
	PARENT("/com/code/aon/ui/manager/facelet/domain/list.xhtml");
	
	private String template;
	
	UserType( String template ) {
		this.template = template;
	}

	public String getTemplate() {
		return template;
	}
	
	public String getResource() {
		return ManagerController.PROPERTIES_PATH + StringUtils.lowerCase(toString()) + ".properties";
	}
	
}
