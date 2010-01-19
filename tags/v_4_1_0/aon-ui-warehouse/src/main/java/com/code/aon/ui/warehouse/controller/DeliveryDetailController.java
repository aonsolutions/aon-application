package com.code.aon.ui.warehouse.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;

public class DeliveryDetailController extends LinesController {

	private boolean longDescription;

	private IPriceStrategy priceStrategy;
	
	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isSalesSource() throws ManagerBeanException {
		DeliveryDetail deliveryDetail = (DeliveryDetail)getTo();
		if (deliveryDetail != null) {
			return (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null);
		}
		return false;
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

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		DeliveryDetail deliveryDetail = (DeliveryDetail)this.getModel().getRowData();
		if (deliveryDetail.getSalesDetail() != null && deliveryDetail.getSalesDetail().getId() != null) {
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_source"));
			info.append(" ");
			info.append(AonUtil.getMessage("salesBundle", "sales_sales"));
			info.append(" ");
			info.append(deliveryDetail.getSalesDetail().getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("warehouseBundle", "warehouse_delivery_detail_line"));
			info.append(" ");
			info.append(deliveryDetail.getSalesDetail().getLine());
		}
		return info.toString();
	}

}