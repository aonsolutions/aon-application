package com.code.aon.ui.infoweb.event;

import java.io.IOException;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.registry.enumeration.RegistryAttachmentType;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.infoweb.controller.FileController;
import com.code.aon.ui.infoweb.controller.IInfoWebConstants;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesControllerListener extends ControllerAdapter {
	
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
}
