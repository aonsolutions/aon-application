package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Image;
import com.code.aon.cms.SportNationality;
import com.code.aon.cms.SportNationalityDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;


public class SportNationalityController extends BasicI18nController {
	
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public String getI18nDescription() throws ManagerBeanException {
		String description = "- NO VALUE -";
		SportNationalityDetail detail = (SportNationalityDetail)getModelRowdataI18n();
		if (detail != null) description = detail.getDescription();
		return description;
	}

	public void onDelImage(ActionEvent event) {
		SportNationality current = (SportNationality)getTo();
		current.setImage(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		SportNationality current = (SportNationality)getTo();
		current.setImage(image);
	}

}