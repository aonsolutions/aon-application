package com.code.aon.ui.sales.controller;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.enumeration.SalesDetailStatus;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.dao.IWarehouseAlias;

public class SalesDetailController extends LinesController {

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

	public boolean isPending() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			if (salesDetail.getStatus() != null) {
				return salesDetail.getStatus().equals(SalesDetailStatus.PENDING);
			}
		}
		return false;
	}

	public boolean isSettled() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
			if (salesDetail.getStatus() != null) {
				return salesDetail.getStatus().equals(SalesDetailStatus.SETTLED);
			}
		}
		return false;
	}

	public boolean isOfferSource() throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)getTo();
		if (salesDetail != null) {
			return (salesDetail.getOfferDetail() != null && salesDetail.getOfferDetail().getId() != null);
		}
		return false;
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

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		if (salesDetail.getOfferDetail() != null && salesDetail.getOfferDetail().getId() != null) {
			info.append(AonUtil.getMessage("salesBundle", "sales_source"));
			info.append(" ");
			info.append(AonUtil.getMessage("commercialBundle", "commercial_offer"));
			info.append(" ");
			info.append(salesDetail.getOfferDetail().getOffer().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("salesBundle", "sales_detail_line"));
			info.append(" ");
			info.append(salesDetail.getOfferDetail().getLine());
		}
		return info.toString();
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage("bundle", "aon_decimal3_truncate_pattern"));

		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IWarehouseAlias.DELIVERY_DETAIL_SALES_DETAIL_ID), salesDetail.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			info.append("<p>");
			info.append(AonUtil.getMessage("salesBundle", "sales_transfered_to"));
			info.append(" ");
			info.append(AonUtil.getMessage("salesBundle", "sales_to_delivery"));
			info.append(" ");
			info.append(deliveryDetail.getDelivery().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("salesBundle", "sales_detail_line"));
			info.append(" ");
			info.append(deliveryDetail.getLine());
			if (salesDetail.getQuantity() > deliveryDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(deliveryDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage("salesBundle", "sales_detail_units"));
				info.append(")");
			}
			info.append("</p>");
		}
		return info.toString();
	}

}