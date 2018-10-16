package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

import com.esferalia.aon.watson.util.AonMathUtils;


public class AccountBalance implements Serializable {
	
	private static final long serialVersionUID = 2821084947789446457L;
	
	private double openingDebit;
	private double openingCredit;
	private double debitSum;
	private double creditSum;
	private double operatingDebit;
	private double operatingCredit;
	private boolean checked;
	
	public AccountBalance() {
		
	}
	public AccountBalance(int type, double debit, double credit) {
		if (type == 0) {
			this.openingDebit = debit;
			this.openingCredit = credit;
		} else if (type == 2) {
			this.operatingDebit = debit;
			this.operatingCredit = credit;
		} else {
			this.debitSum = debit;
			this.creditSum = credit;
		}
	}
	public double getOpeningDebit() {
		return openingDebit;
	}
	public double getOpeningCredit() {
		return openingCredit;
	}
	public double getDebitSum() {
		return debitSum;
	}
	public double getCreditSum() {
		return creditSum;
	}
	public double getOperatingDebit() {
		return operatingDebit;
	}
	public double getOperatingCredit() {
		return operatingCredit;
	}
	
	public boolean isChecked() {
		return checked;
	}
	public void setChecked(boolean checked) {
		this.checked = checked;
	}
	
	public double getDebitBalance() {
		return AonMathUtils.round((openingDebit+debitSum+operatingDebit)-(openingCredit+creditSum+operatingCredit));
	}
	public double getCreditBalance() {
		return AonMathUtils.round((openingCredit+creditSum+operatingCredit)-(openingDebit+debitSum+operatingDebit));
	}
	public double getDebitPyG() {
		return AonMathUtils.round((openingDebit+debitSum)-(openingCredit+creditSum));
	}
	public double getCreditPyG() {
		return AonMathUtils.round((openingCredit+creditSum)-(openingDebit+debitSum));
	}
	
	public void add(int type, double debit, double credit) {
		if (type == 0) {
			this.openingDebit = this.openingDebit + debit;
			this.openingCredit = this.openingCredit + credit;
		} else if (type == 2) {
			this.operatingDebit = this.operatingDebit + debit;
			this.operatingCredit = this.operatingCredit + credit;
		} else {
			this.debitSum = this.debitSum + debit;
			this.creditSum = this.creditSum + credit;
		}
	}
}
