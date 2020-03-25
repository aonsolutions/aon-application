package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class SQLEreForceTestCase extends SQLERETestCase {
	@Override
	protected ContextVariable getDaysVariable() {
		return ContextVariable.ERE_DAYS_FORCE;
	}
	@Override
	protected ContextVariable getEreVariable() {
		return ContextVariable.ERE_FORCE;
	}
	
	@Override
	protected ContextVariable getFactorVariable() {
		return ContextVariable.ERE_FACTOR_FORCE;
	}
	
}
