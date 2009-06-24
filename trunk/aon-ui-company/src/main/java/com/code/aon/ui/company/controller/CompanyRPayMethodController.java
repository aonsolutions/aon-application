package com.code.aon.ui.company.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;

public class CompanyRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getBanks() throws ManagerBeanException {
		Company company = (Company) getMasterController().getTo();
		return getBanks(company.getId());
	}

}
