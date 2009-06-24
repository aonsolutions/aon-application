package com.code.aon.accounting;

import java.util.Date;

import com.code.aon.common.ITransferObject;
import com.code.aon.common.util.CommonUtil;

public class LoanFeeEntryHeader implements ITransferObject {

	private static final long serialVersionUID = -8273893100974866857L;

	private Date feeDate;
	private Loan loan;
	private String description;
	private double amortization;
	private double interest;

	public Date getFeeDate() {
		return feeDate;
	}
	public void setFeeDate(Date feeDate) {
		this.feeDate = feeDate;
	}

	public Loan getLoan() {
		return loan;
	}
	public void setLoan(Loan loan) {
		this.loan = loan;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public double getInterest() {
		return interest;
	}
	public void setInterest(double interest) {
		this.interest = interest;
	}

	public double getAmortization() {
		return amortization;
	}
	public void setAmortization(double amortization) {
		this.amortization = amortization;
	}

	public double getFee(){
		return CommonUtil.round( getAmortization() + getInterest());
	}
}