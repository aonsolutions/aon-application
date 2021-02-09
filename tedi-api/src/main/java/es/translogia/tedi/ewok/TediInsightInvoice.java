package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Date;

public class TediInsightInvoice implements Serializable {

	private static final long serialVersionUID = 6858816497836464586L;
	
	private Date dates[];
	private TediNif nifs[];
	private Double amounts[];
	private Date issueDate;
	private Double total;
	private boolean settledManually;
	
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

	public Date getIssueDate() {
		return issueDate;
	}

	public TediInsightInvoice setIssueDate(Date issueDate) {
		this.issueDate = issueDate;
		return this;
	}

	public Double getTotal() {
		return total;
	}

	public TediInsightInvoice setTotal(Double total) {
		this.total = total;
		return this;
	}
	
	
	public boolean isSettledManually() {
		return settledManually;
	}

	public TediInsightInvoice setSettledManually(boolean settledManually) {
		this.settledManually = settledManually;
		return this;
	}

	public boolean hasIssueDate() {
		return getIssueDate() != null;
	}
	
	public boolean hasTotal() {
		return getTotal() != null;
	}
}
