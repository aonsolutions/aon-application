package com.esferalia.aon.occam.api.model.finance;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class InvoiceCommunicationHistoryMapValue implements Serializable {
	
	private static final long serialVersionUID = 2396504472810555938L;
	
	private InvoiceInfo info;
	private List<InvoiceCommunicationHistory> history = new LinkedList<>();
	
	public InvoiceInfo getInfo() {
		return info;
	}
	public InvoiceCommunicationHistoryMapValue setInfo(InvoiceInfo info) {
		this.info = info;
		return this;
	}
	
	public List<InvoiceCommunicationHistory> getHistory() {
		return history;
	}

	public InvoiceCommunicationHistoryMapValue add(InvoiceCommunicationHistory t) {
		getHistory().add(t);
		return this;
	}
	
}

