package com.code.aon.ui.finance.controller;

import static com.code.aon.ui.common.ICommonMessages.LINE;
import static com.code.aon.ui.common.ICommonMessages.SOURCE;

import java.util.Date;
import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
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
import com.code.aon.finance.invoicing.pricing.InvoicePriceStrategy;
import com.code.aon.product.Item;
import com.code.aon.product.enumeration.ProductType;
import com.code.aon.product.pricing.ItemPricesManager;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.IPriceStrategy;
import com.code.aon.purchase.Purchase;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.registry.Registry;
import com.code.aon.registry.RegistryTax;
import com.code.aon.sales.Sales;
import com.code.aon.sales.SalesDetail;
import com.code.aon.ui.common.ICommonMessages;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.code.aon.warehouse.Delivery;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.Income;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;

public class InvoiceDetailController extends LinesController implements IFinanceConstants {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IPriceStrategy priceStrategy;
	private boolean longDescription;
	private boolean showItemPackageWindow;
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
		if (StringUtils.equals(invoiceDetail.getItem().getFullName().trim(), invoiceDetail.getDescription().trim())) {
			String longDescription = invoiceDetail.getItem().getDescription();
			if (!StringUtils.isEmpty(longDescription)) {
				invoiceDetail.setDescription(invoiceDetail.getDescription() + "\r\n" + longDescription);
			}
		}
	}

	public void onShortDescription(ActionEvent event) {
		setLongDescription(false);
	}

	public boolean isShowItemPackageWindow() {
		return showItemPackageWindow;
	}

	public void setShowItemPackageWindow(boolean value) {
		this.showItemPackageWindow = value;
	}

	public InvoiceDetail getInvoiceDetail() {
		return invoiceDetail;
	}

	public void setInvoiceDetail(InvoiceDetail invoiceDetail) {
		this.invoiceDetail = invoiceDetail;
	}

	public void onInvoiceDetailSelect(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			setInvoiceDetail((InvoiceDetail)this.getModel().getRowData());
		}
	}

	public void onInvoiceDetailSave(ActionEvent event) throws ManagerBeanException {
		getInvoiceDetail().setUpdateEnabled(false);
		getManagerBean().restoreNullSubPOJOs(getInvoiceDetail());
		getManagerBean().update(getInvoiceDetail());
	}

	public void onInvoiceDetailSaveAndUpdate(ActionEvent event) throws ManagerBeanException {
		getInvoiceDetail().setInvoice(getInvoice());
		getInvoiceDetail().setUpdateEnabled(true);
		getManagerBean().restoreNullSubPOJOs(getInvoiceDetail());
		getManagerBean().update(getInvoiceDetail());
	}

	public String getSourceViewer() {
		return sourceViewer;
	}

	public void setSourceViewer(String sourceViewer) {
		this.sourceViewer = sourceViewer;
	}

	public Invoice getInvoice() {
		return (Invoice)getMasterController().getTo();
	}

	public boolean isEditable() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			InvoiceDetail to = (InvoiceDetail)this.getModel().getRowData();
			if (to.isDeliverySource() || to.isIncomeSource() || to.isSalesSource() || to.isPurchaseSource() || to.isOfferSource() || to.isPrepaymentSource()) {
				return false;
			}
		}
		return true;
	}	

	public boolean isIncreaseDetail() {
		if (getInvoice().isSales()) {
			Item item = (getTo() != null) ? ((InvoiceDetail)getTo()).getItem() : null;
			return item != null && item.getId() != null && item.getProduct().getType() == ProductType.INCREASE;
		}
		return false;
	}

	public void onItemPackageShow(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		invoiceDetail.getItem().initializePackQuantities(invoiceDetail.getQuantity());
	}

	public void onAssignItemPackage(ActionEvent event) {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		invoiceDetail.setQuantity(invoiceDetail.getItem().getPackStockQuantity());
	}

	public double getTaxableBase() {
		ICalculable calculable = (ICalculable)getTo();
		double taxableBase = getPriceStrategy().getBasePrice(calculable);
		return (!isIncreaseDetail()) ? taxableBase : CommonUtil.round(calculable.getPrice() - taxableBase);
	}

	public double getTotal() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		return CommonUtil.round(getTaxableBase() + getVatQuota(invoiceDetail) + getSurchargeQuota(invoiceDetail) - getRetentionQuota(invoiceDetail));
	}

	public void fillTaxDataInDetail(boolean workWithSalesPrice, boolean includeQuotas) {
		Invoice invoice = getInvoice();
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		if (invoiceDetail.getItem() != null && invoiceDetail.getItem().getId() != null) {
			invoiceDetail.setVatPercent(isVatIncluded() ? getVatPercent() : 0);
			invoiceDetail.setSurchargePercent(invoice.isSurcharge() ? getSurchargePercent() : 0);
			invoiceDetail.setRetentionPercent(!invoice.isRetentionFree() && invoice.isWithholding() ? getRetentionPercent() : 0);

			if (workWithSalesPrice) {
				ItemPricesManager pricesManager = new ItemPricesManager();
				double salesPrice = pricesManager.getSalesPrice(invoiceDetail.getVatPercent(), invoiceDetail.getRetentionPercent(), invoiceDetail.getPrice());
				invoiceDetail.setPrice(salesPrice);
				double totalSalesPrice = CommonUtil.round(getPriceStrategy().getBasePrice(invoiceDetail));
				invoiceDetail.setPrice(pricesManager.getPrice(invoiceDetail.getVatPercent(), invoiceDetail.getRetentionPercent(), salesPrice, 4));
				invoiceDetail.setTaxableBase(pricesManager.getPrice(invoiceDetail.getVatPercent(), invoiceDetail.getRetentionPercent(), totalSalesPrice, 4));
				if (includeQuotas) {
					if (invoiceDetail.getSurchargePercent() == 0 && invoiceDetail.getRetentionPercent() == 0) {
						double taxableBase = CommonUtil.round(invoiceDetail.getTaxableBase());
						invoiceDetail.setVatQuota(CommonUtil.round(totalSalesPrice - taxableBase));
					} else {
						invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
						invoiceDetail.setSurchargeQuota(getSurchargeQuota(invoiceDetail));
						invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
					}
				}
			} else {
				invoiceDetail.setTaxableBase(getPriceStrategy().getBasePrice(invoiceDetail));
				if (includeQuotas) {
					invoiceDetail.setVatQuota(getVatQuota(invoiceDetail));
					invoiceDetail.setSurchargeQuota(getSurchargeQuota(invoiceDetail));
					invoiceDetail.setRetentionQuota(getRetentionQuota(invoiceDetail));
				}
			}
		}
	}

	public boolean isVatIncluded() {
		Invoice invoice = getInvoice();
		return !invoice.isVatFree() || (!invoice.isSales() && (invoice.isIntracommunity() || invoice.isOtherISP()));
	}

	public double getVatPercent() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(getInvoice().getRegistry(), item.getProduct().getVat(), getInvoice().getIssueDate(), false);
		}
		return 0;
	}

	public double getSurchargePercent() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().getVat() != null && item.getProduct().getVat().getId() != null) {
			return getTaxPercent(getInvoice().getRegistry(), item.getProduct().getVat(), getInvoice().getIssueDate(), true);
		}
		return 0;
	}

	public double getRetentionPercent() {
		InvoiceDetail invoiceDetail = (InvoiceDetail)getTo();
		Item item = invoiceDetail.getItem();
		if (item != null && item.getId() != null && item.getProduct().isWithholding()) {
			return getTaxPercent(getInvoice().getRegistry(), item.getProduct().getRetention(), getInvoice().getIssueDate(), false);
		}
		return 0;
	}

	public double getTaxPercent(Registry registry, Tax tax, Date taxDate, boolean surcharge) {
		double percent = 0;
		try {
			RegistryTax rTax = registry.getTax(tax.getId(), taxDate);
			if (rTax != null) {
				percent = surcharge ? rTax.getSurcharge() : rTax.getPercentage();
			} else {
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
			}
		} catch (ManagerBeanException ex) {
			String msg = "Error obteniendo informacion de Impuestos";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return percent;
	}

	public double getVatQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getVatPercent());
	}

	public double getSurchargeQuota(InvoiceDetail invoiceDetail) {
		return getQuota(invoiceDetail.getTaxableBase(), invoiceDetail.getSurchargePercent());
	}

	public double getRetentionQuota(InvoiceDetail invoiceDetail) {
		double base = invoiceDetail.getTaxableBase();
		if (getInvoice().isWithholdingFarmer()) {
			base = CommonUtil.round(base + invoiceDetail.getVatQuota() + invoiceDetail.getSurchargeQuota());
		}
		return getQuota(base, invoiceDetail.getRetentionPercent());
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
		if (!isNevv()) {
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
			String message = null;
			String refCode = null;
			Integer line = null;
			if (invoiceDetail.isOfferSource()) {
				IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
				OfferDetail offerDetail = (OfferDetail)offerDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(ICommonMessages.INVOICE_OFFER);
				refCode = offerDetail.getOffer().getReferenceCode();
				line = offerDetail.getLine();
			} else if (invoiceDetail.isSalesSource()) {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				SalesDetail salesDetail = (SalesDetail)salesDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(ICommonMessages.INVOICE_SALES);
				refCode = salesDetail.getSales().getReferenceCode();
				line = salesDetail.getLine();
			} else if (invoiceDetail.isPurchaseSource()) {
				IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
				PurchaseDetail purchaseDetail = (PurchaseDetail)purchaseDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(ICommonMessages.INVOICE_SALES);
				refCode = purchaseDetail.getPurchase().getReferenceCode();
				line = purchaseDetail.getLine();
			} else if (invoiceDetail.isDeliverySource()) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				DeliveryDetail deliveryDetail = (DeliveryDetail)deliveryDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(ICommonMessages.INVOICE_DELIVERY);
				refCode = deliveryDetail.getDelivery().getReferenceCode();
				line = deliveryDetail.getLine();
			} else if (invoiceDetail.isIncomeSource()) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				IncomeDetail incomeDetail = (IncomeDetail)incomeDetailBean.get(invoiceDetail.getSourceId());
				message = AonUtil.getMessage(ICommonMessages.INVOICE_DELIVERY);
				refCode = incomeDetail.getIncome().getReferenceCode();
				line = incomeDetail.getLine();
			} else if (invoiceDetail.isPrepaymentSource()) {
				message = AonUtil.getMessage(ICommonMessages.FINANCE_PREPAYMENTS);
			}

			if (message != null) {
				info.append(AonUtil.getMessage(SOURCE));
				info.append(" ");
				info.append(message);
			}
			if (refCode != null) {
				info.append(" ");
				info.append(refCode);
			}
			if (line != null) {
				info.append(" - ");
				info.append(AonUtil.getMessage(LINE));
				info.append(" ");
				info.append(line);
			}
		}
		return info.toString();
	}

	public void onLoadSource(ActionEvent event) throws ManagerBeanException {
		InvoiceDetail invoiceDetail = (InvoiceDetail)this.getModel().getRowData();
		if (invoiceDetail.isOfferSource()) {
			setSourceViewer(OFFER_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(OFFER_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Offer)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.isSalesSource()) {
			setSourceViewer(SALES_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(SALES_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Sales)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.isPurchaseSource()) {
			setSourceViewer(PURCHASE_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(PURCHASE_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Purchase)invoiceDetail.getSourceTo()).getId(), PURCHASE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.isDeliverySource()) {
			setSourceViewer(DELIVERY_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(DELIVERY_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Delivery)invoiceDetail.getSourceTo()).getId(), SALE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.isIncomeSource()) {
			setSourceViewer(INCOME_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(INCOME_CONTROLLER_NAME);
			sourceController.onLoad(event, ((Income)invoiceDetail.getSourceTo()).getId(), PURCHASE_INVOICE_FORM_NAME, null);
		} else if (invoiceDetail.isPrepaymentSource()) {
			setSourceViewer(PREPAYMENT_FORM_NAME);
			BasicController sourceController = (BasicController)AonUtil.getRegisteredBean(PREPAYMENT_CONTROLLER_NAME);
			sourceController.onLoad(event, invoiceDetail.getPrepaymentId(), SALE_INVOICE_FORM_NAME, null);
		}
	}

}
