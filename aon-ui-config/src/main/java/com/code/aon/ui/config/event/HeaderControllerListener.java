package com.code.aon.ui.config.event;

import com.code.aon.ui.config.controller.HeaderObjectController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * Listener added to the controller of any entity with series and number
 * 
 */
public class HeaderControllerListener extends ControllerAdapter {
	
	/**
	 * Obtains the series and the next number to use
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		HeaderObjectController controller = (HeaderObjectController) event.getController();
		controller.updateSeriesNumber();
    }

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		HeaderObjectController controller = (HeaderObjectController) event.getController();
		controller.updateSeriesNumber();
	}	
	
}
