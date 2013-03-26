package com.code.aon.ui.common;

import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.ui.util.AonUtil;

public class LocaleElement {
	
	private Locale locale;

	public LocaleElement(Locale locale) {
		this.locale = locale;
	}

	public String getId() {
		return locale.toString();
	}
	
	public String getDisplayName() {
		return this.locale.getDisplayLanguage(AonUtil.getCurrentLocale());
	}

	public String getIconClass() {
		return "aon-icon-flag-" + this.locale.getLanguage();
	}

	public void onChangeLanguage( ActionEvent event ) {
		changeLanguage();
	}
	
	public void changeLanguage() {
		FacesContext.getCurrentInstance().getViewRoot().setLocale( locale );		
	}
	
}
