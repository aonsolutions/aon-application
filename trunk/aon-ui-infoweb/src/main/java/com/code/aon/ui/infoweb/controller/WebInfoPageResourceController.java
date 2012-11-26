package com.code.aon.ui.infoweb.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.infoweb.WebInfoPage;
import com.code.aon.infoweb.WebInfoPageResource;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.esferalia.aon.entity.IEntityAlias;

public class WebInfoPageResourceController extends BasicController implements IInfoWebConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(CompanyWebInfoPageController.class.getName());
	
	private WebInfoPage webInfoPage;
	
	public void onSelect(ActionEvent event, WebInfoPage webInfoPage) {
		try {
			clearCriteria();
			Criteria criteria = getCriteria();
			criteria.addEqualExpression(getFieldName(IEntityAlias.WEB_INFO_PAGE_RESOURCE_WEB_INFO_PAGE_ID), webInfoPage.getId());
			onCancel(event);
			initializeModel();
			this.webInfoPage = webInfoPage;
		} catch (ManagerBeanException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}	
	
	@Override
	public void accept(ActionEvent event) {
		if (isNew()) {
			WebInfoPageResource resource = (WebInfoPageResource) getTo();
			resource.setWebInfoPage(webInfoPage);
		}
		super.accept(event);
	}	
	
}
