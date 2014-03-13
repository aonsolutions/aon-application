package com.code.aon.ui.customer.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.customer.Customer;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;

public class CustomerRPayMethodController extends RegistryPayMethodController{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Customer customer = (Customer) getMasterController().getTo();
			return getAllBanks(customer.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getAllCompanyBanks();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Customer customer = (Customer) getMasterController().getTo();
			return getActiveBanks(customer.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}
}
