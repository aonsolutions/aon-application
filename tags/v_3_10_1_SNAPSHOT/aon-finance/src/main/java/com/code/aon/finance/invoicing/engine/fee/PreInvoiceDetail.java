package com.code.aon.finance.invoicing.engine.fee;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tax;
import com.code.aon.config.TaxDetail;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.finance.InvoiceDetail;
import com.code.aon.finance.InvoiceTax;
import com.code.aon.product.strategy.TaxBreakDown;
import com.code.aon.ql.Criteria;

public class PreInvoiceDetail extends InvoiceDetail {
	
	private static final long serialVersionUID = -8846731278767129686L;

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
		addTax(detail, detail.getItem().getProduct().getVat());
		if(detail.getInvoice().isWithholding() && detail.getItem().getProduct().getRetention() != null){
			addTax(detail, detail.getItem().getProduct().getRetention());
		}
	}
	
	private void addTax(InvoiceDetail detail, Tax tax) throws ManagerBeanException {
		if(detail.getInvoice().getIssueDate().before(tax.getStartDate())){
			tax = obtainTax(tax.getId(),detail.getInvoice().getIssueDate());
		}
		InvoiceTax invoiceTax = new InvoiceTax();
		invoiceTax.setInvoiceDetail(detail);
		invoiceTax.setTaxType(tax.getType());
		double surcharge = 0.0;
		double percentage = 0.0;
		if(!detail.getInvoice().isTaxFree()){
			percentage = tax.getPercentage();
			if(detail.getInvoice().isSurcharge()){
				surcharge = tax.getSurcharge();
			}
		}
		invoiceTax.setPercentage(percentage);
		invoiceTax.setSurcharge(surcharge);
		this.taxList.add(invoiceTax);
	}
	
	@SuppressWarnings("unchecked")
	private Tax obtainTax(Integer id, Date date) throws ManagerBeanException {
		IManagerBean taxDetailBean = BeanManager.getManagerBean(TaxDetail.class);
    	Criteria criteria = new Criteria();
    	criteria.addEqualExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_TAX_ID),id);
    	criteria.addLessThanExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_START_DATE),date);
    	criteria.addGreaterThanExpression(taxDetailBean.getFieldName(IConfigAlias.TAX_DETAIL_END_DATE),date);
    	Iterator iter = taxDetailBean.getList(criteria).iterator();
    	while(iter.hasNext()){
    		TaxDetail taxDetail = (TaxDetail)iter.next();
    		Tax tax = new Tax();
    		tax.setId(taxDetail.getTax().getId());
    		tax.setPercentage(taxDetail.getValue());
    		tax.setSurcharge(taxDetail.getSurcharge());
    		tax.setType(taxDetail.getTax().getType());
    		return tax;
    	}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List getTaxBreakDowns() {
		List<TaxBreakDown> taxBreakDowns = new LinkedList<TaxBreakDown>();
		Iterator iter = this.taxList.iterator();
		while(iter.hasNext()){
			InvoiceTax invoiceTax = (InvoiceTax)iter.next();
			TaxBreakDown taxBreakDown = new TaxBreakDown();
			taxBreakDown.setBase(getTaxableBase());
			taxBreakDown.setTaxType(invoiceTax.getTaxType());
			taxBreakDown.setTaxPercent(invoiceTax.getPercentage());
			taxBreakDown.setSurchargePercent(invoiceTax.getSurcharge());
			taxBreakDowns.add(taxBreakDown);
		}
		return taxBreakDowns;
	}
}
