package com.esferalia.aon.payroll.core.it;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;

public interface IParteITDAO {
	
	List<IParteIT> getPartesEmpleado( IEmpleado empleado) throws PayrollException;
	IParteIT initialize(IEmpleado empleado) throws PayrollException;
	Criteria getCriteria(ParteITParams params) throws PayrollException;
}
