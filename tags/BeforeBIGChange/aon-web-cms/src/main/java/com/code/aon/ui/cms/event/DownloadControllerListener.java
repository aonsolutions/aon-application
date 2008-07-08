package com.code.aon.ui.cms.event;

import com.code.aon.cms.Download;
import com.code.aon.cms.DownloadDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.DownloadController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DownloadControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanRemoved(ControllerEvent event)
			throws ControllerListenerException {
		DownloadController controller = (DownloadController) event.getController();
		try {
			controller.orderedControllerSupport.reorderObjects(controller);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		DownloadController controller = (DownloadController)event.getController();
		controller.orderedControllerSupport.addListenerSupport(controller);
	}
	

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		Download download = (Download)event.getController().getTo();
		download.setActive(true);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DownloadController dc = (DownloadController)event.getController();
		DownloadDetail dd = (DownloadDetail)dc.getToI18n();
		if (dd.getFile()==null ||
				dd.getFile().trim().equals("")){
			throw new ControllerListenerException("Asigna el archivo.");
		}
		Download d = (Download)event.getController().getTo();		
		d.setDownloadCategory(dc.getCurrentDownloadCategory());
		d.setPosition(dc.orderedControllerSupport.getLastPosition(dc));
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		DownloadController dc = (DownloadController)event.getController();
		DownloadDetail dd = (DownloadDetail)dc.getToI18n();
		if (dd.getFile()==null ||
				dd.getFile().trim().equals("")){
			throw new ControllerListenerException("Asigna el archivo.");
		}
	}

}