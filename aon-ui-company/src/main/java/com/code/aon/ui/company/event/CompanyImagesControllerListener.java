package com.code.aon.ui.company.event;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.event.RegistryAttachControllerListener;

public class CompanyImagesControllerListener extends RegistryAttachControllerListener {
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController) event.getController();
		RegistryAttachment image = (RegistryAttachment) imagesController.getTo();
		if (image != null) {
			AonFile f = new AonFile();
			f.setAttachment(image);
			f.setFileName(image.getDescription());
			MimeType mimeType = image.getMimeType();
			if ( mimeType == null ) {
				mimeType = f.resolveMimeType();
			}
			f.setMimeType(mimeType);
			imagesController.setAonFile(f);
			imagesController.init(image.getData());
		}
	}
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanAdded(event);
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		imagesController.init(attach.getData());
		imagesController.update(attach);	
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		super.beforeBeanUpdated(event);
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		imagesController.update(attach);	
	}

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		super.afterBeanCreated(event);
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		imagesController.reset();
		imagesController.setAonFile(new AonFile());
	}	
	
}