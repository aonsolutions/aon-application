package com.code.aon.ui.company.event;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.common.util.AonFile;
import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.registry.RegistryAttachment;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.company.controller.CompanyImagesController;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.event.RegistryAttachControllerListener;
import com.code.aon.ui.util.AonUtil;

public class CompanyImagesControllerListener extends RegistryAttachControllerListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		checkImage(imagesController);
		super.beforeBeanAdded(event);
		RegistryAttachment attach = (RegistryAttachment)imagesController.getTo();
		imagesController.init(attach.getData());
		imagesController.update(attach);	
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		CompanyImagesController imagesController = (CompanyImagesController)event.getController();
		checkImage(imagesController);
		super.beforeBeanUpdated(event);
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

	private void checkImage( CompanyImagesController controller ) throws ControllerListenerException {
		if ( AttachmentUtil.isUploaded(controller) && !controller.isImage() ) {
			String message = AonUtil.getMessage(ICommonMessages.COMPANY_IMAGE_NOT_IMAGE);
			throw new ControllerListenerException(message);
		}
	}
	
}