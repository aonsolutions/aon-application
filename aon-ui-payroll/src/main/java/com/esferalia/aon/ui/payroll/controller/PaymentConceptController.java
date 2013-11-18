package com.esferalia.aon.ui.payroll.controller;

import javax.faces.event.ActionEvent;

import org.mvel2.PropertyAccessException;
import org.mvel2.UnresolveablePropertyException;

import com.esferalia.aon.payroll.PaymentConcept;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.expression.ExpressionContext;

public class PaymentConceptController extends AbstractConceptController {

	private Throwable expressionException;
	private Throwable irpfExpressionException;
	private Throwable quoteExpressionException;

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		obtainIrpfType();
		obtainQuoteType();
	}

	// -------------------------------------------------------------------------

	public boolean isExpressionValid() {
		try {
			analyze(getPaymentConcept().getExpression());
			return true;
		} catch (Throwable e) {
			expressionException = e;
			return false;
		}
	}

	public String getExpressionErrorMessage() {
		return expressionException.getMessage();
	}

	public boolean isIrpfExpressionValid() {
		try {
			analyze(getPaymentConcept().getIrpfExpression());
			return true;
		} catch (Throwable e) {
			irpfExpressionException = e;
			return false;
		}
	}

	public String getIrpfExpressionErrorMessage() {
		return irpfExpressionException.getMessage();
	}

	public boolean isQuoteExpressionValid() {
		try {
			analyze(getPaymentConcept().getQuoteExpression());
			return true;
		} catch (Throwable e) {
			quoteExpressionException = e;
			return false;
		}
	}

	public String getQuoteExpressionErrorMessage() {
		return quoteExpressionException.getMessage();
	}

	public void onChangeExpression(ActionEvent event) {
	}
	
	
	// --------------------------------------------------------- Private Methods

	private void analyze(String expression) {
		try {
			ExpressionContext.analyze(expression);
		} catch ( PropertyAccessException e ){
			
		} catch ( UnresolveablePropertyException e ){
		}
	}

	private PaymentConcept getPaymentConcept() {
		return (PaymentConcept) getTo();
	}

	private void obtainQuoteType() {
		PaymentConcept pc = ((PaymentConcept) getTo());
		if (pc.getQuoteExpression() == null) {
			pc.setQuote(QuoteType.NO_QUOTE);
		} else if (pc.getQuoteExpression().equals(pc.getCode())) {
			pc.setQuote(QuoteType.QUOTE);
		} else if (pc.getQuoteExpression().equals(pc.ZERO_VALUE)) {
			pc.setQuote(QuoteType.NO_QUOTE);
		} else if (pc.getQuoteExpression().equals(pc.IPREM_FORMMULA)) {
			pc.setQuote(QuoteType.IPREM_EXCESS);
		} else {
			pc.setQuote(QuoteType.MANUAL);
		}
	}

	private void obtainIrpfType() {
		PaymentConcept pc = ((PaymentConcept) getTo());
		if (pc.getIrpfExpression() == null) {
			pc.setTaxation(TaxationType.NO_TAXED);
		} else if (pc.getIrpfExpression().equals(pc.getCode())) {
			pc.setTaxation(TaxationType.TAXED);
		} else if (pc.getIrpfExpression().equals(pc.ZERO_VALUE)) {
			pc.setTaxation(TaxationType.NO_TAXED);
		} else {
			pc.setTaxation(TaxationType.MANUAL);
		}
	}

}
