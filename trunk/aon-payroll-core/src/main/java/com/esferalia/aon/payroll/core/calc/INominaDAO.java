package com.esferalia.aon.payroll.core.calc;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.impl.calc.NominaParams;

public interface INominaDAO {
	
	INomina getNomina(NominaParams params) throws PayrollException;

}
