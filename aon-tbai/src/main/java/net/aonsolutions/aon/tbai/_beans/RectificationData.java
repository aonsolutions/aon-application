package net.aonsolutions.aon.tbai._beans;

import java.util.Optional;

public class RectificationData {
	private String 					 	code;
	private String 						type;								
	private Double 						import_tax_base;					
	private Double 						import_tax_quote;					
	private Double 						import_recharge_amount;     		
	private String 						series;	
	
	public RectificationData(final String code, final String type, final Double import_tax_base, final Double import_tax_quote,
			final Double import_recharge_amount, final String series) {
		this.code = code;
		this.type = type;
		this.import_tax_base = import_tax_base;
		this.import_tax_quote = import_tax_quote;
		this.import_recharge_amount = import_recharge_amount;
		this.series = series;
	}
	public Optional<String> getCode() 													{return Optional.ofNullable(code);}
	public RectificationData setCode(String code) 										{this.code = code; return this;}
	
	public Optional<String> getType() 													{return Optional.ofNullable(type);}
	public RectificationData setType(String type) 										{this.type = type; return this;}
	
	public Optional<Double> getImport_tax_base() 										{return Optional.ofNullable(import_tax_base);}
	public RectificationData setImport_tax_base(Double import_tax_base) 				{this.import_tax_base = import_tax_base;  return this;}
	
	public Optional<Double> getImport_tax_quote() 										{return Optional.ofNullable(import_tax_quote);}
	public RectificationData setImport_tax_quote(Double import_tax_quote) 				{this.import_tax_quote = import_tax_quote; return this;}
	
	public Optional<Double> getImport_recharge_amount() 								{return Optional.ofNullable(import_recharge_amount);}
	public RectificationData setImport_recharge_amount(Double import_recharge_amount) 	{this.import_recharge_amount = import_recharge_amount; return this;}
	
	public Optional<String> getSeries() 												{return Optional.ofNullable(series);}
	public RectificationData setSeries(String series) 									{this.series = series; return this;}		
}
