package com.code.aon.ui.cms.controller;

import com.code.aon.cms.FaqDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;

public class FaqController extends BasicI18nController implements Constants {

	public String getI18nAnswer() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getAnswer();
		return label;
	}

	public String getI18nQuestion() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		FaqDetail fd = (FaqDetail)getModelRowdataI18n();
		if (fd != null) label = fd.getQuestion();
		return label;
	}

}