package com.code.aon.ui.supplier.controller;

import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.supplier.Supplier;
import com.code.aon.ui.registry.controller.RegistryPayMethodController;

public class SupplierRPayMethodController extends RegistryPayMethodController{
	
	public List<SelectItem> getBanks() throws ManagerBeanException {
		Integer id = null;
		if (isBankTransfer()) {
			Supplier supplier = (Supplier) getMasterController().getTo();
			id = supplier.getId();
		} else {
			id = obtainCompany().getId();
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
