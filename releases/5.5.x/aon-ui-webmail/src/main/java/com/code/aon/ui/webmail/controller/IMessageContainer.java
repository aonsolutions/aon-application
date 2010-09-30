package com.code.aon.ui.webmail.controller;

import javax.faces.model.DataModel;

public interface IMessageContainer {

	int getCurrentIndex();
	
	void setCurrentIndex(int currentIndex);
	
	DataModel getModel();
	
}
