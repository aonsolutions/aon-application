package com.code.aon.ui.company.event;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.enumeration.AddressType;
import com.code.aon.registry.enumeration.StreetType;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.company.controller.RegistryInfo;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.registry.controller.RegistryAddressController;
import com.code.aon.ui.util.AonUtil;

public class CompanyAddressListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress) event.getController().getTo();
		rAddress.setStreetType(StreetType.CL);
	}	

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		RegistryAddress rAddress = (RegistryAddress) event.getController().getTo();
		if(rAddress.getAddressType() == null){
			rAddress.setAddressType(AddressType.DELEGATION);
		}
	}	
	
	@Override
	public void afterBeanUpdated(ControllerEvent event)
			throws ControllerListenerException {
		RegistryAddressController address = (RegistryAddressController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_ADDRESS_CONTROLLER_NAME);
		address.initializeModel();
		CompanyController company = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		try {
			company.setMainAddress(RegistryInfo.getMainAddress((Company) company.getTo()));
		} catch (ManagerBeanException e) {
			// nada
		}
	}

}