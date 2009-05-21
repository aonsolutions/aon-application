package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.FaqCategory;
import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class FaqCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(FaqConfig.class);
	}
	
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
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
		FaqCategory faqCategory = (FaqCategory)this.model.getRowData();
		faqCategory.setActive(active);
		getManagerBean().update(faqCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqCategoryDetail faqCategoryDetail = (FaqCategoryDetail)getModelRowdataI18n();
		if (faqCategoryDetail != null) label = faqCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(FAQ).getTo()==null)
			return FAQ_LIST;
		return FAQ_FORM;
	}

}