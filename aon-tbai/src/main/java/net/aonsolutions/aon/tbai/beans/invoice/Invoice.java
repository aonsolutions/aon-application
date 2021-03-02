package net.aonsolutions.aon.tbai.beans.invoice;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import net.aonsolutions.aon.tbai.beans.invoice.parts.Entity;
import net.aonsolutions.aon.tbai.beans.invoice.parts.InvoiceDetailData;
import net.aonsolutions.aon.tbai.beans.invoice.parts.RectificationData;
import net.aonsolutions.aon.tbai.beans.invoice.parts.breakdowns.Breakdown;

public abstract class Invoice {
						
	private Entity 	 				sender;
	private List<Entity>			recievers;
	private Boolean 				multiple; 								
	
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
	private List<InvoiceDetailData> 	details;
	
	private Double 					total_amount;							
	private Double 					supported_retention;					
	private Double 					tax_base_cost;							
	private List<String>			id_keys;								

	private Breakdown 				breakdown;
	private String 					signature; 		
	
	public Optional<Entity> getSender() 												{return Optional.ofNullable(sender);}
	public Invoice setSender(final Entity sender) 										{this.sender = sender; 	return this;}
	
	public List<Entity> getRecievers() 													{return (recievers == null) ?  new ArrayList<>() : recievers;}
	public Invoice setRecievers(final List<Entity> recievers) 							{this.recievers = recievers; return this;}
	
	public Optional<Boolean> isMultiple() 												{return Optional.ofNullable(multiple);}
	public Invoice setMultiple(final Boolean multiple) 									{this.multiple = multiple;return this;}
	
	public Optional<String> getSeries() 												{return Optional.ofNullable(series);}
	public Invoice setSeries(final String series) 										{this.series = series;	return this;}
	
	public Optional<String> getNumber() 												{return Optional.ofNullable(number);}
	public Invoice setNumber(final String number) 										{this.number = number; return this;}
	
	public Optional<Date> getExpedition_date() 											{return Optional.ofNullable(expedition_date);}
	public Invoice setExpedition_date(final Date expedition_date) 						{this.expedition_date = expedition_date; return this;}
	
	public Optional<Boolean> isSimplified() 											{return Optional.ofNullable(simplified);}
	public Invoice setSimplified(final Boolean simplified) 								{this.simplified = simplified; return this;}

	public Optional<Boolean> isReplace_simplified() 									{return Optional.ofNullable(replace_simplified);}
	public Invoice setReplace_simplified(final Boolean replace_simplified) 				{this.replace_simplified = replace_simplified; return this;}
	
	public Optional<RectificationData> getRectification() 								{return Optional.ofNullable(rectification);}
	public Invoice setRectification(final RectificationData rectification) 				{this.rectification = rectification; return this;}
	
	public Optional<String> getReplaced_number() 										{return Optional.ofNullable(replaced_number);}
	public Invoice setReplaced_number(final String replaced_number) 					{this.replaced_number = replaced_number; return this;}
	
	public Optional<String> getReplaced_expedition_date() 								{return Optional.ofNullable(replaced_expedition_date);}
	public Invoice setReplaced_expedition_date(final String replaced_expedition_date) 	{this.replaced_expedition_date = replaced_expedition_date; return this;}
	
	public Optional<Date> getOperation_date() 											{return Optional.ofNullable(operation_date);}
	public Invoice setOperation_date(final Date operation_date) 						{this.operation_date = operation_date; return this;}
	
	public Optional<String> getDescription() 											{return Optional.ofNullable(description);}
	public Invoice setDescription(final String description) 							{this.description = description; return this;}
	
	public List<InvoiceDetailData> getDetails() 											{return (details == null) ?  new ArrayList<>() : details;}
	public Invoice setDetails(final List<InvoiceDetailData> details) 						{this.details = details; return this;}
	
	public Optional<Double> getTotal_amount() 											{return Optional.ofNullable(total_amount);}
	public Invoice setTotal_amount(final Double total_amount) 							{this.total_amount = total_amount; return this;}
	
	public Optional<Double> getSupported_retention() 									{return Optional.ofNullable(supported_retention);}
	public Invoice setSupported_retention(final Double supported_retention) 			{this.supported_retention = supported_retention; return this;}
	
	public Optional<Double> getTax_base_cost() 											{return Optional.ofNullable(tax_base_cost);}
	public Invoice setTax_base_cost(final Double tax_base_cost) 						{this.tax_base_cost = tax_base_cost; return this;}
	
	public List<String> getId_keys() 													{return (id_keys == null) ?  new ArrayList<>() : id_keys;}
	public Invoice setId_keys(final List<String> id_keys) 								{this.id_keys = id_keys; return this;}
	
	public Optional<Breakdown> getBreakdown() 											{return Optional.ofNullable(breakdown);}
	public Invoice setBreakdown(final Breakdown breakdown) 								{this.breakdown = breakdown; return this;}
	
	public Optional<String> getSignature() 												{return Optional.ofNullable(signature);}
	public Invoice setSignature(final String signature) 								{this.signature = signature; return this;}
	
}

