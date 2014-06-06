package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;
import java.util.Date;

import com.code.aon.AonVersion;
import com.code.aon.common.enumeration.Month;
import com.esferalia.aon.payroll.calculator.IContractPayment;
import com.esferalia.aon.payroll.sql.SQLConstants;
import com.esferalia.aon.payroll.sql.SQLConstants.ContractPaymentColumns;
import com.esferalia.aon.payroll.sql.SQLConstants.PaymentConceptColumns;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractPayment extends SQLCollection<IContractPayment> implements IContractPayment {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	public static final String SCOPE_ALIAS = "scope";
	public static final String PAYMENT_ALIAS = "payment";
	
	
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
	public Integer getId() {
		return getInt(ContractPaymentColumns.ID);
	}
	
	@Override
	public ExpressionScope getScope() {
		Integer type = getInt(SCOPE_ALIAS);
		return type == null ? null : ExpressionScope.values()[type];
	}

	@Override
	public boolean isReadOnly() {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
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
	public SalaryType getSalaryType() {
		Integer type = getInt(ContractPaymentColumns.SALARY_TYPE);
		return type != null ? SalaryType.values()[type] : null;
	}
	
	@Override
	public Month getMonth() {
		Integer month = getInt(ContractPaymentColumns.MONTH);
		return  month == null ? null :Month.getMonthByValue(month);
	}
	
	@Override
	public PaymentType getType() {
		Integer type = getInt(ContractPaymentColumns.TYPE, 
				PaymentConceptColumns.TYPE);
		return type == null ? null : PaymentType.values()[type];
	}

	@Override
	public String getDescription() {
		return getString(ContractPaymentColumns.DESCRIPTION, 
				PaymentConceptColumns.DESCRIPTION);
	}

	@Override
	public String getExpression() {
		return getString(ContractPaymentColumns.EXPRESSION, 
				PaymentConceptColumns.EXPRESSION);
	}

	@Override
	public String getName() {
		return getString(PaymentConceptColumns.CODE);
	}
	
	@Override
	public Integer getConceptId() {
		return getInt(ContractPaymentColumns.PAYMENT_CONCEPT);
	}
	
	@Override
	public String getIrpfExpression() {
		return getString(ContractPaymentColumns.IRPF_EXPRESSION, 
				PaymentConceptColumns.IRPF_EXPRESSION);
	}
	
	@Override
	public String getQuoteExpression() {
		return getString(ContractPaymentColumns.QUOTE_EXPRESSION, 
				PaymentConceptColumns.QUOTE_EXPRESSION);
	}

	@Override
	public boolean isDescriptionDecorable() {
		Integer i = getInt(ContractPaymentColumns.DESCRIPTION_DECORABLE,PaymentConceptColumns.DESCRIPTION_DECORABLE); 
		return (i==1); 
	}

	public Integer getInt(String paymentColumn, String conceptColumn ) {
		return super.getInt(paymentColumn, SQLConstants.PAYMENT_CONCEPT + "." + conceptColumn );
	}

	public String getString(String paymentColumn, String conceptColumn ) {
		return super.getString(paymentColumn, SQLConstants.PAYMENT_CONCEPT + "." + conceptColumn );
	}
}
