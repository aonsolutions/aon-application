package com.code.aon.finance;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.commercial.OfferDetail;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.enumeration.InvoiceSource;
import com.code.aon.finance.enumeration.PrepaymentCollect;
import com.code.aon.product.strategy.ICalculable;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.purchase.PurchaseDetail;
import com.code.aon.ql.Criteria;
import com.code.aon.sales.SalesDetail;
import com.code.aon.tas.ProjectTas;
import com.code.aon.warehouse.DeliveryDetail;
import com.code.aon.warehouse.IStockable;
import com.code.aon.warehouse.IncomeDetail;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.entity.master.InvoiceDetailDB;

@Entity
@Table(name="invoice_detail")
public class InvoiceDetail extends InvoiceDetailDB implements ICalculable, IStockable, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean updateEnabled;
	private boolean taxDataInDetail;
	private boolean skipServiceProcess;
	private double vatPercent;
	private double vatQuota;
	private double surchargePercent;
	private double surchargeQuota;
	private double retentionPercent;
	private double retentionQuota;

	public InvoiceDetail() {
		setUpdateEnabled(true);
		setTaxDataInDetail(false);
		setSkipServiceProcess(false);
	}

    public void setPrice(double price) {
        super.setPrice(CommonUtil.round(price, 4));
    }

	public void setTaxableBase(double taxableBase) {
		super.setTaxableBase(CommonUtil.round(taxableBase, 4));
	}
	
	public void setTaxes(double taxes) {
		super.setTaxes(CommonUtil.round(taxes, 3));
	}

	@Transient
	public boolean isUpdateEnabled() {
		return updateEnabled;
	}
	public void setUpdateEnabled(boolean updateEnabled) {
		this.updateEnabled = updateEnabled;
	}

	@Transient
	public boolean isTaxDataInDetail() {
		return taxDataInDetail;
	}
	public void setTaxDataInDetail(boolean taxDataInDetail) {
		this.taxDataInDetail = taxDataInDetail;
	}

	@Transient
	public boolean isSkipServiceProcess() {
		return skipServiceProcess;
	}
	public void setSkipServiceProcess(boolean skipServiceProcess) {
		this.skipServiceProcess = skipServiceProcess;
	}

	@Transient
	public double getVatPercent() {
		return vatPercent;
	}
	public void setVatPercent(double vatPercent) {
		this.vatPercent = CommonUtil.round(vatPercent);
	}

	@Transient
	public double getVatQuota() {
		return vatQuota;
	}
	public void setVatQuota(double vatQuota) {
		this.vatQuota = CommonUtil.round(vatQuota);
	}

	@Transient
	public double getSurchargePercent() {
		return surchargePercent;
	}
	public void setSurchargePercent(double surchargePercent) {
		this.surchargePercent = CommonUtil.round(surchargePercent);
	}

	@Transient
	public double getSurchargeQuota() {
		return surchargeQuota;
	}
	public void setSurchargeQuota(double surchargeQuota) {
		this.surchargeQuota = CommonUtil.round(surchargeQuota);
	}

	@Transient
	public double getRetentionPercent() {
		return retentionPercent;
	}
	public void setRetentionPercent(double retentionPercent) {
		this.retentionPercent = CommonUtil.round(retentionPercent);
	}

	@Transient
	public double getRetentionQuota() {
		return retentionQuota;
	}
	public void setRetentionQuota(double retentionQuota) {
		this.retentionQuota = CommonUtil.round(retentionQuota);
	}

	@Transient
	public List<TaxBreakDown> getTaxBreakDowns() {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		try {
			IManagerBean invoiceTaxBean = BeanManager.getManagerBean(InvoiceTax.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_INVOICE_DETAIL_ID), getId());
			criteria.addOrder(invoiceTaxBean.getFieldName(IEntityAlias.INVOICE_TAX_TAX_TYPE));
			for (ITransferObject ito : invoiceTaxBean.getList(criteria)) {
				InvoiceTax invoiceTax = (InvoiceTax)ito;
				TaxBreakDown taxBreakDown = new TaxBreakDown();
				taxBreakDown.setBase(invoiceTax.getBase());
				taxBreakDown.setTaxType(invoiceTax.getTaxType());
				taxBreakDown.setTaxPercent(invoiceTax.getPercentage());
				taxBreakDown.setSurchargePercent(invoiceTax.getSurcharge());
				taxBreakDown.setTaxQuota(invoiceTax.getQuota());
				taxBreakDown.setSurchargeQuota(invoiceTax.getSurchargeQuota());
				taxBreakDown.setVatDeductionType(invoiceTax.getVatDeductionType());
				taxBreakDown.setDeductibleBase(invoiceTax.getBase());
				taxBreakDown.setDeductiblePercent(invoiceTax.getDeductiblePercent());
				taxBreakDown.setDeductibleQuota(invoiceTax.getDeductibleQuota());
				taxBreakDowns.add(taxBreakDown);
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
		}
		return taxBreakDowns;
	}

	@Transient
	public void fillTaxDataInDetail() {
		for (TaxBreakDown taxBreakDown : getTaxBreakDowns()) {
			if (taxBreakDown.isVat()) {
				setVatPercent(taxBreakDown.getTaxPercent());
				setVatQuota(taxBreakDown.getTaxQuota());
				setSurchargePercent(taxBreakDown.getSurchargePercent());
				setSurchargeQuota(taxBreakDown.getSurchargeQuota());
			} else if (taxBreakDown.isRetention()) {
				setRetentionPercent(taxBreakDown.getTaxPercent());
				setRetentionQuota(taxBreakDown.getTaxQuota());
			}
		}
	}

	@Transient
	public double getSalesPrice() {
		if (getVatQuota() == 0 && getRetentionQuota() == 0) {
			fillTaxDataInDetail();
		}
		return CommonUtil.round(getPrice() * (1 + getVatPercent() / 100 + getSurchargePercent() / 100 - getRetentionPercent() / 100));
	}
	@Transient
	public void setSalesPrice(double salesPrice) {
	}

	@Transient
	public double getTotalSalesPrice() {
		if (getVatQuota() == 0 && getRetentionQuota() == 0) {
			fillTaxDataInDetail();
		}
		if (getVatQuota() == 0 && getRetentionQuota() == 0) {
			return CommonUtil.round(getTaxableBase() * (1 + getVatPercent() / 100 + getSurchargePercent() / 100 - getRetentionPercent() / 100));
		} else {
			return CommonUtil.round(getTaxableBase() + getVatQuota() - getRetentionQuota());
		}
	}

	@Transient
	public ITransferObject getSourceTo() throws ManagerBeanException {
		if (getSourceId() != null) {
			if (InvoiceSource.DELIVERY == getSource()) {
				IManagerBean deliveryDetailBean = BeanManager.getManagerBean(DeliveryDetail.class);
				return ((DeliveryDetail)deliveryDetailBean.get(getSourceId())).getDelivery();
			}
			if (InvoiceSource.INCOME == getSource()) {
				IManagerBean incomeDetailBean = BeanManager.getManagerBean(IncomeDetail.class);
				return ((IncomeDetail)incomeDetailBean.get(getSourceId())).getIncome();
			}
			if (InvoiceSource.SALES == getSource()) {
				IManagerBean salesDetailBean = BeanManager.getManagerBean(SalesDetail.class);
				return ((SalesDetail)salesDetailBean.get(getSourceId())).getSales();
			}
			if (InvoiceSource.PURCHASE == getSource()) {
				IManagerBean purchaseDetailBean = BeanManager.getManagerBean(PurchaseDetail.class);
				return ((PurchaseDetail)purchaseDetailBean.get(getSourceId())).getPurchase();
			}
			if (InvoiceSource.OFFER == getSource()) {
				IManagerBean offerDetailBean = BeanManager.getManagerBean(OfferDetail.class);
				return ((OfferDetail)offerDetailBean.get(getSourceId())).getOffer();
			}
		}
		return null;
	}

	@Transient
	public boolean isDeliverySource() {
		return (getSource() == InvoiceSource.DELIVERY);
	}
	@Transient
	public boolean isIncomeSource() {
		return (getSource() == InvoiceSource.INCOME);
	}
	@Transient
	public boolean isSalesSource() {
		return (getSource() == InvoiceSource.SALES);
	}
	@Transient
	public boolean isPurchaseSource() {
		return (getSource() == InvoiceSource.PURCHASE);
	}
	@Transient
	public boolean isOfferSource() {
		return (getSource() == InvoiceSource.OFFER);
	}
	@Transient
	public boolean isFeeSource() {
		return (getSource() == InvoiceSource.FEE);
	}
	@Transient
	public boolean isReservationSource() {
		return (getSource() == InvoiceSource.RESERVATION);
	}
	@Transient
	public boolean isPrepaymentSource() throws ManagerBeanException {
		if (isFeeSource() && isPrepayment()) {
			IManagerBean prepaymentBean = BeanManager.getManagerBean(Prepayment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT), PrepaymentCollect.INVOICE_DETAIL);
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT_ID), getId());
			return prepaymentBean.getCount(criteria) > 0;
		}
		return false;
	}

	@Transient
	public Integer getPrepaymentId() throws ManagerBeanException {
		if (isFeeSource() && isPrepayment()) {
			IManagerBean prepaymentBean = BeanManager.getManagerBean(Prepayment.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT), PrepaymentCollect.INVOICE_DETAIL);
			criteria.addEqualExpression(prepaymentBean.getFieldName(IEntityAlias.PREPAYMENT_COLLECT_ID), getId());
			for (ITransferObject ito : prepaymentBean.getList(criteria)) {
				return ((Prepayment)ito).getId();
			}
		}
		return null;
	}

	@Transient
	public ITransferObject getSpecificProject() throws ManagerBeanException {
		if (getProject() != null && getProject().isTas()) {
			return (ProjectTas)BeanManager.getManagerBean(ProjectTas.class).get(getProject().getId());
		}
		return null;
	}

	@Transient
	public boolean isEntry() {
		return (getInvoice().isPurchase());
	}
	@Transient
	public String getTableName() {
		return "invoice_detail";
	}

}