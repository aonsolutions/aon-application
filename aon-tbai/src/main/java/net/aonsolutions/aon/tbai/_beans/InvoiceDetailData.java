package net.aonsolutions.aon.tbai._beans;

import java.util.Optional;

public class InvoiceDetailData {
	
	private String	description;					
	private Double  price;							
	private Double  unit_amount;					
	private Double 	discount;						
	private Double  total_amount;					
	
	public InvoiceDetailData(final String description, final Double price, final Double unit_amount, 
			final Double discount, final Double total_amount) {
		this.description 	= description;
		this.price 			= price;
		this.unit_amount 	= unit_amount;
		this.discount 		= discount;
		this.total_amount 	= total_amount;
	}
	
	public Optional<String>  getDescription() 							{return Optional.ofNullable(description);}
	public InvoiceDetailData setDescription(String description) 		{this.description = description; return this;}
	
	public Optional<Double>  getPrice() 								{return Optional.ofNullable(price);}
	public InvoiceDetailData setPrice(Double price) 					{this.price = price; return this;}
	
	public Optional<Double>  getUnitAmount() 							{return Optional.ofNullable(unit_amount);}
	public InvoiceDetailData setUnitAmount(Double detail_unit_amount) 	{this.unit_amount = detail_unit_amount; return this;}
	
	public Optional<Double>  getDiscount()								{return Optional.ofNullable(discount);}
	public InvoiceDetailData setDetailDiscount(Double detail_discount) 	{this.discount = detail_discount; return this;}
	
	public Optional<Double>  getTotalAmount() 							{return Optional.ofNullable(total_amount);}
	public InvoiceDetailData setTotalAmount(Double detail_total_amount) {this.total_amount = detail_total_amount; return this;}

	@Override
	public String toString() {
		return "InvoiceDetailData :\t\n{ \n\tdescription: \t\t" + description + ", \n\tprice: \t\t" + price
				+ ", \n\tunit_amount: \t\t" + unit_amount + ", \n\tdiscount: \t\t" + discount
				+ ", \n\ttotal_amount: \t\t" + total_amount + "\n}";
	}	
}
