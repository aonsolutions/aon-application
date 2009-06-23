package com.code.aon.ui.infoweb.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesControllerListener extends ControllerAdapter {
	
	private static final String COMPANY_CONTROLLER_NAME = "company"; 

	@Override
	public void beforeModelInitialized(ControllerEvent event)throws ControllerListenerException {
		try {
			IController imagesController = (IController) event.getController();
			imagesController.getCriteria().addEqualExpression(imagesController.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE),
							RegistryAttachmentType.ADDITIONAL_IMAGE);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error loading asociated Images", e);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		IController imagesController = (IController) event.getController();
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		CompanyController companyController = (CompanyController)AonUtil.getController(COMPANY_CONTROLLER_NAME);
		attach.setRegistry((Company)companyController.getTo());
		attach.setRegistryAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
		attach.setCategory(null);
	}
	
}
