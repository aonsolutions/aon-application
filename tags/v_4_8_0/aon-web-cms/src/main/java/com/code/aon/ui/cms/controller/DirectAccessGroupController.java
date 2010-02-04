package com.code.aon.ui.cms.controller;

import javax.faces.event.ActionEvent;

import com.code.aon.cms.DirectAccessGroup;
import com.code.aon.cms.DirectAccessGroupDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.Constants;
import com.code.aon.ui.form.FormUtil;


public class DirectAccessGroupController extends BasicI18nController implements ICMSConstants, Constants {

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
		DirectAccessGroup directAccessGroup = (DirectAccessGroup)this.model.getRowData();
		directAccessGroup.setActive(active);
		getManagerBean().update(directAccessGroup);
	}
	
	public String getI18nLabel() throws ManagerBeanException {
		String label = NO_VALUE_LABEL;
		DirectAccessGroupDetail directAccessGroupDetail = (DirectAccessGroupDetail)getModelRowdataI18n();
		if (directAccessGroupDetail != null) label = directAccessGroupDetail.getLabel();
		return label;
	}

	public String getBack(){
		if (FormUtil.getController(DIRECT_ACCESS).getTo()==null)
			return DIRECT_ACCESS_LIST;
		return DIRECT_ACCESS_FORM;
	}

}