package com.code.aon.ui.document.event;

import com.code.aon.faces.controller.AttachmentUtil;
import com.code.aon.ui.document.EnterpriseDocument;
import com.code.aon.ui.document.controller.EnterpriseDocumentController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class EnterpriseDocumentControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanCreated(ControllerEvent event)throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		edc.setAonFile(null);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		edc.setAonFile(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		EnterpriseDocumentController edc = (EnterpriseDocumentController) event.getController();
		AttachmentUtil.checkFileData(edc, edc.isNew(), true);
		EnterpriseDocument ed = (EnterpriseDocument) edc.getTo();
		ed.setData( edc.getAonFile().getData() );
		ed.setMimeType( edc.getAonFile().getMimeType() );
	}
	
}