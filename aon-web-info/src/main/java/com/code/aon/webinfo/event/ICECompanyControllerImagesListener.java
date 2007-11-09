package com.code.aon.webinfo.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.webinfo.controller.ICECompanyImagesController;

public class ICECompanyControllerImagesListener extends ControllerAdapter {
	
	private static final String COMPANY_IMAGES_CONTROLLER_NAME = "companyImages";

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try {
			Company company = (Company)event.getController().getTo();
			ICECompanyImagesController companyImagesController = (ICECompanyImagesController)AonUtil.getController(COMPANY_IMAGES_CONTROLLER_NAME);
			Criteria criteria = companyImagesController.getCriteria();
			criteria.addEqualExpression(companyImagesController.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ID), company.getId());
			companyImagesController.onSearch(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error loading asociated Images", e);
		}
	}
}
