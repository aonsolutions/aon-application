package com.code.aon.ui.finance.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.company.controller.CompanyCollectionsController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;

public class CreditorRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getBanks() throws ManagerBeanException {
		if (isBankTransfer()) {
			Creditor creditor = (Creditor) getMasterController().getTo();
			return getBanks(creditor.getRegistry());
		} else {
			CompanyCollectionsController c = (CompanyCollectionsController)AonUtil.getRegisteredBean(ICompanyConstants.COLLECTIONS_CONTROLLER_NAME);
			return c.getCompanyBanks();
		}
	}
	
}
