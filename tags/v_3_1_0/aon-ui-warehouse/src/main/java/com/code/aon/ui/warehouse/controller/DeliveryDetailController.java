package com.code.aon.ui.warehouse.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.product.Item;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.form.LinesController;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailController extends LinesController {

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onItemChanged(LookupChangeEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			deliveryDetail.setItem(item);
			deliveryDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			Date date = deliveryDetail.getDelivery().getIssueTime();
			DeliveryController master = (DeliveryController)getMasterController();
			Tariff tariff = ((Delivery)master.getTo()).getCustomer().getTariff();
			price = getPriceStrategy().getUnitPrice(deliveryDetail, date, tariff);
		}
		deliveryDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (deliveryDetail.getItem() != null && deliveryDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				deliveryDetail.setQuantity((Double)event.getNewValue());
	
				Date date = deliveryDetail.getDelivery().getIssueTime();
				DeliveryController master = (DeliveryController)getMasterController();
				Tariff tariff = ((Delivery)master.getTo()).getCustomer().getTariff();
				price = getPriceStrategy().getUnitPrice(deliveryDetail, date, tariff);
			}
			deliveryDetail.setPrice(price);
		}
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}























	
	private boolean importingOffer;
	
	public boolean isImportingOffer() {
		return importingOffer;
	}

	public void setImportingOffer(boolean importingOffer) {
		this.importingOffer = importingOffer;
	}

}