package com.esferalia.aon.occam.api.model.tedi;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.AccountingInvoice;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.registry.AccountingRegistry;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

import es.translogia.tedi.ewok.TediInvoice;

public class TediResult implements Serializable {

	private static final long serialVersionUID = -1880851370528653257L;

	private boolean checked;
	private TediInvoice tedi;
	private AccountingInvoice ai;
	private LinkedList<TediError> messages = new LinkedList<TediError>();
	private LinkedList<AccountingRegistry> posibleRegistries;

	public TediResult() {

	}

	public TediResult(TediInvoice tedi, AccountingInvoice ai) {
		this.tedi = tedi;
		this.ai = ai;
	}
	
	public String getUuid() {
		return getTedi() != null ? getTedi().getUuid() : null;
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
	
	public LinkedList<AccountingRegistry> getPosibleRegistries() {
		return posibleRegistries;
	}
	public TediResult setPosibleRegistries(LinkedList<AccountingRegistry> posibleRegistries) {
		this.posibleRegistries = posibleRegistries;
		return this;
	}

	public LinkedList<TediError> getMessages() {
		return messages;
	}

	public void add(TediError error) {
		messages.add(error);
	}

	public TediLevel getMoreSeriousLevel() {
		TediLevel level  = null;
		if (getMessages() != null) {
			for (TediError error : getMessages()) {
				if (level == null || error.getLevel().ordinal() >  level.ordinal()) {
					level = error.getLevel();
				}
			}
		}
		return level;
	}
	
	public boolean isImportable() {
		TediLevel level = getMoreSeriousLevel();;
		return ( level == null || level.ordinal() < TediLevel.ERR.ordinal() );
	}
	public boolean isEmptyTicket() {
		return isEmpty() && tedi.isTicket();
	}
	public boolean isEmpty() {
		boolean empty =  (getInvoice().getRegistry() == null && (getInvoice().getDetails() == null || getInvoice().getDetails().size() == 0));
		return empty;
	}
	public boolean hasAttach() {
		return (getTedi() != null && getTedi().getFile() != null);
	}
	public boolean hasPDFAttach() {
		return hasAttach() && (AonStringUtils.equals(getTedi().getFile().getContentType(), MimeType.PDF.getName())); 
	}
	public boolean hasImageAttach() {
		return hasJPEGAttach() || hasPNGAttach(); 
	}
	private boolean hasJPEGAttach() {
		return hasAttach() && (AonStringUtils.equals(getTedi().getFile().getContentType(), MimeType.JPEG.getName())); 
	}
	private boolean hasPNGAttach() {
		return hasAttach() && (AonStringUtils.equals(getTedi().getFile().getContentType(), MimeType.PNG.getName())); 
	}
	
	public void clearMessages() {
		this.messages = new LinkedList<TediError>();		
	}
	
}
