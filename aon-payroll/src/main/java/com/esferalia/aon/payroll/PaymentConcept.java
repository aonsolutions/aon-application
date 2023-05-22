package com.esferalia.aon.payroll;


import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.annotations.Heritable;
import com.esferalia.aon.entity.master.PaymentConceptDB;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;

@Entity
@Table(name="payment_concept")
@Heritable
public class PaymentConcept extends PaymentConceptDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public final String IPREM_FORMMULA = "EXCESO_IPREM";
	public final String ZERO_VALUE = "0";
	
	private TaxationType taxation;
	private QuoteType quote;
	
	@Transient
	public TaxationType getTaxation() {
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	@Transient
	public QuoteType getQuote() {
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		if(quote==QuoteType.QUOTE){
			this.setQuoteExpression(this.getCode());
		}else if(quote==QuoteType.NO_QUOTE){
			this.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			this.setQuoteExpression(IPREM_FORMMULA);
		}
	}

	private void changeIrpfExpression() {
		if(taxation==TaxationType.TAXED){
			this.setIrpfExpression(this.getCode());
		}else if(taxation==TaxationType.NO_TAXED){
			this.setIrpfExpression(ZERO_VALUE);
		}
	}

}
