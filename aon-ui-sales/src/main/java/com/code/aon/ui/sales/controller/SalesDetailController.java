package com.code.aon.ui.sales.controller;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

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
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.sales.ISalesMessages;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.DeliveryDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class SalesDetailController extends LinesController implements ISalesConstants {

	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	
	public IPriceStrategy getPriceStrategy(){
		if(priceStrategy == null){
			priceStrategy = PriceStrategyFactory.getPriceStrategy();
		}
		return priceStrategy;
	}

	public boolean isLongDescription() {
		return longDescription;
	}

	public void setLongDescription(boolean longDescription) {
		this.longDescription = longDescription;
	}

	public void onLongDescription(ActionEvent event) {
		setLongDescription(true);

		SalesDetail salesDetail = (SalesDetail)getTo();
		if (StringUtils.equals(salesDetail.getItem().getProduct().getName().trim(), salesDetail.getDescription().trim())) {
			String longDescription = salesDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				salesDetail.setDescription(salesDetail.getDescription() + "\r\n" + longDescription);
			}
		}
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

	public boolean isEditable() throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getTo();
		if (salesDetail != null) {
			return isEditable(salesDetail);
		}
		return false;
	}

	public boolean isModelEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			return isEditable((SalesDetail)this.getModel().getRowData());
		}
		return false;
	}

	private boolean isEditable(SalesDetail salesDetail) throws ManagerBeanException {
		return (salesDetail.getOfferDetail() == null || salesDetail.getOfferDetail().getId() == null);
	}

	public void onItemChanged(LookupChangeEvent event) {
		SalesDetail salesDetail = (SalesDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			salesDetail.setItem(item);
			salesDetail.setDescription(item.getFullName());
			if (salesDetail.getQuantity() == 0) {
				salesDetail.setQuantity(1);
			}

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
			info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_SOURCE));
			info.append(" ");
			info.append(AonUtil.getMessage(ICommonMessages.COMMERCIAL_BUNDLE, ICommonMessages.COMMERCIAL_OFFER));
			info.append(" ");
			info.append(salesDetail.getOfferDetail().getOffer().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_DETAIL_LINE));
			info.append(" ");
			info.append(salesDetail.getOfferDetail().getLine());
		}
		return info.toString();
	}

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);
		DecimalFormat formatter = new DecimalFormat(AonUtil.getMessage(ICommonMessages.BUNDLE_NAME, ICommonMessages.QUANTITY_PATTERN));

		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_ID), salesDetail.getId());
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		while (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			//info.append("<p>");
			info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_TRANSFERED_TO));
			info.append(" ");
			info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_TO_DELIVERY));
			info.append(" ");
			info.append(deliveryDetail.getDelivery().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_DETAIL_LINE));
			info.append(" ");
			info.append(deliveryDetail.getLine());
			if (salesDetail.getQuantity() > deliveryDetail.getQuantity()) {
				info.append(" (");
				info.append(formatter.format(deliveryDetail.getQuantity()));
				info.append(" ");
				info.append(AonUtil.getMessage(ISalesMessages.BUNDLE_KEY, ISalesMessages.SALES_DETAIL_UNITS));
				info.append(")");
			}
			//info.append("</p>");
		}
		return info.toString();
	}

	public void onLoadOffer(ActionEvent event) throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		BasicController offerController = (BasicController)AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
		offerController.onLoad(event, salesDetail.getOfferDetail().getOffer().getId(), SALES_FORM_NAME, null);
	}

	public void onLoadDelivery(ActionEvent event) throws ManagerBeanException {
		SalesDetail salesDetail = (SalesDetail)this.getModel().getRowData();
		IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_SALES_DETAIL_ID), salesDetail.getId());
		criteria.addOrder(deliveryDetailBean.getFieldName(IEntityAlias.DELIVERY_DETAIL_ID), false);
		Iterator<?> iterator = deliveryDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			DeliveryDetail deliveryDetail = (DeliveryDetail)iterator.next();
			BasicController deliveryController = (BasicController)AonUtil.getRegisteredBean(DELIVERY_CONTROLLER_NAME);
			deliveryController.onLoad(event, deliveryDetail.getDelivery().getId(), SALES_FORM_NAME, SALES_DETAIL_CONTROLLER_NAME + ".onBackSales");
		}
	}

	public void onBackSales(ActionEvent event) throws ManagerBeanException {
		SalesController salesController = (SalesController) AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
		salesController.refresh(event);

		onSearch(event);
	}

}