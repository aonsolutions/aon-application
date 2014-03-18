package com.code.aon.ui.tas.event;

import com.code.aon.AonVersion;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.tas.controller.TasCollectionsController;
import com.code.aon.ui.util.AonUtil;

public class MakeCollectionInitializer extends ControllerAdapter{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		TasCollectionsController tcc = (TasCollectionsController) AonUtil.getRegisteredBean("tasCollections");
		tcc.initializeMakes();
		
	}

}
