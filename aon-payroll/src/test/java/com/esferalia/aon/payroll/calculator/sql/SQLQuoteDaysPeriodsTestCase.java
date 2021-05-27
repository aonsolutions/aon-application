/**
 * 
 */
package com.esferalia.aon.payroll.calculator.sql;

import static com.esferalia.aon.payroll.enumeration.ContextVariable.QUOTE_DAYS;

import com.esferalia.aon.payroll.enumeration.ContextVariable;

/**
 * @author rtrepiana
 *
 */
public class SQLQuoteDaysPeriodsTestCase extends SQLAbstractPeriodsTestCase {
	
	@Override
	public ContextVariable getDaysVariable() {
		return QUOTE_DAYS;
	}

}
