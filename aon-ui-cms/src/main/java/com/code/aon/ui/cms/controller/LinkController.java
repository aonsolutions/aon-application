package com.code.aon.ui.cms.controller;

import com.code.aon.cms.LinkDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;


public class LinkController extends BasicI18nController implements Constants {

	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		LinkDetail ld = (LinkDetail)getModelRowdataI18n();
		if (ld != null) label = ld.getLabel();
		return label;
	}

}