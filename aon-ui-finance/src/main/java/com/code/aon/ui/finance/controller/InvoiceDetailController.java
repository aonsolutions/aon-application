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
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

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
		getManagerBean().restoreNullSubPOJOs(invoiceDetail);
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
			InvoiceDetail to = (InvoiceDetail)this.getModel().getRowData();
			if (to.isDeliverySource() || to.isIncomeSource() || to.isSalesSource() || to.isPurchaseSource() || to.isOfferSource()) {
				return false;
			}
		}
		return true;
	}	

	public double getTaxableBase() {
		return getPriceStrategy().getBasePrice((ICalculable)getTo());
	}

	public void fillTaxDataInDetail() throws ManagerBeanException {
		Invoice invoice = (Invoice)getMasterController().getTo();
		InvoiceDetail invoiceDetail = (InvoiceDetail) getTo();
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			Tax vat = invoiceDetail.getItem().getProduct().getVat();
			Tax retention = invoiceDetail.getItem().getProduct().getRetention();
			invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
			invoiceDetail.setVatPercent((vat!=null && vat.getId()!=null) ? getTaxPercent(vat, invoice.getIssueDate(), false) : 0);
			invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
			invoiceDetail.setRetentionPercent((retention!=null && retention.getId()!=null) ? getTaxPercent(retention, invoice.getIssueDate(), false) : 0);
			invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
		}
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
		if (item != null && item.getId() != null && item.getProduct().isWithholding()) {
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
			criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID), tax.getId());
			criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), taxDate);
			criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), taxDate);
			Iterator<?> iterator = taxDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				TaxDetail taxDetail = (TaxDetail)iterator.next();
				percent = surcharge ? taxDetail.getSurcharge() : taxDetail.getValue();
			}
		}
		return percent;
	}

	public double getVatQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getVatPercent());
	}

	public double getRetentionQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getRetentionPercent());
	}

	public double getQuota(double base, double percent) {
		return CommonUtil.round(base * percent / 100);
	}

	public Double getRowStock() throws ManagerBeanException {
		double rowStock = 0;
		InvoiceDetail to = (InvoiceDetail)getTo();
		if (to != null && to.getItem() != null && to.getItem().getId() != null) {
			rowStock = getRowStock(to);
		}
		return new Double(rowStock);
	}

	private double getRowStock(InvoiceDetail to) throws ManagerBeanException {
		double rowStock = 0;
		if (!isNew()) {
			InvoiceDetail invoiceDetail = (InvoiceDetail)getManagerBean().get(to.getId());
			if (to.getItem().equals(invoiceDetail.getItem())) {
				rowStock = invoiceDetail.getQuantity();
			}
		}
		return rowStock;
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
				message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_INVOICE_OFFER);
				refCode = offerDetail.getOffer().getReferenceCode();
				line = offerDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.SALES) {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				SalesDetail salesDetail = (SalesDetail)salesDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_INVOICE_SALES);
				refCode = salesDetail.getSales().getReferenceCode();
				line = salesDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.PURCHASE) {
				IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
				PurchaseDetail purchaseDetail = (PurchaseDetail)purchaseDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_INVOICE_SALES);
				refCode = purchaseDetail.getPurchase().getReferenceCode();
				line = purchaseDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.DELIVERY) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_INVOICE_DELIVERY);
				refCode = deliveryDetail.getDelivery().getReferenceCode();
				line = deliveryDetail.getLine().intValue();
			} else if (invoiceDetail.getSource() == InvoiceSource.INCOME) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_INVOICE_DELIVERY);
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
		} else if (invoiceDetail.getSource() == InvoiceSource.SALES) {
			setSourceViewer(SALES_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Sales)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.getSource() == InvoiceSource.PURCHASE) {
			setSourceViewer(PURCHASE_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Purchase)invoiceDetail.getSourceTo()).getId(), PURCHASE_INVOICE_FORM_NAME, null);
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
