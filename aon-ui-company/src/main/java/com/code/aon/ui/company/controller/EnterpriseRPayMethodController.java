package com.code.aon.ui.company.controller;

import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Enterprise;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;

public class EnterpriseRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getAllBanks() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getMasterController().getTo();
		return getAllBanks(enterprise.getRegistry());
	}
	public List<SelectItem> getActiveBanks() throws ManagerBeanException {
		Enterprise enterprise = (Enterprise) getMasterController().getTo();
		return getActiveBanks(enterprise.getRegistry());
	}

}
