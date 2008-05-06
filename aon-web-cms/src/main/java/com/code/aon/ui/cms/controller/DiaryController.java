package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.ui.form.BasicController;

public class DiaryController extends BasicController{

	public void onInit(ActionEvent event) {
		onSearch(event);
		if (getWrappedList().isEmpty()){
			onReset(event);
		}else{
			onSelectFirst(event);
		}
	}

	
}
