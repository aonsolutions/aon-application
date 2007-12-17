package com.code.aon.ui.cms.controller;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.icesoft.faces.component.ext.RowSelectorEvent;


public class FooterController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(RowSelectorEvent event) throws ManagerBeanException {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			FacesContext.getCurrentInstance().getApplication().getNavigationHandler().handleNavigation(FacesContext.getCurrentInstance(), null, "footer_form");
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}


}