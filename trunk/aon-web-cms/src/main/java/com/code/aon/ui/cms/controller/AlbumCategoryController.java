package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.AlbumCategoryDetail;
import com.code.aon.cms.AlbumConfig;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class AlbumCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(AlbumConfig.class);
	}

	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		AlbumCategoryDetail albumCategoryDetail = (AlbumCategoryDetail)getModelRowdataI18n();
		if (albumCategoryDetail != null) label = albumCategoryDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(ALBUM).getTo()==null)
			return ALBUM_LIST;
		return ALBUM_FORM;
	}

}
