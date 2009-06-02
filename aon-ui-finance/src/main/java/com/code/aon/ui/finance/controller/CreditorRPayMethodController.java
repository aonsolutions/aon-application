package com.code.aon.ui.finance.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.finance.Creditor;
import com.code.aon.ui.company.controller.CompanyController;
import com.code.aon.ui.company.controller.ICompanyConstants;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;
import com.code.aon.ui.util.AonUtil;

public class CreditorRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getBanks() throws ManagerBeanException {
		Integer id = null;
		if (isBankTransfer()) {
			Creditor creditor = (Creditor) getMasterController().getTo();
			id = creditor.getId();
		} else {
			id = obtainCompany().getId();
		}
		return getBanks( id );
	}
	
	@SuppressWarnings("unchecked")
	private Company obtainCompany() throws ManagerBeanException {
		CompanyController companyController = (CompanyController) AonUtil.getRegisteredBean(ICompanyConstants.COMPANY_CONTROLLER_NAME);
		return companyController.obtainCompany();
	}

}
