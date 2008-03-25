package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.common.ManagerBeanException;


public class BrandController extends BasicI18nController {

	private boolean cancelOnSelect = false;

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		if (!cancelOnSelect) {
			super.onSelect(new ActionEvent(event.getComponent()));
			loadCurrentLanguage();
		}
		cancelOnSelect = false;
	}
	
	public void onActivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Brand b = (Brand)this.model.getRowData();
		b.setActive(active);
		getManagerBean().update(b);
	}
	
	public void onChecked(ValueChangeEvent event) throws ManagerBeanException {
		cancelOnSelect = true; 
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		BrandDetail bd = (BrandDetail)getModelRowdataI18n();
		if (bd != null) label = bd.getLabel();		
		return label;
	}
	

}