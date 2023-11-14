package com.esferalia.aon.occam.api.model.finance;

import java.util.Date;

import com.esferalia.aon.occam.api.model.type.InvoiceType;

public class InvoiceAndRaw extends Invoice {
	private static final long serialVersionUID = 1L;
	Boolean isInbox;
	Integer mimeType;

	public Integer getMimeType() {
		return mimeType;
	}

	public InvoiceAndRaw setMimeType(Integer mimeType) {
		this.mimeType = mimeType;
		return this;
	}

	public Boolean getIsInbox() {
		return isInbox;
	}

	public InvoiceAndRaw setIsInbox(Boolean isInbox) {
		this.isInbox = isInbox;
		return this;
	}
	
	@Override
	public InvoiceAndRaw setId(Integer value) {
		super.setId(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setReferenceCode(String value) {
		super.setReferenceCode(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setRegistryName(String value) {
		super.setRegistryName(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setTotal(double value) {
		super.setTotal(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setType(InvoiceType value) {
		super.setType(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setIssueDate(Date value) {
		super.setIssueDate(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setRecorded(boolean value) {
		super.setRecorded(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setSeries(String value) {
		super.setSeries(value);
		return this;
	}
	
	@Override
	public InvoiceAndRaw setNumber(int value) {
		super.setNumber(value);
		return this;
	}
	
}
