package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.DownloadCategory;
import com.code.aon.cms.DownloadCategoryDetail;
import com.code.aon.cms.DownloadConfig;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class DownloadCategoryController extends BasicI18nController implements ICMSConstants, Constants {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	public void onInit(ActionEvent event) {
		((GeneratorConfigController)AonUtil.getRegisteredBean(GENERATOR_CONFIG)).initSection(DownloadConfig.class);
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
		DownloadCategory downloadCategory = (DownloadCategory)this.model.getRowData();
		downloadCategory.setActive(active);
		getManagerBean().update(downloadCategory);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		DownloadCategoryDetail downloadCategoryDetail = (DownloadCategoryDetail)getModelRowdataI18n();
		if (downloadCategoryDetail != null) label = downloadCategoryDetail.getLabel();
		return label;
	}


	public void onDelImage(ActionEvent event) {
		DownloadCategory current = (DownloadCategory)getTo();
		current.setImage(null);
	}

	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean(GALLERY);
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		DownloadCategory current = (DownloadCategory)getTo();
		current.setImage(image);
	}
	
	public String getBack(){
		if (FormUtil.getController(DOWNLOAD).getTo()==null)
			return DOWNLOAD_LIST;
		return DOWNLOAD_FORM;
	}

}