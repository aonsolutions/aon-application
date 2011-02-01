package com.code.aon.ui.cms.event;

import com.code.aon.cms.DownloadDetail;
import com.code.aon.ui.cms.controller.DownloadController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DownloadControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		DownloadController dc = (DownloadController)event.getController();
		DownloadDetail dd = (DownloadDetail)dc.getToI18n();
		if (dd.getFile()==null ||
				dd.getFile().trim().equals("")){
			throw new ControllerListenerException("Asigna el archivo.");
		}
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