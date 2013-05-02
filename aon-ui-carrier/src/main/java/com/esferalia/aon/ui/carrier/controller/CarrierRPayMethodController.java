package com.esferalia.aon.ui.carrier.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;

public class CarrierRPayMethodController extends RegistryPayMethodController {

	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Carrier customer = (Carrier) getMasterController().getTo();
			return getAllBanks(customer.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getAllCompanyBanks();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		if (isNegotiableDocument()) {
			Carrier customer = (Carrier) getMasterController().getTo();
			return getActiveBanks(customer.getRegistry());
		}
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}
	
}
