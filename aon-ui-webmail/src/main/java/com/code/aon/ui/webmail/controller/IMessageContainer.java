package com.code.aon.ui.webmail.controller;

import javax.faces.model.DataModel;

import com.code.aon.common.ManagerBeanException;

public interface IMessageContainer {

	int getCurrentIndex();
	
	void setCurrentIndex(int currentIndex);
	
	DataModel getModel() throws ManagerBeanException;
	
}
