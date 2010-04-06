package com.esferalia.aon.payroll.core.it;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.IEmpleado;

public interface IParteITDAO {
	
	void configure();
	
	Criteria getCriteria(ParteITParams params) throws PayrollException;

	int getCount(ParteITParams params) throws PayrollException;;
	List<IParteIT> getPartes( ParteITParams params) throws PayrollException;
	List<IParteIT> getPartes(ParteITParams params, int start, int count) throws PayrollException;
	IParteIT initialize(IEmpleado empleado) throws PayrollException;
	
	int validate(IParteIT parteIT);
	void calculate(IParteIT parteIT) throws PayrollException;
	void accept(IParteIT parteIT) throws PayrollException;
	
	List<IParteIT> getPartesEmpleado( IEmpleado empleado) throws PayrollException;
	IContrato getContrato(IParteIT parteIT) throws PayrollException;

	// Partes de Confimacion
	List<IParteConfirmacionIT> getPartesConfirmacion(IParteIT parteIT) throws PayrollException;
	IParteConfirmacionIT initialize(IParteIT  parte) throws PayrollException;
	void accept(IParteConfirmacionIT confirmacionParteIT) throws PayrollException;;
	
}
