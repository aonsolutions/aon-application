package com.esferalia.aon.payroll.core.nomina;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.INomina;

public interface INominaDAO {
	
	INomina getNomina(NominaParams params) throws PayrollException;
	Criteria getCriteria(NominaParams params);
}
