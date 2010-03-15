package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.registry.IRegistry;

public class RegistryRichLookupBean extends RichLookupBean {
    
	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryRichLookupBean.class);
	
	public void onChangeDocument(ActionEvent event) {
		try {
			if (isNew()) {
				IRegistry iRegistry = (IRegistry) getTo();
				RegistryController.validateDocument(iRegistry,getController().getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}

}
