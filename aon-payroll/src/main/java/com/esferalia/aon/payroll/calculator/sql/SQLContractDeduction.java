package com.esferalia.aon.payroll.calculator.sql;

import java.sql.ResultSet;

import com.esferalia.aon.payroll.calculator.IContractDeduction;
import com.esferalia.aon.salary.enumeration.DeductionType;
import com.esferalia.aon.salary.expression.ExpressionScope;

public class SQLContractDeduction 
	extends SQLCollection<IContractDeduction> 
	implements IContractDeduction {

	public static final String TYPE 		= "type";
	public static final String CONCEPT 		= "concept";
	public static final String EXPRESSION 	= "expression";
	public static final String DESCRIPTION 	= "description";

	public SQLContractDeduction() {
		super();
	}
	
	protected SQLContractDeduction(ResultSet resultSet) {
		super(resultSet);
	}
	
	//-------------------------------------------
	// SQLCollection<IContractDeduction>
	//-------------------------------------------

	@Override
	public IContractDeduction next() {
		return this;
	}

	//-------------------------------------------
	// IContractDeduction
	//-------------------------------------------

	@Override
	public DeductionType getType() {
		int ordinal = getInt(TYPE);
		DeductionType type = 
			DeductionType.values()[ordinal];
		return type;
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

}
