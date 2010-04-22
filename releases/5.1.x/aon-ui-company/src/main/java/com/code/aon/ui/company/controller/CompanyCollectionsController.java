package com.code.aon.ui.company.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.company.Company;
import com.code.aon.company.WorkPlace;
import com.code.aon.company.dao.ICompanyAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.RegistryAddress;
import com.code.aon.registry.RegistryBank;
import com.code.aon.registry.dao.IRegistryAlias;

public class CompanyCollectionsController {

    /**
     * Gets the addresses of the company.
     * 
     * @return the addresses of the company
     * @throws ManagerBeanException 
     */
	@SuppressWarnings("unchecked")
    public List<SelectItem> getCompanyAddresses() throws ManagerBeanException {
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS));
    		List<ITransferObject> list = registryAddressBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryAddress rAddress = (RegistryAddress)to;
    			addresses.add(new SelectItem(rAddress, rAddress.getAddress()));
    		}
    	}
    	return addresses;
    }

	@SuppressWarnings("unchecked")
    public List<SelectItem> getCompanyBanks() throws ManagerBeanException {
    	LinkedList<SelectItem> banks = new LinkedList<SelectItem>();
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	Iterator iterator = companyBean.getList(null).iterator();
    	if(iterator.hasNext()) {
    		Company company = (Company)iterator.next();
    		IManagerBean registryBankBean = BeanManager.getManagerBean(RegistryBank.class);
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryBankBean.getFieldName(IRegistryAlias.REGISTRY_BANK_ID));
    		List<ITransferObject> list = registryBankBean.getList(criteria);
    		for (ITransferObject to : list) {
    			RegistryBank rBank = (RegistryBank)to;
    			banks.add(new SelectItem(rBank, rBank.getFullName()));
    		}
    	}
    	return banks;
    }

	public WorkPlace getWorkPlace() {
		return null;
	}

	public void setWorkPlace( WorkPlace workPlace ) {
	}

	public List<SelectItem> getWorkPlaces() throws ManagerBeanException {
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(workplaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ID));
		List<ITransferObject> list = workplaceBean.getList(criteria);
		for (ITransferObject to : list) {
			WorkPlace workPlace = (WorkPlace)to;
			workPlaces.add(new SelectItem(workPlace, workPlace.getDescription()));
		}
		return workPlaces;
	}	

	public int getWorkPlacesCount() throws ManagerBeanException {
		return BeanManager.getManagerBean(WorkPlace.class).getCount(null);
	}

}