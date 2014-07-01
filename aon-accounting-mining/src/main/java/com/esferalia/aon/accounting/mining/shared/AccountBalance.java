package com.esferalia.aon.accounting.mining.shared;


public class AccountBalance {
	
	private double debit;
	private double credit;
	
	public AccountBalance(double debit, double credit) {
		this.debit = debit;
		this.credit = credit;
	}
	public double getDebit() {
		return debit;
	}
	public double getCredit() {
		return credit;
	}
	public double getDebitBalance() {
		return AccMiningUtils.round(debit-credit);
	}
	public double getCreditBalance() {
		return AccMiningUtils.round(credit-debit);
	}
	public void add(double debit, double credit) {
		this.debit = this.debit + debit;
		this.credit = this.credit + credit;
	}
	public void add(AccountBalance ab) {
		this.debit = this.debit + ab.getDebit();
		this.credit = this.credit + ab.getCredit();
	}
	
}
