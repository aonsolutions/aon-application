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
import com.code.aon.registry.dao.IRegistryAlias;

public class CompanyCollectionsController {

    /**
     * Gets the addresses of the company.
     * 
     * @return the addresses of the company
     * @throws ManagerBeanException 
     */
    @SuppressWarnings("unchecked")
    public List<SelectItem> getCompanyAddresses() throws ManagerBeanException{
    	IManagerBean companyBean = BeanManager.getManagerBean(Company.class);
    	IManagerBean registryAddressBean = BeanManager.getManagerBean(RegistryAddress.class);
    	Iterator iter = companyBean.getList(null).iterator();
    	LinkedList<SelectItem> addresses = new LinkedList<SelectItem>();
    	if(iter.hasNext()){
    		Company company = (Company)iter.next();
    		Criteria criteria = new Criteria();
    		criteria.addEqualExpression(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_REGISTRY_ID), company.getId());
    		criteria.addOrder(registryAddressBean.getFieldName(IRegistryAlias.REGISTRY_ADDRESS_ADDRESS));
    		iter = registryAddressBean.getList(criteria).iterator();
    		while(iter.hasNext()){
    			RegistryAddress rAddress = (RegistryAddress)iter.next();
    			SelectItem item = new SelectItem(rAddress.getId(),rAddress.getAddress());
    			addresses.add(item);
    		}
    	}
    	return addresses;
    }

	public int getWorkPlacesCount() throws ManagerBeanException{
		IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
		return workplaceBean.getCount(null);
	}

	public List<SelectItem> getWorkPlaces() throws ManagerBeanException{
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		Iterator<ITransferObject> iter = obtainWorkplaces().iterator();
		while(iter.hasNext()){
			WorkPlace workPlace = (WorkPlace)iter.next();
			workPlaces.add( new SelectItem(workPlace.getId(), workPlace.getId().toString()));
		}
		return workPlaces;
	}


	public List<SelectItem> getWorkPlacesDetailed() throws ManagerBeanException{
		List<SelectItem> workPlaces = new LinkedList<SelectItem>();
		Iterator<ITransferObject> iter = obtainWorkplaces().iterator();
		while(iter.hasNext()){
			WorkPlace workPlace = (WorkPlace)iter.next();
			workPlaces.add( new SelectItem(workPlace.getId(), workPlace.getDescription()));
		}
		return workPlaces;
	}
	
	private List<ITransferObject> obtainWorkplaces() throws ManagerBeanException{
		IManagerBean workplaceBean = BeanManager.getManagerBean(WorkPlace.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(workplaceBean.getFieldName(ICompanyAlias.WORK_PLACE_ID));
		return workplaceBean.getList(criteria);
	}

}