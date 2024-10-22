package net.aonsolutions.occam.api.model;

import java.io.Serializable;
import java.util.Date;
import java.util.EnumMap;

import net.aonsolutions.occam.api.model.type.VATTaxRegime;

public class InvoiceFiscal implements Serializable {
	
	private static final long serialVersionUID = -1911682283856222146L;
	
	private Integer invoice;
	private Integer domain;
	private Date issueDate;
	private Date taxDate;
	private Date expDate;
	private EnumMap<VATTaxRegime,Boolean> vatRegimes = new EnumMap<>(VATTaxRegime.class);
	
	
	public Integer getInvoice() {
		return invoice;
	}
	public InvoiceFiscal setInvoice(Integer invoice) {
		this.invoice = invoice;
		return this;
	}

	public Integer getDomain() {
		return domain;
	}
	public InvoiceFiscal setDomain(Integer domain) {
		this.domain = domain;
		return this;
	}
	
	public Date getIssueDate() {
		return issueDate;
	}
	public InvoiceFiscal setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}
	
	public Date getTaxDate() {
		return taxDate;
	}
	public InvoiceFiscal setTaxDate(Date taxDate) {
		this.taxDate = taxDate;
		return this;
	}
	
	public Date getExpDate() {
		return expDate;
	}
	public InvoiceFiscal setExpDate(Date expDate) {
		this.expDate = expDate;
		return this;
	}
	
	public EnumMap<VATTaxRegime,Boolean> getVatRegimes() {
		return vatRegimes;
	}
	public InvoiceFiscal setVatRegimes(EnumMap<VATTaxRegime,Boolean> vatRegimes) {
		this.vatRegimes = vatRegimes;
		return this;
	}
	
	public boolean isVatRegimeEnabled(VATTaxRegime vatRegime) {
		return (getVatRegimes() != null 
			&& getVatRegimes().get(vatRegime) != null
			&& Boolean.TRUE.equals( getVatRegimes().get(vatRegime) )); 
	}
	public InvoiceFiscal setVatRegime(VATTaxRegime vatRegime, boolean value) {
		getVatRegimes().put(vatRegime, value?Boolean.TRUE:Boolean.FALSE);
		return this;
	}
	
}

