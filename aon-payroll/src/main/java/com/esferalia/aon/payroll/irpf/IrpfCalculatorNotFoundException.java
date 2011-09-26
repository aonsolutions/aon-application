package com.esferalia.aon.payroll.irpf;

import com.code.aon.common.AonException;
import com.code.aon.config.enumeration.Administration;

public class IrpfCalculatorNotFoundException extends IrpfException {
	
	private Administration administration;
	
	public IrpfCalculatorNotFoundException(Administration administration) {
		this.administration = administration;
	}

	public Administration getAdministration() {
		return administration;
	}

}
