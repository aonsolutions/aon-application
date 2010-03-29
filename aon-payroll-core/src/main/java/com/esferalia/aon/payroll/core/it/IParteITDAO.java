package com.esferalia.aon.payroll.core.it;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;

public interface IParteITDAO {
	
	int getCount(ParteITParams params) throws PayrollException;;
	List<IParteIT> getPartes( ParteITParams params) throws PayrollException;
	List<IParteIT> getPartes(ParteITParams params, int start, int count) throws PayrollException;
	List<IParteIT> getPartesEmpleado( IEmpleado empleado) throws PayrollException;
	List<IConfirmacionParteIT> getPartesConfirmacion(IParteIT parteIT) throws PayrollException;
	IParteIT initialize(IEmpleado empleado) throws PayrollException;
	IConfirmacionParteIT initialize(IParteIT  parte) throws PayrollException;
	Criteria getCriteria(ParteITParams params) throws PayrollException;
	IContrato getContrato(IParteIT parteIT) throws PayrollException;
	int validate(IParteIT parteIT);
	void calculate(IParteIT parteIT) throws PayrollException;
	void accept(IParteIT parteIT) throws PayrollException;
	void accept(IConfirmacionParteIT confirmacionParteIT) throws PayrollException;;
	
}
