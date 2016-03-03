package com.code.aon.ui.commercial.controller;

import static com.code.aon.ui.common.ICommonMessages.INVOICE_SALES;
import static com.code.aon.ui.common.ICommonMessages.LINE;
import static com.code.aon.ui.common.ICommonMessages.TRANSFERED_TO;

import java.util.Iterator;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.product.strategy.PriceStrategyFactory;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class OfferDetailController extends LinesController implements ICommercialConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
		if (StringUtils.equals(offerDetail.getItem().getFullName().trim(), offerDetail.getDescription().trim())) {
			String longDescription = offerDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				offerDetail.setDescription(offerDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public void onItemChanged(LookupChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Item item = (Item)event.getNewValue();
			Offer offer = (Offer)getMasterController().getTo();

			OfferDetail offerDetail = (OfferDetail)getTo();
			offerDetail.setItem(item);
			offerDetail.setDescription(item.getFullName());
			if (offerDetail.getQuantity() == 0) {
				offerDetail.setQuantity(1);
			}
			offerDetail.setPrice(getPriceStrategy().getUnitPrice(offerDetail, offer.getIssueDate(), offer.getTarget()));
		}
	}	

	public void onQuantityChanged(ValueChangeEvent event) {
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			Offer offer = (Offer)getMasterController().getTo();
			OfferDetail offerDetail = (OfferDetail)getTo();
			if (offerDetail.getItem() != null && offerDetail.getItem().getId() != null) {
				offerDetail.setQuantity((Double)event.getNewValue());
				offerDetail.setPrice(getPriceStrategy().getUnitPrice(offerDetail, offer.getIssueDate(), offer.getTarget()));
			}
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
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_OFFER_DETAIL_ID), offerDetail.getId());
		Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			SalesDetail salesDetail = (SalesDetail)iterator.next();
			info.append(AonUtil.getMessage(TRANSFERED_TO));
			info.append(' ');
			info.append(AonUtil.getMessage(INVOICE_SALES));
			info.append(' ');
			info.append(salesDetail.getSales().getReferenceCode());
			info.append(" - ");
			info.append(AonUtil.getMessage(LINE));
			info.append(' ');
			info.append(salesDetail.getLine());
		}
		return info.toString();
	}

	public void onLoadSales(ActionEvent event) throws ManagerBeanException {
		OfferDetail offerDetail = (OfferDetail)this.getModel().getRowData();
		IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(salesDetailBean.getFieldName(IEntityAlias.SALES_DETAIL_OFFER_DETAIL_ID), offerDetail.getId());
		Iterator<?> iterator = salesDetailBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			SalesDetail salesDetail = (SalesDetail)iterator.next();
			BasicController salesController = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
			salesController.onLoad(event, salesDetail.getSales().getId(), NAVIGATION_OFFER_FORM, OFFER_DETAIL_CONTROLLER_NAME + ".onBackOffer");
		}
	}

	public void onBackOffer(ActionEvent event) throws ManagerBeanException {
		OfferController offerController = (OfferController) AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
		offerController.refresh(event);

		onSearch(event);
	}

}