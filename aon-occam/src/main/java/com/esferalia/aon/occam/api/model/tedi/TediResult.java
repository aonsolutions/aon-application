package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;

import es.translogia.tedi.ewok.TediInvoice;

public class TediResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	private TediInvoice tedi;
	private Invoice inv;
	private AccountingInvoice ai;

	public TediResult() {

	}

	public TediResult(TediInvoice tedi, AccountingInvoice ai) {
		this.tedi = tedi;
		this.ai = ai;
	}
	
	public TediResult(TediInvoice tedi, AccountingInvoice ai, Invoice inv) {
		this.tedi = tedi;
		this.ai = ai;
		this.inv = inv;
	}
	
	public String getUuid() {
		return getTedi() != null ? getTedi().getUuid() : null;
	}

	public TediInvoice getTedi() {
		return tedi;
	}

	public TediResult setTedi(TediInvoice tedi) {
		this.tedi = tedi;
		return this;
	}
	
	public Invoice getInv() {
		if(inv == null) {
			inv = new Invoice();
		}
		return inv;
	}
	
	public TediResult setInv(Invoice inv) {
		this.inv = inv;
		return this;
	}

	public AccountingInvoice getAccountingInvoice() {
		return ai;
	}
	public Invoice getInvoice() {
		return ai!=null?ai.getInvoice():null;
	}
	public TediResult setAon(AccountingInvoice aon) {
		this.ai = aon;
		return this;
	}
	
}
