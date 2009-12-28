package com.code.aon.ui.cms.controller;

import com.code.aon.cms.BrandDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;


public class BrandController extends BasicI18nController implements ICMSConstants, Constants {

	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		BrandDetail bd = (BrandDetail)getModelRowdataI18n();
		if (bd != null) label = bd.getLabel();		
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(PRODUCT).getTo()==null)
			return PRODUCT_LIST;
		return PRODUCT_FORM;
	}

}