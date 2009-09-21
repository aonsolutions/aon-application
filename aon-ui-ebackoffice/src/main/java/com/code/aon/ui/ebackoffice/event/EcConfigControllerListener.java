package com.code.aon.ui.ebackoffice.event;

import com.code.aon.ebackoffice.Ecconfig;
import com.code.aon.ui.common.io.AonFile;
import com.code.aon.ui.ebackoffice.controller.EcconfigController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.product.controller.IItemConstants;

public class EcConfigControllerListener extends ControllerAdapter implements
		IItemConstants {
	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		AonFile aonFile1 = controller.getAonFile();		
		((Ecconfig) controller.getTo()).setHeaderImg(aonFile1.getData());
	}

	
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event
				.getController();
		AonFile aonFile1 = controller.getAonFile();
		if (aonFile1 != null) {
			((Ecconfig) controller.getTo()).setHeaderImg(aonFile1.getData());
			
		}	
	}
	
		
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		EcconfigController controller = (EcconfigController) event.getController();
		Ecconfig config = (Ecconfig) getController().getTo();
		AonFile aonFile = new AonFile();
		aonFile.setData( config.getHeaderImg() );
		controller.setAonFile( aonFile );
	}

}