package com.code.aon.ui.accounting.event;

import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class AutConceptControllerListener extends ControllerAdapter {
	private static final String COLLECTIONS_CONTROLLER = "accountingCollections";

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		initializeCollection();
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		initializeCollection();
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		initializeCollection();
	}

	private void initializeCollection() {
		AccountingCollectionsController acc = (AccountingCollectionsController) 
			AonUtil.getRegisteredBean(COLLECTIONS_CONTROLLER);
		acc.setAutoConcepts(null);
		acc.setConceptsDescriptions(null);
	}
}
