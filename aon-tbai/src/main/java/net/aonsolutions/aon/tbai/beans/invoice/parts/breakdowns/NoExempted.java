package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns;

import java.util.Optional;

import ticketbai.emision.TipoOperacionSujetaNoExentaType;

public class NoExempted {
	private TipoOperacionSujetaNoExentaType type;
	private Double 	tax_base;								
	private Double 	tax_rate;								
	private Double 	tax_quote;
	private Double 	equivalence_recharge_amount;			
	private Boolean equivalence_recharge_or_simplified;
	private String 	equivalence_type;
	
	public NoExempted(TipoOperacionSujetaNoExentaType type,Double tax_base, Double tax_rate, Double tax_quote, Double equivalence_recharge_amount, String equivalence_type, Boolean equivalence_recharge_or_simplified) {
		this.type = type;
		this.tax_base = tax_base;
		this.tax_rate = tax_rate;
		this.equivalence_type = equivalence_type;
		this.equivalence_recharge_amount = equivalence_recharge_amount;
		this.equivalence_recharge_or_simplified = equivalence_recharge_or_simplified;
	}
	
	public Optional<TipoOperacionSujetaNoExentaType> getType() {return Optional.ofNullable(type);}
	public void setType(TipoOperacionSujetaNoExentaType type) {this.type = type;}
	
	public Optional<Double> getTax_base() {return Optional.ofNullable(tax_base);}
	public void setTax_base(Double tax_base) {this.tax_base = tax_base;}
	
	public Optional<Double> getTax_rate() {return Optional.ofNullable(tax_rate);}
	public void setTax_rate(Double tax_rate) {this.tax_rate = tax_rate;}
	
	public Optional<Double> getTax_quote() {return Optional.ofNullable(tax_quote);}
	public void setTax_quote(Double tax_quote) {this.tax_quote = tax_quote;}
	
	public Optional<Double> getEquivalence_recharge_amount() {return Optional.ofNullable(equivalence_recharge_amount);}
	public void setEquivalence_recharge_amount(Double equivalence_recharge_amount) {this.equivalence_recharge_amount = equivalence_recharge_amount;}
	
	public Optional<Boolean> isEquivalence_recharge_or_simplified() {return Optional.ofNullable(equivalence_recharge_or_simplified);}
	public void setEquivalence_recharge_or_simplified(Boolean equivalence_recharge_or_simplified) {this.equivalence_recharge_or_simplified = equivalence_recharge_or_simplified;}

	public Optional<String> getEquivalence_type() {return Optional.ofNullable(equivalence_type);}
	public void setEquivalence_type(String equivalence_type) {this.equivalence_type = equivalence_type;}				
		
	
}
