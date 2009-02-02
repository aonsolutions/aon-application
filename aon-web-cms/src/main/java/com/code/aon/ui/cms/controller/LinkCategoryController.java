package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.LinkCategory;
import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;


public class LinkCategoryController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean("generator_config")).initSection(LinkConfig.class);
	}

	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event)  {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		LinkCategory linkCategory = (LinkCategory)this.model.getRowData();
		linkCategory.setActive(active);
		getManagerBean().update(linkCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = "- NO VALUE -";
		LinkCategoryDetail linkCategoryDetail = (LinkCategoryDetail)getModelRowdataI18n();
		if (linkCategoryDetail != null) label = linkCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController("link").getTo()==null)
			return "link_list";
		return "link_form";
	}
	
}