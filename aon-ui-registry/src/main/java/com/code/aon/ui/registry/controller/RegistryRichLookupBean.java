package com.code.aon.ui.registry.controller;

import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.controller.RichLookupBean;
import com.code.aon.registry.IRegistry;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.DocumentType;
import com.code.aon.registry.enumeration.RegistryType;
import com.code.aon.ui.registry.controller.event.RegistryFormListener;
import com.code.aon.ui.util.AonUtil;

public class RegistryRichLookupBean extends RichLookupBean {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
    
	private final static Logger LOGGER = LoggerFactory.getLogger(RegistryRichLookupBean.class);
	
	public void onChangeRegistryType(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setDocumentType(iRegistry.getRegistry().getType() == RegistryType.LEGAL ? DocumentType.CIF : DocumentType.NIF);
	}
	
	public void onChangeDocument(ActionEvent event) {
		IRegistry iRegistry = (IRegistry) getTo();
		iRegistry.getRegistry().setType(iRegistry.getRegistry().getDocumentType() == DocumentType.CIF ? RegistryType.LEGAL : RegistryType.NATURAL);

		try {
			if (isNevv()) {
				RegistryController.validateDocument(iRegistry, getController().getManagerBean());
			}
		} catch (ManagerBeanException e) {
			LOGGER.warn("unable to check Document.",e);
		}
	}
	
	public void onLoadGeozone(ActionEvent event){ 
		RegistryFormListener registryForm = (RegistryFormListener) AonUtil.getRegisteredBean(this.getPojoShortName().toLowerCase()+"Form"); 
		RegistryAddress address = registryForm.getMainAddress();
		if(address!=null && address.getZip()!=null) {
			address.loadGeoZoneByZip();
		}
	}

}
