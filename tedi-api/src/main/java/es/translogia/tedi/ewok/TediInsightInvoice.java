package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Date;

public class TediInsightInvoice implements Serializable {

	private static final long serialVersionUID = 6858816497836464586L;
	
	private Date dates [];
	private TediNif nifs [];
	private Double amounts [];
	private String references [];
	
	
	public Date[] getDates() {
		return dates;
	}
	
	public TediInsightInvoice setDates(Date[] dates) {
		this.dates = dates;
		return this;
	}
	
	public TediNif[] getNifs() {
		return nifs;
	}
	
	public TediInsightInvoice setNifs(TediNif[] nifs) {
		this.nifs = nifs;
		return this;
	}
	
	public Double[] getAmounts() {
		return amounts;
	}
	
	public TediInsightInvoice setAmounts(Double[] amounts) {
		this.amounts = amounts;
		return this;
	}
	
	public String[] getReferences() {
		return references;
	}
	
	public TediInsightInvoice setReferences(String[] references) {
		this.references = references;
		return this;
	}

}
