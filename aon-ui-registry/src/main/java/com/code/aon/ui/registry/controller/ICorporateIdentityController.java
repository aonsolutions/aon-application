package com.code.aon.ui.registry.controller;

import com.code.aon.common.ManagerBeanException;

public interface ICorporateIdentityController {

	boolean isServiconvenios();
	
	String getCurrentTagList() throws ManagerBeanException;
	
}
