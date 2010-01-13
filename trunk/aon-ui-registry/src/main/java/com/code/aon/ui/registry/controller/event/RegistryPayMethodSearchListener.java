package com.code.aon.ui.registry.controller.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.form.event.IControllerListener;
import com.code.aon.ui.util.AonUtil;

public class RegistryPayMethodSearchListener extends RegistrySearchListener {

	private static final String RPAY_METHOD_SUFFIX = "RPayMethod";

	private static final String RBANK_SUFFIX = "Rbank";

	private static final Logger LOGGER = LoggerFactory.getLogger(RegistryPayMethodSearchListener.class);
	
	private LinesController registryPayMethod;
	
	private LinesController registryBank;
	
	@Override
	protected void initControllers( String name ) {
		super.initControllers(name);
		this.registryPayMethod = (LinesController) AonUtil.getRegisteredBean(name + RPAY_METHOD_SUFFIX);
		if ( registryPayMethod == null ) {
			LOGGER.error( "Registry Pay Method Managed Bean not found for {}", name );
		}			
		this.registryBank = (LinesController) AonUtil.getRegisteredBean(name + RBANK_SUFFIX);
		if ( registryBank == null ) {
			LOGGER.error( "Registry Bank Managed Bean not found for {}", name );
		}					
	}

	@Override
	protected void updateListeners( IControllerListener controllerListener ) throws ControllerListenerException {
		super.updateListeners(controllerListener);
		RegistryPayMethodLookupListener listener = (RegistryPayMethodLookupListener) controllerListener;
		try {
			if ( registryBank != null ) {
				listener.updateRegistryBank(registryBank);
			}
			if ( registryPayMethod != null ) {
				listener.updateRegistryPayMethod(registryPayMethod);	
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException( e.getMessage(), e );
		}
	}
	
}