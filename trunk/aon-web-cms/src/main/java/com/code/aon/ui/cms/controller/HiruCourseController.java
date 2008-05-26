package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.HiruCourse;
import com.code.aon.common.ManagerBeanException;


public class HiruCourseController extends BasicI18nController {

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
		HiruCourse f = (HiruCourse)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
	
	@SuppressWarnings("unused")
	public void onBuildFile(ActionEvent event) {
		
	}

	public static void main(String[] args) {
		
	}
}