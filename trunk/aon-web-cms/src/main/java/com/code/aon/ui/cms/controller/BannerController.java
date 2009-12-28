package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.util.AonUtil;

public class BannerController extends BasicI18nController implements ICMSConstants, Constants {

	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		BannerDetail bd = (BannerDetail)getModelRowdataI18n();
		if (bd != null) label = bd.getLabel();
		return label;
	}

	public void onDelImage(ActionEvent event) {
		BannerDetail current = (BannerDetail)getToI18n();
		current.setImage(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		BannerDetail current = (BannerDetail)getToI18n();
		current.setImage(image);
	}

}