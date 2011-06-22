package com.code.aon.ui.company.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;

public class CompanyRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Company company = (Company) getMasterController().getTo();
		return getAllBanks(company);
	}
	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Company company = (Company) getMasterController().getTo();
		return getActiveBanks(company);
	}

}
