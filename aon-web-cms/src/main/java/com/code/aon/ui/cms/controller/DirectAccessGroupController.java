package com.code.aon.ui.cms.controller;

import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;


public class DirectAccessGroupController extends BasicI18nController implements ICMSConstants, Constants {
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		DirectAccessGroupDetail directAccessGroupDetail = (DirectAccessGroupDetail)getModelRowdataI18n();
		if (directAccessGroupDetail != null) label = directAccessGroupDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(DIRECT_ACCESS).getTo()==null)
			return DIRECT_ACCESS_LIST;
		return DIRECT_ACCESS_FORM;
	}

}