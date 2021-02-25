package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdown;

import java.util.Optional;

import ticketbai.emision.TipoOperacionSujetaNoExentaType;

public class TbaiNoExemptedBreakdown implements Breakdown {
	private TipoOperacionSujetaNoExentaType type;
	private Double 	tax_base;								//DECIMAL #{12}.##
	private Double 	tax_rate;								//DECIMAL #{3}.##
	private Double 	equivalence_recharge_amount;			//DECIMAL #{12}.##
	private Boolean equivalence_recharge_or_simplified;
	
	public TbaiNoExemptedBreakdown(TipoOperacionSujetaNoExentaType type,Double tax_base, Double tax_rate, Double equivalence_recharge_amount, Boolean equivalence_recharge_or_simplified) {
		this.type = type;
		this.tax_base = tax_base;
		this.tax_rate = tax_rate;
		this.equivalence_recharge_amount = equivalence_recharge_amount;
		this.equivalence_recharge_or_simplified = equivalence_recharge_or_simplified;
	}
	
	public Optional<TipoOperacionSujetaNoExentaType> getType() {return Optional.ofNullable(type);}
	public void setType(TipoOperacionSujetaNoExentaType type) {this.type = type;}
	
	public Optional<Double> getTax_base() {return Optional.ofNullable(tax_base);}
	public void setTax_base(Double tax_base) {this.tax_base = tax_base;}
	
	public Optional<Double> getTax_rate() {return Optional.ofNullable(tax_rate);}
	public void setTax_rate(Double tax_rate) {this.tax_rate = tax_rate;}
	
	public Optional<Double> getEquivalence_recharge_amount() {return Optional.ofNullable(equivalence_recharge_amount);}
	public void setEquivalence_recharge_amount(Double equivalence_recharge_amount) {this.equivalence_recharge_amount = equivalence_recharge_amount;}
	
	public Optional<Boolean> isEquivalence_recharge_or_simplified() {return Optional.ofNullable(equivalence_recharge_or_simplified);}
	public void setEquivalence_recharge_or_simplified(Boolean equivalence_recharge_or_simplified) {this.equivalence_recharge_or_simplified = equivalence_recharge_or_simplified;}				
		
}
