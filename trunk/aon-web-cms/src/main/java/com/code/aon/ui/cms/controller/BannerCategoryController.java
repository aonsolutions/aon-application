package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.BannerCategory;
import com.code.aon.cms.BannerCategoryDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;

public class BannerCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	@SuppressWarnings("unused")
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
		BannerCategory bannerCategory = (BannerCategory)this.model.getRowData();
		bannerCategory.setActive(active);
		getManagerBean().update(bannerCategory);
	}

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