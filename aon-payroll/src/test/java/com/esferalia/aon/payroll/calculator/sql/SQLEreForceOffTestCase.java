package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class SQLEreForceOffTestCase extends SQLERETestCase {
	@Override
	protected ContextVariable getDaysVariable() {
		return ContextVariable.ERE_DAYS_FORCE_OFF;
	}
	@Override
	protected ContextVariable getEreVariable() {
		return ContextVariable.ERE_FORCE_OFF;
	}
	
	@Override
	protected ContextVariable getFactorVariable() {
		return ContextVariable.ERE_FACTOR_FORCE_OFF;
	}
}
