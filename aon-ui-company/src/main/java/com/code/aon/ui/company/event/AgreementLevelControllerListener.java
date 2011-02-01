package com.code.aon.ui.company.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AgreementLevelControllerListener extends ControllerAdapter{
	
	@Override
	public void afterModelInitialized(ControllerEvent event)
			throws ControllerListenerException {
		LinesController controller = (LinesController)getController();
		try {
			if(controller.getModel().getRowCount()>0){
				controller.onSelectFirst(null);
			} else {
				controller.onReset(null);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException("error on afterModelInitialized");
		}
	}

}
