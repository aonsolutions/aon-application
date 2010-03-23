package com.esferalia.aon.payroll.core.it;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.ITrabajo;

public interface IParteITDAO {
	
	List<IParteIT> getPartesEmpleado( IEmpleado empleado) throws PayrollException;
	IParteIT initialize(IEmpleado empleado) throws PayrollException;
	Criteria getCriteria(ParteITParams params) throws PayrollException;
	int validate(IParteIT parteIT);
	void calculate(IParteIT parteIT,ITrabajo trabajo) throws PayrollException;
}
