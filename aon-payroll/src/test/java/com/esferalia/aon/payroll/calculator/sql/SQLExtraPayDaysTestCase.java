package com.esferalia.aon.payroll.calculator.sql;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

public class SQLExtraPayDaysTestCase extends SQLExtraTestCase {
	
	@Override
	protected ContextVariable getPeriodVariable() {
		return ContextVariable.PAY_DAYS;
	}
}
