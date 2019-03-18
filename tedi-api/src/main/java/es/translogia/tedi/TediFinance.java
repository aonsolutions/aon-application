package es.translogia.tedi;

import java.util.Date;

import org.json.JSONObject;

public class TediFinance {
	public TediFinance() {}

	public TediFinance(JSONObject json) {
		if(json != null) {
			this.iban = json.optString("iban");
			this.amount = json.optDouble("amount");
			this.dueDate = json.opt("due_date") != null ? TediDateUtils.parse(json.getString("due_date"), "dd/MM/yyyy") : null;
			this.status = json.optString("status");
			this.payMethod = json.optString("pay_method");
			this.pending = json.optBoolean("pending");
		} 
	}
	
	private String iban;
	private String status;
	private String payMethod;
	private Double amount;
	private Date dueDate;
	private Boolean pending;
	
	public String getIban() {
		return iban;
	}
	public TediFinance setIban(String iban) {
		this.iban = iban;
		return this;
	}
	public Double getAmount() {
		return amount;
	}
	public TediFinance setAmount(Double amount) {
		this.amount = amount;
		return this;
	}
	public Date getDueDate() {
		return dueDate;
	}
	public TediFinance setDueDate(Date dueDate) {
		this.dueDate = dueDate;
		return this;
	}

	public String getStatus() {
		return status;
	}

	public TediFinance setStatus(String status) {
		this.status = status;
		return this;
	}

	public String getPayMethod() {
		return payMethod;
	}

	public TediFinance setPayMethod(String payMethod) {
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
