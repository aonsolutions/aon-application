package com.esferalia.aon.gwt.stat;

import java.io.Serializable;
import java.util.Date;

public class InvoiceStatParams implements Serializable {
	
	private static final long serialVersionUID = 4103540769966661072L;
	
	private Date from;
	private Date to;
	
	public Date getFrom() {
		return from;
	}
	public InvoiceStatParams setFrom(Date from) {
		this.from = from;
		return this;
	}
	public Date getTo() {
		return to;
	}
	public InvoiceStatParams setTo(Date to) {
		this.to = to;
		return this;
	}
	
	
}
