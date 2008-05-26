package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.HiruOrganizerCentre;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.GridController;


public class HiruOrganizerCentreController extends GridController {

	public void onActivate(ActionEvent event) throws ManagerBeanException {
		activate(true);
	}

	public void onDeactivate(ActionEvent event) throws ManagerBeanException {
		activate(false);
	}
	
	private void activate(boolean active) throws ManagerBeanException {
		HiruOrganizerCentre f = (HiruOrganizerCentre)this.model.getRowData();
		f.setActive(active);
		getManagerBean().update(f);
	}
}