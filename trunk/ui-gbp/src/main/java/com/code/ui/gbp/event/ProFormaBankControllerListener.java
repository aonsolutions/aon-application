package com.code.ui.gbp.event;

import java.util.logging.Logger;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.gbp.dao.IGBPAlias;
import com.code.ui.gbp.controller.ProFormaBankController;

public class ProFormaBankControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(ProFormaBankControllerListener.class.getName());
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		try {
			event.getController().getCriteria().addOrder(event.getController().getFieldName(IGBPAlias.PRO_FORMA_BANK_PERCENT), false);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event)
			throws ControllerListenerException {
		double sum = ((ProFormaBankController)event.getController()).getSumOfPercents();
        if (sum>100){
        	throw new ControllerListenerException("Percent sumatory can not be more than 100.");
        }
	}
	
	@Override
	public void beforeBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		double sum = ((ProFormaBankController)event.getController()).getSumOfPercents();
        if (sum>100){
        	throw new ControllerListenerException("Percent sumatory can not be more than 100.");
        }
	}

}