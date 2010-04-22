package com.code.aon.finance.invoicing.engine.fee;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Invoice;
import com.code.aon.finance.InvoiceDetail;

public class PreInvoice extends Invoice {
	
	private static final long serialVersionUID = 6315145312817823828L;

	private List<PreInvoiceDetail> details;
	
	public PreInvoice(){
		this.details = new LinkedList<PreInvoiceDetail>();
	}
	
	public PreInvoice(Invoice invoice){
		this.setId(invoice.getId());
		this.setIssueDate(invoice.getIssueDate());
		this.setNumber(invoice.getNumber());
		this.setRegistry(invoice.getRegistry());
		this.setRegistryAddress(invoice.getRegistryAddress());
		this.setRegistryDocument(invoice.getRegistryDocument());
		this.setRegistryName(invoice.getRegistryName());
		this.setSecurityLevel(invoice.getSecurityLevel());
		this.setSeries(invoice.getSeries());
		this.setStatus(invoice.getStatus());
		this.setSurcharge(invoice.isSurcharge());
		this.setTaxFree(invoice.isTaxFree());
		this.setWithholding(invoice.isWithholding());
		this.setType(invoice.getType());
		this.details = new LinkedList<PreInvoiceDetail>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List getDetailList() {
		return details;
	}
	
	public void addPreInvoiceDetail(InvoiceDetail detail) throws ManagerBeanException{
		PreInvoiceDetail preInvoiceDetail = new PreInvoiceDetail(detail);
		preInvoiceDetail.addInvoiceTaxes(detail);
		details.add(preInvoiceDetail);
	}
}
