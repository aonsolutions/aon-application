package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.IRegistryConstants;

public class RegistryAddressControllerListener extends ControllerAdapter implements IRegistryConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			RegistryAddress to = (RegistryAddress)event.getController().getTo();
			to.setGeozone(CompanyUtil.getCompanyGeoZone());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

}