package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;

public class PaymentConceptController extends AbstractConceptController {

	private static final String IPREM_FORMMULA = "EXCESO_IPREM";
	private static final String ZERO_VALUE = "0";
	
	private TaxationType taxation;
	private QuoteType quote;
	
	public TaxationType getTaxation() {
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	public QuoteType getQuote() {
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(quote==QuoteType.QUOTE){
			pc.setQuoteExpression(pc.getCode());
		}else if(quote==QuoteType.NO_QUOTE){
			pc.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			pc.setQuoteExpression(IPREM_FORMMULA);
		}
	}

	private void changeIrpfExpression() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(taxation==TaxationType.TAXED){
			pc.setIrpfExpression(pc.getCode());
		}else if(taxation==TaxationType.NO_TAXED){
			pc.setIrpfExpression(ZERO_VALUE);
		}
	}

	private void obtainQuoteType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getQuoteExpression()==null){
			quote=QuoteType.NO_QUOTE;
		} else if(pc.getQuoteExpression().equals(pc.getCode())){
			quote=QuoteType.QUOTE;
		} else if(pc.getQuoteExpression().equals(ZERO_VALUE)){
			quote=QuoteType.NO_QUOTE;
		} else if(pc.getQuoteExpression().equals(IPREM_FORMMULA)){
			quote=QuoteType.IPREM_EXCESS;
		} else {
			quote=QuoteType.MANUAL;
		}
	}
	
	private void obtainIrpfType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getIrpfExpression()==null){
			taxation=TaxationType.NO_TAXED;
		} else if(pc.getIrpfExpression().equals(pc.getCode())){
			taxation=TaxationType.TAXED;
		} else if(pc.getIrpfExpression().equals(ZERO_VALUE)){
			taxation=TaxationType.NO_TAXED;
		} else {
			taxation=TaxationType.MANUAL;
		}
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		obtainIrpfType();
		obtainQuoteType();		
	}
	
}
