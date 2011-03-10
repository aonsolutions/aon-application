package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.util.Date;

import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.master.sql.AbstractSQL.PaymentConcept;
import com.esferalia.aon.master.sql.SQLConstants;
import com.esferalia.aon.master.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.master.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractPayment extends SQLCollection<IContractPayment> implements IContractPayment {
	
	
	
	public SQLContractPayment() {
		super();
	}
	
	protected SQLContractPayment(ResultSet resultSet) {
		super(resultSet);
	}

	// ------------------------------------------
	// Iterator<IContractPayment>
	// ------------------------------------------
	
	
	@Override
	public IContractPayment next() {
		return this;
	}

	
	// ------------------------------------------
	// IContractPayment
	// ------------------------------------------
	
	@Override
	public Month getMonth() {
		Integer month = getInt(ContractPaymentColumns.MONTH);
		return  month == null ? null :Month.values()[month];
	}
	
	@Override
	public PaymentType getType() {
		int type = getInt(ContractPaymentColumns.TYPE);
		return PaymentType.values()[type];
	}

	@Override
	public String getDescription() {
		return getString(ContractPaymentColumns.DESCRIPTION);
	}

	@Override
	public String getExpression() {
		return getString(ContractPaymentColumns.EXPRESSION);
	}

	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return getString(SQLConstants.PAYMENT_CONCEPT + "." + PaymentConceptColumns.CODE);
	}

	@Override
	public ExpressionScope getScope() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String getIrpfExpression() {
		return getString(ContractPaymentColumns.IRPF_EXPRESSION);
	}
	
	@Override
	public String getQuoteExpression() {
		return getString(ContractPaymentColumns.QUOTE_EXPRESSION);
	}

	@Override
	public Date getStartDate() {
		return getDate(ContractPaymentColumns.START_DATE);
	}

	@Override
	public Date getEndDate() {
		return getDate(ContractPaymentColumns.END_DATE);
	}
	
	@Override
	public boolean isSalaryInKind() {
		return getType()==PaymentType.SALARY_IN_KIND;
	}
	
	@Override
	public SalaryType getSalaryType() {
		Integer type = getInt(ContractPaymentColumns.SALARY_TYPE);
		return type != null ? SalaryType.values()[type] : null;
	}
}
