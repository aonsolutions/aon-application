package com.esferalia.aon.payroll;

import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.calculator.IHasPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class DelegateContractPayment implements IContractPayment , IHasPayment<IContractPayment>{
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private IContractPayment contractPayment;
	
	public DelegateContractPayment(IContractPayment contractPayment) {
		this.contractPayment = contractPayment;
	}
	
	@Override
	public Integer getId() {
		return contractPayment.getId();
	}
	
	@Override
	public Integer getConceptId() {
		return contractPayment.getConceptId();
	}
	
	public ExpressionScope getScope() {
		return contractPayment.getScope();
	}

	public boolean isReadOnly() {
		return contractPayment.isReadOnly();
	}

	public PaymentType getType() {
		return contractPayment.getType();
	}

	public String getName() {
		return contractPayment.getName();
	}

	public double getAmount() {
		return contractPayment.getAmount();
	}

	public String getExpression() {
		return contractPayment.getExpression();
	}

	public String getDescription() {
		return contractPayment.getDescription();
	}

	public Month getMonth() {
		return contractPayment.getMonth();
	}

	public Date getStartDate() {
		return contractPayment.getStartDate();
	}

	public Date getEndDate() {
		return contractPayment.getEndDate();
	}

	public String getIrpfExpression() {
		return contractPayment.getIrpfExpression();
	}

	public String getQuoteExpression() {
		return contractPayment.getQuoteExpression();
	}

	public SalaryType getSalaryType() {
		return contractPayment.getSalaryType();
	}

	public boolean isDescriptionDecorable() {
		return contractPayment.isDescriptionDecorable();
	}
	
	
	@Override
	public IContractPayment getPayment() {
		return contractPayment;
	}
	
}
