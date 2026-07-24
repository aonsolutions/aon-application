package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

public class AccountingAmortization implements Serializable {

	private static final long serialVersionUID = 5316857988882611335L;
	
	private Amortization amortization;
	private AmortizationDetail detail;
	
	public Amortization getAmortization() {
		return amortization;
	}
	public AccountingAmortization setAmortization(Amortization amortization) {
		this.amortization = amortization;
		return this;
	}
	
	public AmortizationDetail getDetail() {
		return detail;
	}
	public AccountingAmortization setDetail(AmortizationDetail detail) {
		this.detail = detail;
		return this;
	}
	
}
