package net.aonsolutions.aon.tbai.beans.invoice.parts;

import java.util.Optional;

public class RectificationData {
	private String 					 	code;
	private String 						type;								
	private Double 						import_tax_base;					
	private Double 						import_tax_quote;					
	private Double 						import_recharge_amount;     		
	private String 						series;
	
	public Optional<String> getCode() {return Optional.ofNullable(code);}
	public void setCode(String code) {this.code = code;}
	
	public Optional<String> getType() {return Optional.ofNullable(type);}
	public void setType(String type) {this.type = type;}
	
	public Optional<Double> getImport_tax_base() {return Optional.ofNullable(import_tax_base);}
	public void setImport_tax_base(Double import_tax_base) {this.import_tax_base = import_tax_base;}
	
	public Optional<Double> getImport_tax_quote() {return Optional.ofNullable(import_tax_quote);}
	public void setImport_tax_quote(Double import_tax_quote) {	this.import_tax_quote = import_tax_quote;}
	
	public Optional<Double> getImport_recharge_amount() {return Optional.ofNullable(import_recharge_amount);}
	public void setImport_recharge_amount(Double import_recharge_amount) {this.import_recharge_amount = import_recharge_amount;}
	
	public Optional<String> getSeries() {return Optional.ofNullable(series);}
	public void setSeries(String series) {this.series = series;}		
}
