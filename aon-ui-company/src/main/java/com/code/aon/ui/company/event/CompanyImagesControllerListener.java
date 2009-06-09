package com.code.aon.ui.company.event;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIInput;

import org.apache.commons.lang.ArrayUtils;

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
import com.sun.faces.util.MessageFactory;

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

	private void checkAonFile( CompanyImagesController imagesController ) throws ControllerListenerException {
		AonFile aonFile = imagesController.getAonFile();
		if ( ArrayUtils.isEmpty(aonFile.getData()) ) {
			FacesMessage message = MessageFactory.getMessage( UIInput.REQUIRED_MESSAGE_ID, AonUtil.getMessage("aon_fileupload_element") );
			throw new ControllerListenerException( message.getSummary() );									
		} else if (aonFile.getSize() > imagesController.getMaximumSize()) {
			String message = AonUtil.getMessage(BUNDLE_NAME, COMPANY_IMAGE_MAX_SIZE_ERROR);
			String formatted = AonUtil.substituteParams(AonUtil.getCurrentLocale(), message, new Object[]{imagesController.getMaximumSize()});
			throw new ControllerListenerException(formatted);										
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		checkAonFile(imagesController);
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		attach.setRegistryAttachmentType(imagesController.getAttachmentType());
		CompanyImagesController.update(attach, imagesController.getAonFile());	
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		checkAonFile(imagesController);
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		CompanyImagesController.update(attach, imagesController.getAonFile());	
	}
	
}
