package com.esferalia.aon.ui.pms.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationService;

public class ProjectReservationServiceController extends LinesController {

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	/*public void onItemChanged(LookupChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			reservationService.setItem(item);
			reservationService.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));
			if (reservationService.getQuantity() == 0) {
				reservationService.setQuantity(1);
			}
			Date date = (reservationService.getEffectiveDate() != null) ? reservationService.getEffectiveDate() : new Date();
			ProjectReservationController master = (ProjectReservationController)getMasterController();
			Tariff tariff = ((ProjectReservation)master.getTo()).getTariff();
			reservationService.setPrice(getPriceStrategy().getUnitPrice(reservationService, date, tariff));
			reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (reservationService.getItem() != null && reservationService.getItem().getId() != null) {
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				reservationService.setQuantity((Double)event.getNewValue());
	
				Date date = (reservationService.getEffectiveDate() != null) ? reservationService.getEffectiveDate() : new Date();
				ProjectReservationController master = (ProjectReservationController)getMasterController();
				Tariff tariff = ((ProjectReservation)master.getTo()).getTariff();
				reservationService.setPrice(getPriceStrategy().getUnitPrice(reservationService, date, tariff));
			} else {
				reservationService.setQuantity(1);
			}
		}
		reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
	}	

	public void onPriceChanged(ValueChangeEvent event) {
		ProjectReservationService reservationService = (ProjectReservationService)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservationService.setPrice((Double)event.getNewValue());
		} else {
			reservationService.setPrice(0);
		}
		reservationService.setTaxableBase(getPriceStrategy().getBasePrice(reservationService));
	}*/	

}