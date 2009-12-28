package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.LinkCategoryDetail;
import com.code.aon.cms.LinkConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;


public class LinkCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(LinkConfig.class);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		LinkCategoryDetail linkCategoryDetail = (LinkCategoryDetail)getModelRowdataI18n();
		if (linkCategoryDetail != null) label = linkCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(LINK).getTo()==null)
			return LINK_LIST;
		return LINK_FORM;
	}
	
}