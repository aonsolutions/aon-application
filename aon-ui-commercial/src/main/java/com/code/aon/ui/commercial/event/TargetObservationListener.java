package com.code.aon.ui.commercial.event;

import static com.code.aon.ui.commercial.controller.ICommercialConstants.TARGET_OBSERVATION_CONTROLLER_NAME;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Target;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryObservationController;
import com.code.aon.ui.util.AonUtil;

public class TargetObservationListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(TARGET_OBSERVATION_CONTROLLER_NAME);
			Target target = ((Target)event.getController().getTo());
			rObservationController.onRecover(target.getRegistry());
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(TARGET_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onSave();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(TARGET_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onSave();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(TARGET_OBSERVATION_CONTROLLER_NAME);
			Target target = ((Target)event.getController().getTo());
			rObservationController.onRecover(target.getRegistry());
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void afterBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try{
			RegistryObservationController rObservationController = (RegistryObservationController)AonUtil.getRegisteredBean(TARGET_OBSERVATION_CONTROLLER_NAME);
			rObservationController.onRemove();
		}catch (Exception e) {
			throw new ControllerListenerException(e);
		}
	}
	
}