package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.HashMap;

public class InvoiceFiscal implements Serializable {
	
	private static final long serialVersionUID = -1911682283856222146L;
	
	private Integer id;
	private Integer domain;
	private Integer invoice;
	private HashMap<VATTaxRegime,Boolean> vatRegimes;
	
	
	public Integer getId() {
		return id;
	}
	public InvoiceFiscal setId(Integer id) {
		this.id = id;
		return this;
	}
	
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
	
	public HashMap<VATTaxRegime, Boolean> getVatRegimes() {
		return vatRegimes;
	}
	public InvoiceFiscal setVatRegimes(HashMap<VATTaxRegime, Boolean> vatRegimes) {
		this.vatRegimes = vatRegimes;
		return this;
	}
	
	private HashMap<VATTaxRegime, Boolean> ensureVatTaxRegime() {
		if ( getVatRegimes() == null) {
			setVatRegimes( new HashMap<>());
		}
		return getVatRegimes();
	}
	
	public boolean isVatRegimeEnabled(VATTaxRegime vatRegime) {
		return (getVatRegimes() != null 
			&& getVatRegimes().get(vatRegime) != null
			&& Boolean.TRUE.equals( getVatRegimes().get(vatRegime) )); 
	}
	public InvoiceFiscal setVatRegime(VATTaxRegime vatRegime, boolean value) {
		ensureVatTaxRegime().put(vatRegime, value?Boolean.TRUE:Boolean.FALSE);
		return this;
	}
	
}

