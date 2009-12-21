package com.code.aon.ui.audit.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class DomainController extends BasicController {

	public void tabChanged( ValueChangeEvent event ) {
		Object controllerName = event.getOldValue();
		if ( controllerName != null ) {
			IController controller = FormUtil.getController((String) controllerName);
			if ( controller != null ) {
				controller.onCancel(null);	
			}
		}
	}
	
}
