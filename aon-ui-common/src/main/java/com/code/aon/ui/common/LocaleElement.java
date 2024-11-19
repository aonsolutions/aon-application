package com.code.aon.ui.common;

import java.io.Serializable;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import com.code.aon.AonVersion;
import com.code.aon.ui.util.AonUtil;

public class LocaleElement implements Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Locale locale;

	public LocaleElement(Locale locale) {
		this.locale = locale;
	}

	public String getId() {
		return locale.toString();
	}
	
	public String getLanguage() {
		return locale.getLanguage();
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
		AonUtil.getConfigurationController().setLocale(locale);
	}
	
}
