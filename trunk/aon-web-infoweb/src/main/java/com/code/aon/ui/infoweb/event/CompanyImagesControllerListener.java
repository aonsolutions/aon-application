package com.code.aon.ui.infoweb.event;

import java.io.IOException;
import java.util.ResourceBundle;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.infoweb.controller.FileController;

public class CompanyImagesControllerListener extends ControllerAdapter {
	
	private static final String COMPANY_CONTROLLER_NAME = "company"; 

    /** BASE_NAME. */
    private static final String BASE_NAME = "com.code.aon.ui.registry.i18n.messages";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Expression imageExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
			event.getController().getCriteria().addExpression(imageExp);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			FileController imagesController = (FileController)event.getController();
			if(imagesController.getAonFile().getData() != null){
				AonFile aonFile = imagesController.getAonFile(); 
				if (aonFile.getSize() > 65535){
			        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
					throw new ControllerListenerException(bundle.getString("aon_company_image_max_size_error"));
				}
				RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
				CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
				attach.setRegistry((Company)companyController.getTo());
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				//attach.setDescription(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf("\\") + 1, aonFile.getFileName().lastIndexOf(".")));
				attach.setRegistryAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
				attach.setMimeType(MimeType.getByExtension(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1)));
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
}
