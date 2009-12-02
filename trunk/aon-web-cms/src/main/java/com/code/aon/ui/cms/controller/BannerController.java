package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Banner;
import com.code.aon.cms.BannerDetail;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.util.AonUtil;

public class BannerController extends BasicI18nController implements ICMSConstants, Constants {

	public void onSelect(ActionEvent event) {
		super.onSelect(event);
		loadCurrentLanguage();
	}

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		Banner b = (Banner)this.model.getRowData();
		b.setActive(active);
		getManagerBean().update(b);
	}
	
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