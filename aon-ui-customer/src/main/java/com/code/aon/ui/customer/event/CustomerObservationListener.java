package com.code.aon.ui.customer.event;

import static com.code.aon.ui.customer.controller.ICustomerConstants.CUSTOMER_OBSERVATION_CONTROLLER_NAME;

import com.code.aon.common.AonVersion;
import com.code.aon.customer.Customer;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.util.AonUtil;

public class CustomerObservationListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(CUSTOMER_OBSERVATION_CONTROLLER_NAME);
			Customer customer = ((Customer)event.getController().getTo());
			rObservationController.onRecover(customer.getRegistry());
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(CUSTOMER_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onSave();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(CUSTOMER_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onSave();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(CUSTOMER_OBSERVATION_CONTROLLER_NAME);
			Customer customer = ((Customer)event.getController().getTo());
			rObservationController.onRecover(customer.getRegistry());
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(CUSTOMER_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onRemove();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}
	
}