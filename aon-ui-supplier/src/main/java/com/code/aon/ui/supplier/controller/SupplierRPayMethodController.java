package com.code.aon.ui.supplier.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;

public class SupplierRPayMethodController extends RegistryPayMethodController{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		if (isBankTransfer()) {
			Supplier supplier = (Supplier) getMasterController().getTo();
			return getAllBanks(supplier.getRegistry());
		} 
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getAllCompanyBanks();
	}

	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		if (isBankTransfer()) {
			Supplier supplier = (Supplier) getMasterController().getTo();
			return getActiveBanks(supplier.getRegistry());
		} 
		CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
		return c.getActiveCompanyBanks();
	}
}
