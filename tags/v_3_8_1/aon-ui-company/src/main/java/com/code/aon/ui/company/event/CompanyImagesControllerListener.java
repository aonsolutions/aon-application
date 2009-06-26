package com.code.aon.ui.company.event;

import java.io.IOException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.dao.IRegistryAlias;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesControllerListener extends ControllerAdapter implements ICompanyConstants {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			IManagerBean rAttachBean = BeanManager.getManagerBean(RegistryAttachment.class);
			Expression imageExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.ADDITIONAL_IMAGE);
			Expression bannerExp = ExpressionUtilities.getEqualExpression(rAttachBean.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE), RegistryAttachmentType.BANNER);
			Expression orExpr = ExpressionUtilities.getOrExpression(imageExp, bannerExp);
			event.getController().getCriteria().addExpression(orExpr);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyImagesController imagesController = (CompanyImagesController)event.getController();
			if(imagesController.getAonFile().getData() != null){
				AonFile aonFile = imagesController.getAonFile(); 
				if (aonFile.getSize() > IMAGE_MAX_SIZE) {
					String message = AonUtil.getMessage(BUNDLE_NAME, "company_image_max_size_error");
					throw new ControllerListenerException(message);					
				}
				RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setDescription(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf("\\") + 1, aonFile.getFileName().lastIndexOf(".")));
				attach.setMimeType(MimeType.getByExtension(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1)));
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
	
}
