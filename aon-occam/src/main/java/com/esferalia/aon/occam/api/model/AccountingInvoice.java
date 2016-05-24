package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceVAT;
import com.esferalia.aon.occam.api.model.finance.InvoiceWithholding;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.InvoiceTransactionType;
import com.esferalia.aon.watson.util.AonMathUtils;

public class AccountingInvoice implements Serializable {
	
	private static final long serialVersionUID = -4435280253306756102L;
	
	private Invoice invoice;
	private AccountingRegistry registry;

	private InvoiceWithholding withholdingData;
	private LinkedList<InvoiceVAT> vats;
	
	public Invoice getInvoice() {
		return invoice;
	}
	public AccountingInvoice setInvoice(Invoice invoice) {
		this.invoice = invoice;
		return this;
	}
	
	public AccountingRegistry getRegistry() {
		return registry;
	}

	public AccountingInvoice setRegistry(AccountingRegistry registry) {
		this.registry = registry;
		return this;
	}

	public InvoiceTransactionType getTransaction() {
		return invoice != null?invoice.getTransaction():null;
	}

	public InvoiceWithholding getWithholdingData() {
		return withholdingData;
	}
	public AccountingInvoice setWithholdingData(InvoiceWithholding withholdingData) {
		this.withholdingData = withholdingData;
		return this;
	}
	public LinkedList<InvoiceVAT> getVats() {
		return vats;
	}
	public LinkedList<InvoiceVAT> addVat(InvoiceVAT vat) {
		if (getVats() == null) {
			setVats(new LinkedList<InvoiceVAT>());
		}
		getVats().add(vat);
		return vats;
	}
	public AccountingInvoice setVats(LinkedList<InvoiceVAT> vats) {
		this.vats = vats;
		return this;
	}
	
	public double getTotalTaxableBase() {
		if (getVats() == null) return 0.0;
		double tb = 0.0; 
		for (InvoiceVAT vat : getVats()) {
			tb = tb + vat.getBase();
		}
		return AonMathUtils.round(tb);
	}
	
	public double getTotalInvoice() {
		// En el caso de las intracomunitarias o ISP, ambos IVAS están 
		// habilitados, pero no deben ir al total factura.
		if (isInputVatEnabled() != isOutputVatEnabled()) {
			if (getVats() == null) return 0.0;
			double total = 0.0; 
			for (InvoiceVAT vat : getVats()) {
				total = total + vat.getBase() + vat.getQuota() + vat.getSurchargeQuota();
			}
			total = total - (getWithholdingData()==null?0.0:getWithholdingData().getQuota());
			return AonMathUtils.round(total);
		}
		return getTotalTaxableBase();
	}
	
	public boolean isNational() {
		return invoice != null && invoice.isNational();
	}
	public boolean isIntracommunity() {
		return invoice != null && invoice.isIntracommunity();
	}
	public boolean isIsp() {
		return invoice != null && invoice.isIsp();
	}

	public boolean isSales() {
		return invoice != null && invoice.isSales(); 
	}

	public boolean isSurcharge() {
		return invoice != null && invoice.isSurcharge();
	}

	public boolean isWithholding() {
		return invoice != null && invoice.isWithholding();
	}

	public boolean isWithholdingFarmer() {
		return invoice != null && invoice.isWithholdingFarmer();
	}

	public boolean isVatAccrualPayment() {
		return invoice != null && invoice.isVatAccrualPayment();
	}

	public boolean isInputVatEnabled() {
		return invoice != null && invoice.isInputVatEnabled();
	}
	public boolean isOutputVatEnabled() {
		return invoice != null && invoice.isOutputVatEnabled();
	}
	public static void main(String[] args) {
		System.out.println( true ^ true);
		System.out.println( true ^ false);
		System.out.println( false ^ true);
		System.out.println( false ^ false);
		
	}	
}
