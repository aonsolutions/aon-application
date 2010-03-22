package com.code.ui.gbp.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.model.SelectItemGroup;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.gbp.AccountContact;
import com.code.gbp.Area;
import com.code.gbp.AreaGroup;
import com.code.gbp.Bank;
import com.code.gbp.BatchConfig;
import com.code.gbp.GeoZone;
import com.code.gbp.IncidenceType;
import com.code.gbp.InternalCustomer;
import com.code.gbp.Office;
import com.code.gbp.SupplierType;
import com.code.gbp.dao.IGBPAlias;
import com.code.gbp.enumeration.AddInfoType;
import com.code.gbp.enumeration.AreaStatus;
import com.code.gbp.enumeration.BatchConfigStatus;
import com.code.gbp.enumeration.CampaignStatus;
import com.code.gbp.enumeration.ContactType;
import com.code.gbp.enumeration.CostType;
import com.code.gbp.enumeration.DocumentType;
import com.code.gbp.enumeration.OfferStatus;
import com.code.gbp.enumeration.OfferType;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;
import com.code.gbp.enumeration.SupplierStatus;

public class GBPCollectionsController {

	@SuppressWarnings("unchecked")
	public List<SelectItem> getGeozones() throws ManagerBeanException{
		List<SelectItem> geoZones = new LinkedList<SelectItem>();
		IManagerBean geozoneBean = BeanManager.getManagerBean(GeoZone.class);
		Iterator iter = geozoneBean.getList(null).iterator();
		while(iter.hasNext()){
			GeoZone geoZone = (GeoZone)iter.next();
			SelectItem item = new SelectItem(geoZone.getId(), geoZone.getName());
			geoZones.add(item);
		}
		return geoZones;
	}
	
	public List<SelectItem> getContactTypes(){
		List<SelectItem> contactTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(ContactType type:ContactType.values()){
			SelectItem item = new SelectItem(type,type.getName(locale));
			contactTypes.add(item);
		}
		return contactTypes;
	}

	public List<SelectItem> getAddInfoTypes(){
		List<SelectItem> addInfoTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(AddInfoType type:AddInfoType.values()){
			SelectItem item = new SelectItem(type,type.getName(locale));
			addInfoTypes.add(item);
		}
		return addInfoTypes;
	}
	
	public List<SelectItem> getCampaignStatus(){
		List<SelectItem> campaignStatusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(CampaignStatus status:CampaignStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			campaignStatusList.add(item);
		}
		return campaignStatusList;
	}
	
	public List<SelectItem> getCostTypes(){
		List<SelectItem> costTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(CostType type:CostType.values()){
			SelectItem item = new SelectItem(type, type.getName(locale));
			costTypes.add(item);
		}
		return costTypes;
	}
	
	public List<SelectItem> getOfferStatus(){
		List<SelectItem> offerStatusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(OfferStatus status:OfferStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			offerStatusList.add(item);
		}
		return offerStatusList;
	}

	public List<SelectItem> getOfferTypes(){
		List<SelectItem> offerTypeList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(OfferType type:OfferType.values()){
			SelectItem item = new SelectItem(type, type.getName(locale));
			offerTypeList.add(item);
		}
		return offerTypeList;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getOffices() throws ManagerBeanException{
		List<SelectItem> offices = new LinkedList<SelectItem>();
		IManagerBean officeBean = BeanManager.getManagerBean(Office.class);
		Iterator iter = officeBean.getList(null).iterator();
		while(iter.hasNext()){
			Office office = (Office)iter.next();
			SelectItem item = new SelectItem(office.getId(), office.getCode() + " / " +office.getName());
			offices.add(item);
		}
		return offices;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getIncidenceTypes() throws ManagerBeanException{
		List<SelectItem> incidenceTypes = new LinkedList<SelectItem>();
		IManagerBean incidenceTypeBean = BeanManager.getManagerBean(IncidenceType.class);
		Iterator iter = incidenceTypeBean.getList(null).iterator();
		while(iter.hasNext()){
			IncidenceType incidenceType = (IncidenceType)iter.next();
			SelectItem item = new SelectItem(incidenceType.getId(), incidenceType.getDescription());
			incidenceTypes.add(item);
		}
		return incidenceTypes;
	}

	public List<SelectItem> getProformaInvoiceStatus(){
		List<SelectItem> proformaInvoiceStatusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(ProFormaInvoiceStatus status:ProFormaInvoiceStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			proformaInvoiceStatusList.add(item);
		}
		return proformaInvoiceStatusList;
	}
	
	public List<SelectItem> getDocumentTypes(){
		List<SelectItem> documentTypes = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(DocumentType type:DocumentType.values()){
			SelectItem item = new SelectItem(type, type.getName(locale));
			documentTypes.add(item);
		}
		return documentTypes;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getInternalCustomers() throws ManagerBeanException{
		List<SelectItem> customers = new LinkedList<SelectItem>();
		IManagerBean internalCustomerBean = BeanManager.getManagerBean(InternalCustomer.class);
		Iterator iter = internalCustomerBean.getList(null).iterator();
		while(iter.hasNext()){
			InternalCustomer internalCustomer = (InternalCustomer)iter.next();
			SelectItem item = new SelectItem(internalCustomer.getId(), internalCustomer.getDescription());
			customers.add(item);
		}
		return customers;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAccountContacts() throws ManagerBeanException {
		List<SelectItem> contacts = new LinkedList<SelectItem>();
		IManagerBean accountContactBean = BeanManager.getManagerBean(AccountContact.class);
		Iterator iter = accountContactBean.getList(null).iterator();
		while(iter.hasNext()){
			AccountContact contact = (AccountContact)iter.next();
			SelectItem item = new SelectItem(contact.getId(), contact.getDescription());
			contacts.add(item);
		}
		return contacts;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getSupplierTypes() throws ManagerBeanException{
		List<SelectItem> supplierTypes = new LinkedList<SelectItem>();
		IManagerBean supplierTypeBean = BeanManager.getManagerBean(SupplierType.class);
		Iterator iter = supplierTypeBean.getList(null).iterator();
		while(iter.hasNext()){
			SupplierType supplierType = (SupplierType)iter.next();
			SelectItem item = new SelectItem(supplierType.getId(), supplierType.getDescription());
			supplierTypes.add(item);
		}
		return supplierTypes;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getBanks() throws ManagerBeanException {
		List<SelectItem> banks = new LinkedList<SelectItem>();
		IManagerBean bankBean = BeanManager.getManagerBean(Bank.class);
		Iterator iter = bankBean.getList(null).iterator();
		while(iter.hasNext()){
			Bank bank = (Bank)iter.next();
			SelectItem item = new SelectItem(bank.getId(), bank.getName());
			banks.add(item);
		}
		return banks;
	}
	
	public List<SelectItem> getBatchConfigStatus(){
		List<SelectItem> statusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(BatchConfigStatus status:BatchConfigStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			statusList.add(item);
		}
		return statusList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getBatchConfigs() throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(BatchConfig.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IGBPAlias.BATCH_CONFIG_STATUS), BatchConfigStatus.ACTIVE);
		Iterator iter = bean.getList(criteria).iterator();
		while(iter.hasNext()){
			BatchConfig object = (BatchConfig)iter.next();
			SelectItem item = new SelectItem(object.getId(), object.getDescription());
			items.add(item);
		}
		return items;
	}
	
	public List<SelectItem> getAreaStatus(){
		List<SelectItem> statusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(AreaStatus status:AreaStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			statusList.add(item);
		}
		return statusList;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAreaGroups() throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(AreaGroup.class);
		Iterator iter = bean.getList(null).iterator();
		while(iter.hasNext()){
			AreaGroup object = (AreaGroup)iter.next();
			SelectItem item = new SelectItem(object.getId(), object.getDescription());
			items.add(item);
		}
		return items;
	}
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getAreas() throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(Area.class);
		Iterator iter = bean.getList(null).iterator();
		while(iter.hasNext()){
			Area object = (Area)iter.next();
			SelectItem item = new SelectItem(object.getId(), object.getDescription());
			items.add(item);
		}
		return items;
	}

	@SuppressWarnings("unchecked")
	public List<SelectItem> getAreasByGroup() throws ManagerBeanException {
		List<SelectItem> items = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(AreaGroup.class);
		Criteria criteria = new Criteria();
		criteria.addOrder(bean.getFieldName(IGBPAlias.AREA_GROUP_DESCRIPTION));
		Iterator iter = bean.getList(criteria).iterator();
		while(iter.hasNext()){
			AreaGroup object = (AreaGroup)iter.next();
			IManagerBean beanSub = BeanManager.getManagerBean(Area.class);
			Criteria criteriaSub = new Criteria();
			criteriaSub.addEqualExpression(beanSub.getFieldName(IGBPAlias.AREA_AREA_GROUP_ID), object.getId());
			criteriaSub.addOrder(beanSub.getFieldName(IGBPAlias.AREA_DESCRIPTION));
			List listSub = beanSub.getList(criteriaSub);
			SelectItem[] subList = new SelectItem[listSub.size()];
			for (int j = 0; j < listSub.size(); j++) {
				SelectItem subItem;
				Area area = (Area)listSub.get(j);
				subItem = new SelectItem(area.getId(),area.getDescription());
				subList[j] = subItem; 
			}
			SelectItem item = new SelectItemGroup(object.getDescription(),object.getDescription(),true,subList);
			items.add(item);
		}
		return items;
	}

	public List<SelectItem> getSupplierStatus(){
		List<SelectItem> statusList = new LinkedList<SelectItem>();
		Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
		for(SupplierStatus status:SupplierStatus.values()){
			SelectItem item = new SelectItem(status, status.getName(locale));
			statusList.add(item);
		}
		return statusList;
	}
	

}