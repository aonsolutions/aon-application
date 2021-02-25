package net.aonsolutions.aon.tbai.beans.invoice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.aonsolutions.aon.tbai.beans.invoice.parts.Entity;
import net.aonsolutions.aon.tbai.beans.invoice.parts.InvoiceDetails;
import net.aonsolutions.aon.tbai.beans.invoice.parts.RectificationData;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdown.Breakdown;
import ticketbai.emision.ClaveTipoRectificativaType;
import ticketbai.emision.EmitidaPorTercerosType;
import ticketbai.emision.SiNoType;

public abstract class Invoice {
						
	private Entity 	 				sender;
	private List<Entity>			recievers;
	private Boolean 				multiple; 								
	private Boolean				 	external;
	
	private String 	  				series;									
	private String 	  				number;									
	private Date	  				expedition_date;						
	private Boolean  				simplified;							
	private Boolean  				replace_simplified;
	
	private RectificationData 		rectification;						
	private String 					replaced_number;						
	private String   				replaced_expedition_date;	
	
	private Date 					operation_date;							
	private String 					description;							
	private List<InvoiceDetails> 	details;
	
	private Double 					total_amount;							
	private Double 					supported_retention;					
	private Double 					tax_base_cost;							
	private List<String>			id_keys;								

	private Breakdown 				breakdown;
	private String 					signature; 			//FIRMA ELECTRONICA TICKETBAI
	
	public Optional<Entity> getSender() {return Optional.ofNullable(sender);}
	public void setSender(Entity sender) {this.sender = sender;}
	
	public List<Entity> getRecievers() {return (recievers == null) ?  new ArrayList<>() : recievers;}
	public void setRecievers(List<Entity> recievers) {this.recievers = recievers;}
	
	public Optional<Boolean> isMultiple() {return Optional.ofNullable(multiple);}
	public void setMultiple(Boolean multiple) {this.multiple = multiple;}
	
	public Optional<Boolean> isExternal() {return Optional.ofNullable(external);}
	public void setExternal(Boolean external) {this.external = external;}
	
	public Optional<String> getSeries() {return Optional.ofNullable(series);}
	public void setSeries(String series) {this.series = series;}
	
	public Optional<String> getNumber() {return Optional.ofNullable(number);}
	public void setNumber(String number) {this.number = number;}
	
	public Optional<Date> getExpedition_date() {return Optional.ofNullable(expedition_date);}
	public void setExpedition_date(Date expedition_date) {this.expedition_date = expedition_date;}
	
	public Optional<Boolean> isSimplified() {return Optional.ofNullable(simplified);}
	public void setSimplified(Boolean simplified) {this.simplified = simplified;}
	
	public Optional<Boolean> isReplace_simplified() {return Optional.ofNullable(replace_simplified);}
	public void setReplace_simplified(Boolean replace_simplified) {this.replace_simplified = replace_simplified;}
	
	public Optional<RectificationData> getRectification() {return Optional.ofNullable(rectification);}
	public void setRectification(RectificationData rectification) {this.rectification = rectification;}
	
	public Optional<String> getReplaced_number() {return Optional.ofNullable(replaced_number);}
	public void setReplaced_number(String replaced_number) {this.replaced_number = replaced_number;}
	
	public Optional<String> getReplaced_expedition_date() {return Optional.ofNullable(replaced_expedition_date);}
	public void setReplaced_expedition_date(String replaced_expedition_date) {this.replaced_expedition_date = replaced_expedition_date;}
	
	public Optional<Date> getOperation_date() {return Optional.ofNullable(operation_date);}
	public void setOperation_date(Date operation_date) {this.operation_date = operation_date;}
	
	public Optional<String> getDescription() {return Optional.ofNullable(description);}
	public void setDescription(String description) {this.description = description;}
	
	public List<InvoiceDetails> getDetails() {return (details == null) ?  new ArrayList<>() : details;}
	public void setDetails(List<InvoiceDetails> details) {this.details = details;}
	
	public Optional<Double> getTotal_amount() {return Optional.ofNullable(total_amount);}
	public void setTotal_amount(Double total_amount) {this.total_amount = total_amount;}
	
	public Optional<Double> getSupported_retention() {return Optional.ofNullable(supported_retention);}
	public void setSupported_retention(Double supported_retention) {this.supported_retention = supported_retention;}
	
	public Optional<Double> getTax_base_cost() {return Optional.ofNullable(tax_base_cost);}
	public void setTax_base_cost(Double tax_base_cost) {this.tax_base_cost = tax_base_cost;}
	
	public List<String> getId_keys() {return id_keys;}
	public void setId_keys(List<String> id_keys) {this.id_keys = id_keys;}
	
	public Optional<Breakdown> getBreakdown() {return Optional.ofNullable(breakdown);}
	public void setBreakdown(Breakdown breakdown) {this.breakdown = breakdown;}
	
	public Optional<String> getSignature() {return Optional.ofNullable(signature);}
	public void setSignature(String signature) {this.signature = signature;}

	
	
}

