package com.code.aon.ui.marketing.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.marketing.NewsletterDetail;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.marketing.controller.NewsletterDetailController;

public class NewletterDetailControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		NewsletterDetailController ndc = (NewsletterDetailController) event.getController();
		NewsletterDetail nd = (NewsletterDetail) ndc.getTo();
		nd.setPosition(ndc.getNextPosition());
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		NewsletterDetailController ndc = (NewsletterDetailController)event.getController();
		try {
			ndc.reorderObjects();
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
}
