package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Brand;
import com.code.aon.cms.BrandDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;


public class BrandController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(new ActionEvent(event.getComponent()));
		loadCurrentLanguage();
	}
	
	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Brand b = (Brand)this.model.getRowData();
		b.setActive(active);
		getManagerBean().update(b);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		BrandDetail bd = (BrandDetail)getModelRowdataI18n();
		if (bd != null) label = bd.getLabel();		
		return label;
	}

	public String getBack(){
		if (FormUtil.getController("product").getTo()==null)
			return "product_list";
		return "product_form";
	}

}