package com.code.aon.ui.commercial.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.commercial.enumeration.OfferDetailStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;
import com.code.aon.sales.dao.ISalesAlias;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class OfferDetailController extends LinesController {

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

		OfferDetail offerDetail = (OfferDetail)getTo();
		if (StringUtils.equals(offerDetail.getItem().getProduct().getName().trim(), offerDetail.getDescription().trim())) {
			String longDescription = offerDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				offerDetail.setDescription(offerDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isPending() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			OfferDetail offerDetail = (OfferDetail)this.getModel().getRowData();
			if (offerDetail.getStatus() != null) {
				return offerDetail.getStatus().equals(OfferDetailStatus.PENDING);
			}
		}
		return false;
	}

	public void onItemChanged(LookupChangeEvent event) {
		OfferDetail offerDetail = (OfferDetail)getTo();
		double price = 0;
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			offerDetail.setItem(item);
			offerDetail.setDescription(item.getProduct().getName() + (item.getDetail() != null ? " " + item.getDetail() : ""));

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

	public String getLineStatusInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		OfferDetail offerDetail = (OfferDetail)this.getModel().getRowData();
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(ISalesAlias.SALES_DETAIL_OFFER_DETAIL_ID), offerDetail.getId());
		Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			SalesDetail salesDetail = (SalesDetail)iterator.next();
			info.append(AonUtil.getMessage("commercialBundle", "commercial_offer_transfered_to"));
			info.append(" ");
			info.append(AonUtil.getMessage("commercialBundle", "commercial_offer_to_sales"));
			info.append(" ");
			info.append(salesDetail.getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage("commercialBundle", "commercial_offer_detail_line"));
			info.append(" ");
			info.append(salesDetail.getLine());
		}
		return info.toString();
	}

}