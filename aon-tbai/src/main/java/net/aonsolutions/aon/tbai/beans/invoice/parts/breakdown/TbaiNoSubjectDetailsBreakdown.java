package net.aonsolutions.aon.tbai.beans.invoice.parts.breakdown;

import java.util.Optional;

public class TbaiNoSubjectDetailsBreakdown implements Breakdown {
	
	private String details_cause;				    //L13
	private Double details_amount;					//DECIMAL #{12}.##
	
	public TbaiNoSubjectDetailsBreakdown(String details_cause, Double details_amount) {
		this.details_amount = details_amount;
		this.details_cause = details_cause;
	}

	public Optional<String> getDetails_cause() {return Optional.ofNullable(details_cause);}
	public void setDetails_cause(String details_cause) {this.details_cause = details_cause;}

	public Optional<Double> getDetails_amount() {return Optional.ofNullable(details_amount);}
	public void setDetails_amount(Double subject_details_amount) {this.details_amount = subject_details_amount;}
	
}
