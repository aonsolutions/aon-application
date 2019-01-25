package es.translogia.tedi;

import java.util.Date;

import org.json.JSONObject;

public class TediFinance {
	public TediFinance() {}

	public TediFinance(JSONObject json) {
		this.bankAccount = json.getString("bank_account");
		this.amount = json.getDouble("amount");
		this.dueDate = TediDateUtils.parse(json.getString("due_date"), "dd/MM/yyyy");
	}
	
	private String bankAccount;
	private String status;
	private String paymethod;
	private Double amount;
	private Date dueDate;
	
	public String getBankAccount() {
		return bankAccount;
	}
	public TediFinance setBankAccount(String bankAccount) {
		this.bankAccount = bankAccount;
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

	public String getPaymethod() {
		return paymethod;
	}

	public TediFinance setPaymethod(String paymethod) {
		this.paymethod = paymethod;
		return this;
	}
		
}
