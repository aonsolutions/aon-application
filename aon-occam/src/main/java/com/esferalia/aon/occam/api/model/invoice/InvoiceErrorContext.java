package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;

public class InvoiceErrorContext implements Serializable {

	private static final long serialVersionUID = -1772168986100300984L;

	private InvoiceErrorKey key;
	private Integer line;

	public InvoiceErrorContext() {
	}

	public InvoiceErrorContext(InvoiceErrorKey key) {
		this(key, null);
	}

	public InvoiceErrorContext(InvoiceErrorKey key, Integer line) {
		this.key = key;
		this.line = line;
	}

	public InvoiceErrorKey getKey() {
		return key;
	}

	public InvoiceErrorContext setKey(InvoiceErrorKey key) {
		this.key = key;
		return this;
	}

	public Integer getLine() {
		return line;
	}

	public InvoiceErrorContext setLine(Integer line) {
		this.line = line;
		return this;
	}

	@Override
	public String toString() {
		return key == null ? "" : key.toString() + (line == null ? "" : (" (" + line + ")"));
	}

}