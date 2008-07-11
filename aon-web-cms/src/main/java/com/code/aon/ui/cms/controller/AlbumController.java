package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.Album;
import com.code.aon.cms.AlbumDetail;
import com.code.aon.cms.Image;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class AlbumController extends BasicI18nController {

	private int page;
	
	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
	
	@SuppressWarnings("unused")
	public void onSelect(ActionEvent event){
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
		Album a = (Album)this.model.getRowData();
		a.setActive(active);
		getManagerBean().update(a);
	}
	
	public String getI18nTitle() throws ManagerBeanException {
		String title = "- NO VALUE -";
		AlbumDetail ad = (AlbumDetail)getModelRowdataI18n();
		if (ad != null) title = ad.getTitle();
		return title;
	}


	public void onDelImage(ActionEvent event) {
		Album current = (Album)getTo();
		current.setImage(null);
	}
	
	public void onSelectImage(ActionEvent event) throws ManagerBeanException {
		GalleryController controller = (GalleryController)AonUtil.getRegisteredBean("gallery");
		String image = ((Image)controller.getModel().getRowData()).getRelativePath();
		Album current = (Album)getTo();
		current.setImage(image);
	}

}