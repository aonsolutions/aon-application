package com.code.aon.ui.cms.controller;

import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;

public class BannerCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		BannerCategoryDetail bannerCategoryDetail = (BannerCategoryDetail)getModelRowdataI18n();
		if (bannerCategoryDetail != null) label = bannerCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(BANNNER).getTo()==null)
			return BANNNER_LIST;
		return BANNNER_FORM;
	}

}