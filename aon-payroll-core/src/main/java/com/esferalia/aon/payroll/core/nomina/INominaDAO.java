package com.esferalia.aon.payroll.core.nomina;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;

public interface INominaDAO {
	
	void configure();
	
	INomina getNomina(NominaParams params) throws PayrollException;
	Criteria getCriteria(NominaParams params);
	
}
