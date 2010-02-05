package com.code.aon.ebackoffice.event;

import org.apache.commons.validator.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ebackoffice.Ectarget;
import com.code.aon.ebackoffice.util.EmailUtils;

public class EctargetBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	private static final Logger LOGGER = LoggerFactory
			.getLogger(EctargetBeanVetoListener.class.getName());

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {
		
		if(!EmailUtils.validateEmailAddress(((Ectarget)evt.getTo()).getLogin())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}
		
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt)
			throws ManagerBeanVetoListenerException {

		if(!EmailUtils.validateEmailAddress(((Ectarget)evt.getTo()).getLogin())){
    		throw new ManagerBeanVetoListenerException(
			"El email es incorrecto.");
		}
		
	}

}
