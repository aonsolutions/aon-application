package net.aonsolutions.aon.tbai.beans.invoice.parts;

import java.util.Optional;

public class InvoiceDetails {
	
	private String	detail_description;						//ALPHANUMERIC 250
	private Double  detail_amount;							//DECIMAL 	   #{12}.00
	private Double  detail_unit_amount;						//DECIMAL 	   #{12}.00
	private Double 	detail_discount;						//DECIMAL 	   #{12}.00
	private Double  detail_total_amount;					//DECIMAL 	   #{12}.00
	
	public InvoiceDetails(String detail_description,Double detail_amount, Double detail_unit_amount, Double detail_discount, Double detail_total_amount) {
		
		this.detail_description = detail_description;
		this.detail_amount = detail_amount;
		this.detail_unit_amount = detail_unit_amount;
		this.detail_discount = detail_discount;
		this.detail_total_amount = detail_total_amount;
		
	}
	
	public Optional<String> getDetail_description() {return Optional.ofNullable(detail_description);}
	public void setDetail_description(String detail_description) {this.detail_description = detail_description;}
	
	public Optional<Double> getDetail_amount() {return Optional.ofNullable(detail_amount);}
	public void setDetail_amount(Double detail_amount) {this.detail_amount = detail_amount;}
	
	public Optional<Double> getDetail_unit_amount() {return Optional.ofNullable(detail_unit_amount);}
	public void setDetail_unit_amount(Double detail_unit_amount) {this.detail_unit_amount = detail_unit_amount;}
	
	public Optional<Double> getDetail_discount() {return Optional.ofNullable(detail_discount);}
	public void setDetail_discount(Double detail_discount) {this.detail_discount = detail_discount;}
	
	public Optional<Double> getDetail_total_amount() {return Optional.ofNullable(detail_total_amount);}
	public void setDetail_total_amount(Double detail_total_amount) {this.detail_total_amount = detail_total_amount;}

	@Override
	public String toString() {
		return "InvoiceDetails :\t\n{ \n\tdetail_description: \t\t" + detail_description + ", \n\tdetail_amount: \t\t"
				+ detail_amount + ", \n\tdetail_unit_amount: \t\t" + detail_unit_amount + ", \n\tdetail_discount: \t\t"
				+ detail_discount + ", \n\tdetail_total_amount: \t\t" + detail_total_amount + "\n}";
	}
	
	
}
