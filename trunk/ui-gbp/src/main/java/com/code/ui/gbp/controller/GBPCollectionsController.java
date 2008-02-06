package com.code.ui.gbp.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.gbp.GeoZone;
import com.code.gbp.IncidenceType;
import com.code.gbp.Office;
import com.code.gbp.enumeration.AddInfoType;
import com.code.gbp.enumeration.CampaignStatus;
import com.code.gbp.enumeration.ContactType;
import com.code.gbp.enumeration.CostType;
import com.code.gbp.enumeration.DocumentType;
import com.code.gbp.enumeration.OfferStatus;
import com.code.gbp.enumeration.ProFormaInvoiceStatus;

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
	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getOffices() throws ManagerBeanException{
		List<SelectItem> offices = new LinkedList<SelectItem>();
		IManagerBean officeBean = BeanManager.getManagerBean(Office.class);
		Iterator iter = officeBean.getList(null).iterator();
		while(iter.hasNext()){
			Office office = (Office)iter.next();
			SelectItem item = new SelectItem(office.getId(), office.getName());
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
}