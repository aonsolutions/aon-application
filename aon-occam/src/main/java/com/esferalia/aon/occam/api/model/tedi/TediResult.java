package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;

import es.translogia.tedi.ewok.TediInvoice;

public class TediResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	private boolean checked;
	private TediInvoice tedi;
	private AccountingInvoice ai;
	private LinkedList<TediParserError> messages = new LinkedList<TediParserError>();

	public TediResult() {

	}

	public TediResult(TediInvoice tedi, AccountingInvoice ai) {
		this.tedi = tedi;
		this.ai = ai;
	}
	
	public boolean isChecked() {
		return checked;
	}
	public TediResult setChecked(boolean checked) {
		this.checked = checked;
		return this;
	}
	
	public TediInvoice getTedi() {
		return tedi;
	}

	public TediResult setTedi(TediInvoice tedi) {
		this.tedi = tedi;
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


	public LinkedList<TediParserError> getMessages() {
		return messages;
	}

	public void add(TediParserError error) {
		messages.add(error);
	}

	public TediLevel getMoreSeriousLevel() {
		TediLevel level  = null;
		if (getMessages() != null) {
			for (TediParserError error : getMessages()) {
				if (level == null || error.getLevel().ordinal() >  level.ordinal()) {
					level = error.getLevel();
				}
			}
		}
		return level;
	}
	
	public boolean isImportable() {
		TediLevel level = getMoreSeriousLevel();;
		return ( level == null || level.ordinal() < TediLevel.WRN.ordinal() );
	}
	
}
