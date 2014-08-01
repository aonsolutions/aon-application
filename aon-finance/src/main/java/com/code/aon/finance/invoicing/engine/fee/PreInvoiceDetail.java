package com.code.aon.finance.invoicing.engine.fee;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class PreInvoiceDetail extends InvoiceDetail {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private List<InvoiceTax> taxList;
	
	public PreInvoiceDetail(){
		this.taxList = new LinkedList<InvoiceTax>();
	}
	
	public PreInvoiceDetail(InvoiceDetail invoiceDetail){
		this.setId(invoiceDetail.getId());
		this.setDescription(invoiceDetail.getDescription());
		this.setDiscountExpression(invoiceDetail.getDiscountExpression());
		this.setInvoice(invoiceDetail.getInvoice());
		this.setItem(invoiceDetail.getItem());
		this.setLine(invoiceDetail.getLine());
		this.setPrice(invoiceDetail.getPrice());
		this.setQuantity(invoiceDetail.getQuantity());
		this.setSource(invoiceDetail.getSource());
		this.setTaxableBase(invoiceDetail.getTaxableBase());
		this.setTaxes(invoiceDetail.getTaxes());
		this.taxList = new LinkedList<InvoiceTax>();
	}
	
	public void addInvoiceTaxes(InvoiceDetail detail) throws ManagerBeanException{
		if (!detail.getInvoice().isVatFree() && detail.getItem().getProduct().getVat() != null) {
			addTax(detail, detail.getItem().getProduct().getVat());
		}
		if (!detail.getInvoice().isRetentionFree() && detail.getInvoice().isWithholding() && detail.getItem().getProduct().isWithholding()) {
			addTax(detail, detail.getItem().getProduct().getRetention());
		}
	}
	
	private void addTax(InvoiceDetail detail, Tax tax) throws ManagerBeanException {
		if (detail.getInvoice().getIssueDate().before(tax.getStartDate())) {
			tax = obtainTax(tax.getId(),detail.getInvoice().getIssueDate());
		}
		InvoiceTax invoiceTax = new InvoiceTax();
		invoiceTax.setInvoiceDetail(detail);
		invoiceTax.setTaxType(tax.getType());
		invoiceTax.setBase(detail.getTaxableBase());
		invoiceTax.setPercentage(tax.getPercentage());
		invoiceTax.setSurcharge((detail.getInvoice().isSurcharge()) ? tax.getSurcharge() : 0.0);
		this.taxList.add(invoiceTax);
	}
	
	private Tax obtainTax(Integer id, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_TAX_ID),id);
    	criteria.addLessThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_START_DATE), date);
    	criteria.addGreaterThanOrEqualExpression(taxDetailBean.getFieldName(IEntityAlias.TAX_DETAIL_END_DATE), date);
    	for (ITransferObject ito : taxDetailBean.getList(criteria)) {
    		TaxDetail taxDetail = (TaxDetail)ito;
    		Tax tax = new Tax();
    		tax.setId(taxDetail.getTax().getId());
    		tax.setPercentage(taxDetail.getValue());
    		tax.setSurcharge(taxDetail.getSurcharge());
    		tax.setType(taxDetail.getTax().getType());
    		return tax;
    	}
		return null;
	}
	
	public List<TaxBreakDown> getTaxBreakDowns() {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		for (ITransferObject ito : taxList) {
			InvoiceTax invoiceTax = (InvoiceTax)ito;
			TaxBreakDown taxBreakDown = new TaxBreakDown();
			taxBreakDown.setBase(invoiceTax.getBase());
			taxBreakDown.setTaxType(invoiceTax.getTaxType());
			taxBreakDown.setTaxPercent(invoiceTax.getPercentage());
			taxBreakDown.setSurchargePercent(invoiceTax.getSurcharge());
			taxBreakDowns.add(taxBreakDown);
		}
		return taxBreakDowns;
	}
}
