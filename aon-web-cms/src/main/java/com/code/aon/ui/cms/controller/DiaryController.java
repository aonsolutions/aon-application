package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

public class DiaryController extends BasicI18nController{

	private boolean richTextEnabled;

	public boolean isRichTextEnabled() {
		return richTextEnabled;
	}

	public void setRichTextEnabled(boolean richTextEnabled) {
		this.richTextEnabled = richTextEnabled;
	}
	
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
