package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdown;

import java.util.Optional;

import ticketbai.emision.CausaExencionType;

public class TbaiExemptedBreakdown implements Breakdown {
	private CausaExencionType cause;    
	private Double tax_base;								//DECIMAL #{12}.##

	private Double tax_quota;								//DECIMAL #{12}.##
	private Double equivalence_recharge_quota;				//DECIMAL #{3}.##

	public TbaiExemptedBreakdown(CausaExencionType cause,Double tax_base, Double tax_quota, Double equivalence_recharge_quota) {
		this.cause = cause;
		this.tax_base = tax_base;
		this.tax_quota = tax_quota;
		this.equivalence_recharge_quota = equivalence_recharge_quota;
	}

	public Optional<CausaExencionType> getCause() {return Optional.ofNullable(cause);}
	public void setCause(CausaExencionType cause) {this.cause = cause;}

	public Optional<Double> getTax_base() {return Optional.ofNullable(tax_base);}
	public void setTax_base(Double tax_base) {this.tax_base = tax_base;}

	public Optional<Double> getTax_quota() {return Optional.ofNullable(tax_quota);}
	public void setTax_quota(Double tax_quota) {this.tax_quota = tax_quota;}

	public Optional<Double> getEquivalence_recharge_quota() {return Optional.ofNullable(equivalence_recharge_quota);}
	public void setEquivalence_recharge_quota(Double equivalence_recharge_quota) {this.equivalence_recharge_quota = equivalence_recharge_quota;}
	
}
