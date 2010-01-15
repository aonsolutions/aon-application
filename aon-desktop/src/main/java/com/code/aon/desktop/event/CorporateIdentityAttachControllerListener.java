package com.code.aon.desktop.event;

import java.io.IOException;
import java.util.ResourceBundle;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.desktop.controller.CorporateIdentityAttachController;
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

public class CorporateIdentityAttachControllerListener extends ControllerAdapter {
	
	private static final String COMPANY_CONTROLLER_NAME = "company"; 

    /** BASE_NAME. */
    private static final String BASE_NAME = "com.code.aon.desktop.i18n.messages";

	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Expression imageExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.CORPORATE_IDENTITY);
			event.getController().getCriteria().addExpression(imageExp);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CorporateIdentityAttachController ciaController = (CorporateIdentityAttachController)event.getController();
			if(ciaController.getAonFile().getData() != null){
				AonFile aonFile = ciaController.getAonFile(); 
				if (aonFile.getSize() > 1048576){
			        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME); 
					throw new ControllerListenerException(bundle.getString("company_image_max_size_error"));
				}
				RegistryAttachment attach = (RegistryAttachment)ciaController.getTo();
				CompanyController companyController = (CompanyController)FormUtil.getController(COMPANY_CONTROLLER_NAME);
				attach.setRegistry((Company)companyController.getTo());
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				String ext = aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1);
				MimeType mt = MimeType.getByExtension(ext);
				attach.setMimeType(mt);
				if (attach.getDescription() == null || attach.getDescription().trim().equals("")) {
					attach.setDescription(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf("\\") + 1));
				}
				else {
					if (attach.getDescription().indexOf(".") < 0) attach.setDescription(attach.getDescription() + "." + ext);
				}
				attach.setRegistryAttachmentType(RegistryAttachmentType.CORPORATE_IDENTITY);
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
}
