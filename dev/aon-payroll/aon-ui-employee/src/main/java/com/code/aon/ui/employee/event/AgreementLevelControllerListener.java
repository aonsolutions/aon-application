package com.code.aon.ui.employee.event;

import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AgreementLevelControllerListener extends ControllerAdapter{
	
	@Override
	public void afterModelSearched(ControllerEvent event)
			throws ControllerListenerException {
//		((BasicController)getController()).onSelectFirst(null);
	}
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
//		((BasicController)getController()).onSelectFirst(null);
	}

}
