package com.code.aon.ui.finance.controller;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.commercial.Offer;
import com.code.aon.commercial.OfferDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;

public class InvoiceDetailController extends LinesController implements IFinanceConstants {

	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private InvoiceDetail invoiceDetail;
	private String sourceViewer;

	public IPriceStrategy getPriceStrategy() {
		if (priceStrategy == null) {
			priceStrategy = new InvoicePriceStrategy();
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

		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (StringUtils.equals(invoiceDetail.getItem().getProduct().getName().trim(), invoiceDetail.getDescription().trim())) {
			String longDescription = invoiceDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				invoiceDetail.setDescription(invoiceDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}

	public void onInvoiceDetailProjectShow(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setInvoiceDetail((InvoiceDetail)this.getModel().getRowData());
		}
	}

	public void addInvoiceDetailProject(ActionEvent event) throws ManagerBeanException {
		invoiceDetail.setUpdateEnabled(false);
		getManagerBean().update(invoiceDetail);
	}

	public String getSourceViewer() {
		return sourceViewer;
	}

	public void setSourceViewer(String sourceViewer) {
		this.sourceViewer = sourceViewer;
	}

	public boolean isEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
			InvoiceSource source = invoiceDetail.getSource();
			if (source == InvoiceSource.DELIVERY || source == InvoiceSource.INCOME || source == InvoiceSource.OFFER) {
				return false;
			}
		}
		return true;
	}	

	public double getTaxableBase() {
		return getPriceStrategy().getBasePrice((ICalculable)getTo());
	}

	public double getVatPercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(item.getProduct().getVat(), invoice.getIssueDate(), false);
		}
		return 0;
	}

	public double getSurchargePercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(item.getProduct().getVat(), invoice.getIssueDate(), true);
		}
		return 0;
	}

	public double getRetentionPercent() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getRetention() != null && item.getProduct().getRetention().getId() != null) {
			return getTaxPercent(item.getProduct().getRetention(), invoice.getIssueDate(), false);
		}
		return 0;
	}

	public double getTaxPercent(Tax tax, Date taxDate, boolean surcharge) throws ManagerBeanException {
		double percent = 0;
		if (!tax.getStartDate().after(taxDate)) {
			percent = surcharge ? tax.getSurcharge() : tax.getPercentage();
		} else {
			IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID), tax.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_START_DATE), taxDate);
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_END_DATE), taxDate);
			Iterator<?> iterator = taxDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				TaxDetail taxDetail = (TaxDetail)iterator.next();
				percent = surcharge ? taxDetail.getSurcharge() : taxDetail.getValue();
			}
		}
		return percent;
	}

	public String getLineSourceInfo() throws ManagerBeanException {
		StringBuffer info = new StringBuffer(64);

		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		if (!isEditable() && invoiceDetail.getSourceId() != null) {
			String message = "";
			String refCode = "";
			int line = 0;
			if (invoiceDetail.getSource() == InvoiceSource.OFFER) {
				IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
				OfferDetail offerDetail = (OfferDetail)offerDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage("commercialBundle", "commercial_offer");
				refCode = offerDetail.getOffer().getReferenceCode();
				line = offerDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage("financeBundle", "finance_invoice_delivery");
				refCode = deliveryDetail.getDelivery().getReferenceCode();
				line = deliveryDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.INCOME) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage("financeBundle", "finance_invoice_delivery");
				refCode = incomeDetail.getIncome().getReferenceCode();
				line = incomeDetail.getLine().intValue();
			}

			info.append(AonUtil.getMessage("financeBundle", "finance_source"));
			info.append(" ");
			info.append(message);
			info.append(" ");
			info.append(refCode);
			info.append(" - ");
			info.append(AonUtil.getMessage("financeBundle", "finance_invoice_detail_line"));
			info.append(" ");
			info.append(line);
		}
		return info.toString();
	}

	public void onLoadSource(ActionEvent event) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		if (invoiceDetail.getSource() == InvoiceSource.OFFER) {
			setSourceViewer(OFFER_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Offer)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
			setSourceViewer(DELIVERY_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(DELIVERY_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Delivery)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.getSource() == InvoiceSource.INCOME) {
			setSourceViewer(INCOME_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(INCOME_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Income)invoiceDetail.getSourceTo()).getId(), PURCHASE_INVOICE_FORM_NAME, null);
		}
	}

}
