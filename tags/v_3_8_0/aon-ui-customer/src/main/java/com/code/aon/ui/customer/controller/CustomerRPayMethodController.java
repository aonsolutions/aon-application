package com.code.aon.ui.customer.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.customer.Customer;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;

public class CustomerRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getBanks() throws ManagerBeanException {
		Integer id = null;
		if (isBankTransfer()) {
			id = obtainCompany().getId();
		} else {
			Customer customer = (Customer) getMasterController().getTo();
			id = customer.getId();
		}
		return getBanks( id );
	}
	
	@SuppressWarnings("unchecked")
	private Company obtainCompany() throws ManagerBeanException {
		IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
		Iterator iter = companyBean.getList(null).iterator();
		if(iter.hasNext()){
			return (Company)iter.next();
		}
		return null;
	}

}
