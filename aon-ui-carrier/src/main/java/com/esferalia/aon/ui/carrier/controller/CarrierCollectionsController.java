package com.esferalia.aon.ui.carrier.controller;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.carrier.Carrier;
import com.esferalia.aon.carrier.enumeration.ShipmentPeriod;
import com.esferalia.aon.carrier.enumeration.ShipmentStatus;

public class CarrierCollectionsController {
	
	private List<SelectItem> shipmentPeriods;
	private List<SelectItem> shipmentStatuses;

	public List<SelectItem> getShipmentPeriods() {
		if ( shipmentPeriods == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			shipmentPeriods = new LinkedList<SelectItem>();
			for (ShipmentPeriod p : ShipmentPeriod.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				shipmentPeriods.add(item);
			}
		}
		return shipmentPeriods;
	}
	
	public List<SelectItem> getShipmentStatuses() {
		if ( shipmentStatuses == null ) {
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			shipmentStatuses = new LinkedList<SelectItem>();
			for (ShipmentStatus p : ShipmentStatus.values()) {
				String name = p.getName(locale);
				SelectItem item = new SelectItem(p, name);
				shipmentStatuses.add(item);
			}
		}
		return shipmentStatuses;
	}
	
	public List<SelectItem> getCarriers() {
		List<SelectItem> carriers = new LinkedList<SelectItem>();
		try {
			Iterator<?> iter = getCarriersList().iterator();
			while(iter.hasNext()){
				Carrier carrier = (Carrier)iter.next();
				String name = carrier.getRegistry().getFullName();
				SelectItem item = new SelectItem(carrier, name);
				carriers.add(item);
			}
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener las agencias de transporte";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		}
		return carriers;
	}
	
	public Integer getCarriersCount() {
		try {
			return getCarriersList().size();
		} catch (ManagerBeanException e) {
			String msg = "No se han podido obtener las agencias de transporte";
			AonUtil.addErrorMessage(msg);
			AonUtil.addErrorMessage(e.toString());
		}
		return 0;
	}
	
	public List<ITransferObject> getCarriersList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(Carrier.class);
		return bean.getList(null);
	}

}