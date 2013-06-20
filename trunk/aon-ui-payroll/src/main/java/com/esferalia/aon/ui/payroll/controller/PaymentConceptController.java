package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;

public class PaymentConceptController extends AbstractConceptController {

	private void obtainQuoteType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getQuoteExpression()==null){
			pc.setQuote(QuoteType.NO_QUOTE);
		} else if(pc.getQuoteExpression().equals(pc.getCode())){
			pc.setQuote(QuoteType.QUOTE);
		} else if(pc.getQuoteExpression().equals(pc.ZERO_VALUE)){
			pc.setQuote(QuoteType.NO_QUOTE);
		} else if(pc.getQuoteExpression().equals(pc.IPREM_FORMMULA)){
			pc.setQuote(QuoteType.IPREM_EXCESS);
		} else {
			pc.setQuote(QuoteType.MANUAL);
		}
	}
	
	private void obtainIrpfType() {
		PaymentConcept pc = ((PaymentConcept)getTo());
		if(pc.getIrpfExpression()==null){
			pc.setTaxation(TaxationType.NO_TAXED);
		} else if(pc.getIrpfExpression().equals(pc.getCode())){
			pc.setTaxation(TaxationType.TAXED);
		} else if(pc.getIrpfExpression().equals(pc.ZERO_VALUE)){
			pc.setTaxation(TaxationType.NO_TAXED);
		} else {
			pc.setTaxation(TaxationType.MANUAL);
		}
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		obtainIrpfType();
		obtainQuoteType();		
	}
	
}
