package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

public class DiaryController extends BasicI18nController{

	public void onInit(ActionEvent event) {
		onSearch(event);
		if (getWrappedList().isEmpty()){
			onReset(event);
		}else{
			onSelectFirst(event);
		}
		loadCurrentLanguage();
	}

	
}
