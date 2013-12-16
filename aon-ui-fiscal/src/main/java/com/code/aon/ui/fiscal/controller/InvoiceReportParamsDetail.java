package com.code.aon.ui.fiscal.controller;

import com.code.aon.config.enumeration.InvoiceTransactionType;
import com.code.aon.finance.enumeration.InvoiceType;

public class InvoiceReportParamsDetail {

    private InvoiceTransactionType transaction;
    private Boolean investment;
    private InvoiceType type;
    private Boolean surcharge;
    private Boolean service;
    private Boolean vatDeductionTypeWithoutRight;
    private Boolean rectificationTypeSpecial;
    private Double percent;
	
	public InvoiceTransactionType getTransaction() {
		return transaction;
	}
	public void setTransaction(InvoiceTransactionType transaction) {
		this.transaction = transaction;
	}

	public Boolean getInvestment() {
		return investment;
	}
	public void setInvestment(Boolean investment) {
		this.investment = investment;
	}
	
	public InvoiceType getType() {
		return type;
	}
	public void setType(InvoiceType type) {
		this.type = type;
	}
	
	public Boolean getSurcharge() {
		return surcharge;
	}
	public void setSurcharge(Boolean surcharge) {
		this.surcharge = surcharge;
	}
	
	public Boolean getService() {
		return service;
	}
	public void setService(Boolean service) {
		this.service = service;
	}
	
	public Boolean getVatDeductionTypeWithoutRight() {
		return vatDeductionTypeWithoutRight;
	}
	public void setVatDeductionTypeWithoutRight(Boolean vatDeductionTypeWithoutRight) {
		this.vatDeductionTypeWithoutRight = vatDeductionTypeWithoutRight;
	}
	
	public Boolean getRectificationTypeSpecial() {
		return rectificationTypeSpecial;
	}
	public void setRectificationTypeSpecial(Boolean rectificationTypeSpecial) {
		this.rectificationTypeSpecial = rectificationTypeSpecial;
	}
	
	public Double getPercent() {
		return percent;
	}
	public void setPercent(Double percent) {
		this.percent = percent;
	}
	
	public void reset() {
	    setTransaction(null);
	    setInvestment(null);
	    setType(null);
	    setSurcharge(null);
	    setService(null);
	    setVatDeductionTypeWithoutRight(null);
	    setRectificationTypeSpecial(null);
	    setPercent(null);
	}
}
