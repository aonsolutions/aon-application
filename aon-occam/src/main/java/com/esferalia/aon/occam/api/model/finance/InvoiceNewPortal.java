package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.occam.api.model.type.MimeType;

public class InvoiceNewPortal extends Invoice {
	private static final long serialVersionUID = 1L;
	MimeType mimeType;
	Double surchargeQuota;
	Double retentionPercentage;
	
	public Double getRetentionPercentage() {
		return retentionPercentage;
	}

	public InvoiceNewPortal setRetentionPercentage(Double value) {
		this.retentionPercentage = value;
		return this;
	}

	public Double getSurchargeQuota() {
		return this.surchargeQuota;
	}
	
	public InvoiceNewPortal setSurchargeQuota(Double value) {
		this.surchargeQuota = value;
		return this;
	}

	public MimeType getMimeType() {
		return mimeType;
	}

	public InvoiceNewPortal setMimeType(MimeType mimeType) {
		this.mimeType = mimeType;
		return this;
	}
	
	@Override
	public InvoiceNewPortal setId(Integer value) {
		super.setId(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setReferenceCode(String value) {
		super.setReferenceCode(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setRegistryName(String value) {
		super.setRegistryName(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setTotal(double value) {
		super.setTotal(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setType(InvoiceType value) {
		super.setType(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setIssueDate(Date value) {
		super.setIssueDate(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setRecorded(boolean value) {
		super.setRecorded(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setSeries(String value) {
		super.setSeries(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setNumber(int value) {
		super.setNumber(value);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setInvoiceInfo(InvoiceInfo invoiceInfo) {
		super.setInvoiceInfo(invoiceInfo);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setVatQuota(double vatQuota) {
		super.setVatQuota(vatQuota);
		return this;
	}
	
	@Override
	public InvoiceNewPortal setTaxableBase(double taxableBase) {
		super.setTaxableBase(taxableBase);
		return this;
	}
	
}
