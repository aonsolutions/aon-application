package com.code.aon.ui.company.event;

import java.io.IOException;

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
			CompanyImagesController imagesController = (CompanyImagesController) event.getController();
			String alias = imagesController.getFieldName(IRegistryAlias.REGISTRY_ATTACHMENT_REGISTRY_ATTACHMENT_TYPE); 
			Expression expression = null;
			for( RegistryAttachmentType type : imagesController.getDisplayedTypes() ) {
				if ( expression == null ) {
					expression = ExpressionUtilities.getEqualExpression(alias, type);				
				} else {
					Expression exp  = ExpressionUtilities.getEqualExpression(alias, type);
					expression = ExpressionUtilities.getOrExpression(expression, exp);
				}
			}
			if ( expression != null ) {
				imagesController.getCriteria().addExpression(expression);
			}			
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("Error before model Initialized",e);
		}
	}
	
	/*
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			FileController imagesController = (FileController)event.getController();
			if(imagesController.getAonFile().getData() != null){
				AonFile aonFile = imagesController.getAonFile(); 
				if (aonFile.getSize() > imagesController.getMaximumSize()) {
					String message = AonUtil.getMessage(IInfoWebConstants.BUNDLE_NAME, "infoweb_image_max_size_error");
					String formatted = AonUtil.substituteParams(AonUtil.getCurrentLocale(), message, new Object[]{imagesController.getMaximumSize()});
					throw new ControllerListenerException(formatted);					
				}				
				RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
				CompanyController companyController = (CompanyController)FormUtil.getController(ICompanyConstants.COMPANY_CONTROLLER_NAME);
				attach.setRegistry((Company)companyController.getTo());
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				attach.setRegistryAttachmentType(RegistryAttachmentType.ADDITIONAL_IMAGE);
				attach.setMimeType(MimeType.getByExtension(aonFile.getFileName().substring(aonFile.getFileName().lastIndexOf(".") + 1)));
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
	*/
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController) event.getController();
		RegistryAttachment image = (RegistryAttachment) imagesController.getTo();
		if (image != null) {
			AonFile f = new AonFile();
			f.setData(image.getData());
			f.setFileName(image.getDescription());
			imagesController.setAonFile(f);
			MimeType mimeType = image.getMimeType();
			if ( mimeType == null ) {
				mimeType = CompanyImagesController.getMimeType(f.getFileName(), f.getData());
			}
			imagesController.setMimeType(mimeType);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController) event.getController();
		imagesController.setAonFile(new AonFile());
		imagesController.setMimeType(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			CompanyImagesController imagesController = (CompanyImagesController)event.getController();
			AonFile aonFile = imagesController.getAonFile();
			if(aonFile.getSize() > 0) {				 
				if (aonFile.getSize() > imagesController.getMaximumSize()) {
					String message = AonUtil.getMessage(BUNDLE_NAME, "company_image_max_size_error");
					String formatted = AonUtil.substituteParams(AonUtil.getCurrentLocale(), message, new Object[]{imagesController.getMaximumSize()});
					throw new ControllerListenerException(formatted);										
				}
				RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
				attach.setCategory(null);
				attach.setData(aonFile.getData());
				MimeType mimeType = CompanyImagesController.getMimeType(aonFile.getFileName(), aonFile.getData());
				attach.setMimeType(mimeType);
			}	
		} catch (IOException e) {
			throw new ControllerListenerException("Error uploading file");
		}
	}
	
}
