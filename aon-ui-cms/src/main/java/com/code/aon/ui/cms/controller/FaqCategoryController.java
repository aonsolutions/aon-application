package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.FaqCategoryDetail;
import com.code.aon.cms.FaqConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class FaqCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(FaqConfig.class);
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