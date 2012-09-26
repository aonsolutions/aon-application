package com.esferalia.aon.payroll;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;

import com.esferalia.aon.entity.master.ContractPaymentDB;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.enumeration.QuoteType;
import com.esferalia.aon.payroll.enumeration.TaxationType;
import com.esferalia.aon.salary.expression.ExpressionScope;

@Entity
@Table(name="contract_payment")
public class ContractPayment extends ContractPaymentDB implements IContractPayment {
	
	private static final long serialVersionUID = 1L;
	
	public final String ZERO_VALUE = "0";
	public final String IPREM_FORMMULA = "EXCESO_IPREM";
	
	private TaxationType taxation;
	private QuoteType quote;
	
	@Override
	@Transient
	public double getAmount() {
		if (NumberUtils.isNumber(getExpression()) ) {
			return NumberUtils.toDouble(getExpression());	
		}
		return 0;
	}

	@Override
	@Transient
	public String getName() {
		return getPaymentConcept()==null?null:getPaymentConcept().getCode();
	}

	@Override
	@Transient
	public ExpressionScope getScope() {
		return ExpressionScope.CONTRACT;
	}

	@Transient
	public String getFullDescription() {
		return (getPaymentConcept() == null || StringUtils.isEmpty(getPaymentConcept().getCode()))?
				getDescription():
				getPaymentConcept().getCode()+ " - " + (StringUtils.isEmpty(getDescription())?getPaymentConcept().getDescription():
					getDescription());
	}
	
	@Override
	@Transient
	public boolean isReadOnly() {
		return false;
	}
	
	@Transient
	public TaxationType getTaxation() {
		if(taxation==null){
			taxation = obtainTaxationType();
		}
		return taxation;
	}

	public void setTaxation(TaxationType taxation) {
		this.taxation = taxation;
		changeIrpfExpression();
	}

	@Transient
	public QuoteType getQuote() {
		if(quote==null){
			quote = obtainQuoteType();
		}
		return quote;
	}

	public void setQuote(QuoteType quote) {
		this.quote = quote;
		changeQuoteExpression();
	}
	
	private void changeQuoteExpression() {
		if(quote==QuoteType.QUOTE){
			this.setQuoteExpression(this.getExpression());
		}else if(quote==QuoteType.NO_QUOTE){
			this.setQuoteExpression("0");
		}else if(quote==QuoteType.IPREM_EXCESS){
			this.setQuoteExpression(IPREM_FORMMULA);
//		}else if(quote==QuoteType.MANUAL){
//			this.setQuoteExpression(" ");
		} else {
			this.setQuoteExpression(null);
		}
	}

	private void changeIrpfExpression() {
		if(taxation==TaxationType.TAXED){
			this.setIrpfExpression(this.getExpression());
		}else if(taxation==TaxationType.NO_TAXED){
			this.setIrpfExpression(ZERO_VALUE);
//		}else if(taxation==TaxationType.MANUAL){
//			this.setIrpfExpression(" ");
		} else {
			this.setIrpfExpression(null);
		}
	}
	
	private QuoteType obtainQuoteType() {
		if(this.getQuoteExpression()==null) {
			return null;
		} else if(this.getQuoteExpression().equals(this.getExpression())){
			return QuoteType.QUOTE;
		} else if(this.getQuoteExpression().equals(ZERO_VALUE)){
			return QuoteType.NO_QUOTE;
		} else if(this.getQuoteExpression().equals(IPREM_FORMMULA)){
			return QuoteType.IPREM_EXCESS;
		} else {
			return QuoteType.MANUAL;
		}
	}
	
	private TaxationType obtainTaxationType() {
		if(this.getIrpfExpression()==null){
			return null;
		} else if(this.getIrpfExpression().equals(this.getExpression())){
			return TaxationType.TAXED;
		} else if(this.getIrpfExpression().equals(ZERO_VALUE)){
			return TaxationType.NO_TAXED;
		} else {
			return TaxationType.MANUAL;
		}
	}
	
}
