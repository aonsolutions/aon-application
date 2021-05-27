package net.aonsolutions.aon.tbai.emision._beans.breakdowns;

import java.util.Optional;

import ticketbai.emision.CausaNoSujetaType;

public class NoSubject {
	
	private CausaNoSujetaType details_cause;				    
	private Double details_amount;				
	
	public NoSubject(CausaNoSujetaType details_cause, Double details_amount) {
		this.details_amount = details_amount;
		this.details_cause = details_cause;
	}

	public Optional<CausaNoSujetaType> getDetails_cause() {return Optional.ofNullable(details_cause);}
	public void setDetails_cause(CausaNoSujetaType details_cause) {this.details_cause = details_cause;}

	public Optional<Double> getDetails_amount() {return Optional.ofNullable(details_amount);}
	public void setDetails_amount(Double subject_details_amount) {this.details_amount = subject_details_amount;}
	
}
