package com.code.aon.ui.registry.controller.event;

import java.lang.reflect.InvocationTargetException;

import org.apache.commons.beanutils.PropertyUtils;

import com.code.aon.common.ITransferObject;
import com.code.aon.registry.Registry;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

/**
 * Listener added to the detail controllers of Registry.
 */
public class RegistryChildControllerListener extends ControllerAdapter {
	
	/** COMPANY_CONTROLLER_NAME. */
	private static final String COMPANY_CONTROLLER_NAME = "company";

	/**
	 * Gets the master controller.
	 * 
	 * @return the master controller
	 */
	public IController getMasterController() {
		return AonUtil.getController(COMPANY_CONTROLLER_NAME);
	}

	/**
	 * Updates master controller current object with the detail controller info
	 * 
	 * @param event the event
	 * 
	 * @throws ControllerListenerException the controller listener exception
	 */
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		try {
			BasicController detail = (BasicController) event.getController();
			ITransferObject child = detail.getTo();
			if ( child != null ) {
				IController master = getMasterController();
				Registry registry = (Registry) master.getTo();
				PropertyUtils.setProperty(child,"registry", registry);
			}
		} catch (IllegalAccessException e) {
			throw new ControllerListenerException(e.getMessage(),e); 
		} catch (InvocationTargetException e) {
			throw new ControllerListenerException(e.getMessage(),e); 
		} catch (NoSuchMethodException e) {
			throw new ControllerListenerException(e.getMessage(),e); 
		}
	}
}
