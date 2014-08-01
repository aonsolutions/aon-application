/**
 * 
 */
package com.esferalia.aon.payroll.calculator.test;

import java.util.Date;

import com.code.aon.AonVersion;

/**
 * @author rtrepiana
 *
 */
public class NoSuchSalaryError extends AssertionError {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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
