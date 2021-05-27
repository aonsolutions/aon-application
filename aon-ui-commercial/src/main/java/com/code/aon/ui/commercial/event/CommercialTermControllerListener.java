package com.code.aon.ui.commercial.event;

import com.code.aon.commercial.CommercialTerm;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.commercial.controller.CommercialTermController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class CommercialTermControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		CommercialTermController controller = (CommercialTermController)event.getController();
		CommercialTerm term = (CommercialTerm)controller.getTo();
		term.setGeneral(true);
		try {
			term.setLine(controller.calculateNextLine(term.isGeneral()));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		event.getController().initializeModel();
	}

}