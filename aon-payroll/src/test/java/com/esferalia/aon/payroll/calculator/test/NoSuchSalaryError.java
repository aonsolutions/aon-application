/**
 * 
 */
package com.esferalia.aon.payroll.calculator.test;

import java.util.Date;

/**
 * @author rtrepiana
 *
 */
public class NoSuchSalaryError extends AssertionError {

	/**
	 * 
	 */
	public NoSuchSalaryError() {
	}

	/**
	 * @param message
	 */
	public NoSuchSalaryError(Integer contractId, String employeeDocument, Date start, Date end) {
		super(String.format("Not found salary for contract %d [%s] at %tD..%tD", 
				contractId, employeeDocument, start, end ));
	}


}
