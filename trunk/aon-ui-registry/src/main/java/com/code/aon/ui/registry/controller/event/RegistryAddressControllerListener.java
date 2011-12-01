package com.code.aon.ui.registry.controller.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.util.CompanyUtil;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.IRegistryConstants;

public class RegistryAddressControllerListener extends ControllerAdapter implements IRegistryConstants {

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		try {
			RegistryAddress to = (RegistryAddress)event.getController().getTo();
			to.setStreetType(StreetType.CL);
			to.setGeozone(CompanyUtil.getCompanyGeoZone());
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress to = (RegistryAddress)event.getController().getTo();
		if (to.getStreetType() == null) {
			to.setStreetType(StreetType.CL);
		}
	}

}