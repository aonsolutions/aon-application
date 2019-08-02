package es.translogia.tedi.ewok;

import java.io.Serializable;
import java.util.Date;

public class TediFinance implements Serializable {

	private static final long serialVersionUID = -6448429311160948539L;
	
	private Date dueDate;
	private Double amount;
	private String iban;
	private TediPayMethod payMethod;
	private Boolean pending;

	public Date getDueDate() {
		return dueDate;
	}
	public TediFinance setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public Double getAmount() {
		return amount;
	}
	public TediFinance setAmount(Double amount) {
		this.amount = amount;
		return this;
	}

	public String getIban() {
		return iban;
	}
	public TediFinance setIban(String iban) {
		this.iban = iban;
		return this;
	}

	public TediPayMethod getPayMethod() {
		return payMethod;
	}
	public TediFinance setPayMethod(TediPayMethod payMethod) {
		this.payMethod = payMethod;
		return this;
	}

	public Boolean getPending() {
		return pending;
	}
	public TediFinance setPending(Boolean pending) {
		this.pending = pending;
		return this;
	}
}
