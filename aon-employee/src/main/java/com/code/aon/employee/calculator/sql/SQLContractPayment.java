package com.code.aon.employee.calculator.sql;

import java.sql.ResultSet;

import com.code.aon.common.enumeration.Month;
import com.code.aon.employee.calculator.IContractPayment;
import com.esferalia.aon.salary.enumeration.PaymentType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractPayment extends SQLCollection<IContractPayment> implements IContractPayment {
	
	
	public static final String TYPE 			= "type";
	public static final String MONTH 			= "month";
	public static final String CONCEPT 			= "concept";
	public static final String EXPRESSION 		= "expression";
	public static final String DESCRIPTION 		= "description";
	public static final String IRPF_EXPRESSION 	= "irpf_expression";
	public static final String QUOTE_EXPRESSION = "quote_expression";
	
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
		Integer month = getInt(MONTH);
		return  month == null ? null :Month.values()[month];
	}
	
	@Override
	public PaymentType getType() {
		int type = getInt(TYPE);
		return PaymentType.values()[type];
	}

	@Override
	public String getDescription() {
		return getString(DESCRIPTION);
	}

	@Override
	public String getExpression() {
		return getString(EXPRESSION);
	}

	@Override
	public double getAmount() {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getName() {
		return getString(CONCEPT);
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
		return getString(IRPF_EXPRESSION);
	}
	
	@Override
	public String getQuoteExpression() {
		return getString(QUOTE_EXPRESSION);
	}
	
}
