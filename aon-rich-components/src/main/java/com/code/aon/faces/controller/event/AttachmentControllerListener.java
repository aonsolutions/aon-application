package com.code.aon.faces.controller.event;

import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.faces.controller.IAttachmentController;
import com.code.aon.faces.controller.AttachmentController;
import com.code.aon.ui.common.ICommonConstants;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AttachmentControllerListener extends ControllerAdapter implements ICommonConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		IAttachmentController controller = (IAttachmentController) event.getController();
		controller.setAonFile(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		IAttachmentController controller = (IAttachmentController) event.getController();
		controller.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		IAttachmentController controller = (IAttachmentController) event.getController();
		AttachmentUtil.checkFileData(controller, event.getController().isNew(), true);
		AttachmentUtil.updateAttachment(controller);
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		AttachmentController controller = (AttachmentController) event.getController();
		AttachmentUtil.checkFileData(controller, event.getController().isNew(), true);
		AttachmentUtil.updateAttachment(controller);
	}
	
}
