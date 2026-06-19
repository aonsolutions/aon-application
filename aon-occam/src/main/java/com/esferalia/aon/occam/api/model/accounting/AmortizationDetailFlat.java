package com.esferalia.aon.occam.api.model.accounting;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.AmortizationDetailStatus;

public class AmortizationDetailFlat implements Serializable {

	private static final long serialVersionUID = -2788114698849263524L;

	private boolean selected;
	
	private Amortization amortization;
	private AmortizationDetail  detail;
	
	public boolean isSelected() {
		return selected;
	}
	public AmortizationDetailFlat setSelected(boolean selected) {
		this.selected = selected;
		return this;
	}
	
	public Amortization getAmortization() {
		return amortization;
	}
	public AmortizationDetailFlat setAmortization(Amortization amortization) {
		this.amortization = amortization;
		return this;
	}
	public AmortizationDetail getDetail() {
		return detail;
	}
	public AmortizationDetailFlat setDetail(AmortizationDetail detail) {
		this.detail = detail;
		return this;
	}
	
	public boolean isPending() 		{ return getDetail().getStatus() == AmortizationDetailStatus.PENDING;}
	public boolean isBlocked() 		{ return getDetail().getStatus() == AmortizationDetailStatus.BLOCKED;}
	public boolean isScored() 		{ return getDetail().getStatus() == AmortizationDetailStatus.SCORED;}
	public boolean isNotScored() 	{ return !isScored();}

}
