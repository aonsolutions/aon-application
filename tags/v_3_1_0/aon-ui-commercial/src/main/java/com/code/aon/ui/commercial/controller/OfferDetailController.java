package com.code.aon.ui.commercial.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.product.Item;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.form.LinesController;

public class OfferDetailController extends LinesController {

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onItemChanged(LookupChangeEvent event) {
		OfferDetail offerDetail = (OfferDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			offerDetail.setItem(item);
			offerDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			Date date = offerDetail.getOffer().getIssueDate();
			OfferController master = (OfferController)getMasterController();
			Tariff tariff = ((Offer)master.getTo()).getTariff();
			price = getPriceStrategy().getUnitPrice(offerDetail, date, tariff);
		}
		offerDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		OfferDetail offerDetail = (OfferDetail)getTo();
		if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				offerDetail.setQuantity((Double)event.getNewValue());
	
				Date date = offerDetail.getOffer().getIssueDate();
				OfferController master = (OfferController)getMasterController();
				Tariff tariff = ((Offer)master.getTo()).getTariff();
				price = getPriceStrategy().getUnitPrice(offerDetail, date, tariff);
			}
			offerDetail.setPrice(price);
		}
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

}