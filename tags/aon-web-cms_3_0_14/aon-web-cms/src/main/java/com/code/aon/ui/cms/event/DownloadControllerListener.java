package com.code.aon.ui.cms.event;

import com.code.aon.cms.DownloadDetail;
import com.code.aon.cms.dao.ICMSAlias;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.cms.controller.DownloadController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class DownloadControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		DownloadController controller = (DownloadController)event.getController(); 
		try {
			controller.completeCriteria();
			controller.getCriteria().addOrder(controller.getFieldName(ICMSAlias.DOWNLOAD_ALIAS));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

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