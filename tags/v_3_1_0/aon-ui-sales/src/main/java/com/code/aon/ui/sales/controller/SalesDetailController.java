package com.code.aon.ui.sales.controller;

import java.util.Date;

import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.faces.component.richfaces.lookup.LookupChangeEvent;
import com.code.aon.product.Item;
import com.code.aon.product.Tariff;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.form.LinesController;

public class SalesDetailController extends LinesController {

	private IPriceStrategy priceStrategy;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onItemChanged(LookupChangeEvent event) {
		SalesDetail salesDetail = (SalesDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			salesDetail.setItem(item);
			salesDetail.setDescription(item.getProduct().getName() + " " + (item.getDetail()!=null?item.getDetail():""));

			Date date = salesDetail.getSales().getIssueDate();
			SalesController master = (SalesController)getMasterController();
			Tariff tariff = ((Sales)master.getTo()).getCustomer().getTariff();
			price = getPriceStrategy().getUnitPrice(salesDetail, date, tariff);
		}
		salesDetail.setPrice(price);
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		SalesDetail salesDetail = (SalesDetail)getTo();
		if (salesDetail.getItem() != null && salesDetail.getItem().getId() != null) {
			double price = 0;
			if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
				salesDetail.setQuantity((Double)event.getNewValue());
	
				Date date = salesDetail.getSales().getIssueDate();
				SalesController master = (SalesController)getMasterController();
				Tariff tariff = ((Sales)master.getTo()).getCustomer().getTariff();
				price = getPriceStrategy().getUnitPrice(salesDetail, date, tariff);
			}
			salesDetail.setPrice(price);
		}
	}

	public double getAmount() {
		return getPriceStrategy().getBasePrice((ICalculable)this.getTo());
	}

	public double getModelAmount() throws ManagerBeanException {
		return getPriceStrategy().getBasePrice((ICalculable)this.getModel().getRowData());
	}

}