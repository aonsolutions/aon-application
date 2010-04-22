package com.code.aon.accounting.summary;

public class Amounts {
	
	private Double debit;
	private Double credit;
	
	public Amounts(Double debit, Double credit) {
		this.debit = debit;
		this.credit = credit;
	}
	
	public Double getDebit() {
		return debit;
	}
	public void setDebit(Double debit) {
		this.debit = debit;
	}
	public Double getCredit() {
		return credit;
	}
	public void setCredit(Double credit) {
		this.credit = credit;
	}
	
	

}
