package com.code.aon.ui.form;

import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;

public interface ITemplateController {
	
    DataModel getModel() throws ManagerBeanException;

	Integer getPageLimit();
    
	int getPage();
	
	void setPage(int page);
	
	String getBeanName();

}