package com.esferalia.aon.ui.pms.controller;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.pms.ProjectReservationService;

public class ProjectReservationServiceController extends LinesController {

	public void onItemChanged(LookupChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		double price = 0;
		double taxableBase = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationService.setItem(item);
			reservationService.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (reservationService.getQuantity() == 0) {
				reservationService.setQuantity(1);
			}
			price = item.getPrice();
			taxableBase = CommonUtil.round(reservationService.getQuantity() * price);
		}
		reservationService.setPrice(price);
		reservationService.setTaxableBase(taxableBase);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		double taxableBase = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Double quantity = (Double)event.getNewValue();
			taxableBase = CommonUtil.round(quantity * reservationService.getPrice());
		}
		reservationService.setTaxableBase(taxableBase);
	}	

	public void onPriceChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		double taxableBase = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Double price = (Double)event.getNewValue();
			taxableBase = CommonUtil.round(reservationService.getQuantity() * price);
		}
		reservationService.setTaxableBase(taxableBase);
	}	

}