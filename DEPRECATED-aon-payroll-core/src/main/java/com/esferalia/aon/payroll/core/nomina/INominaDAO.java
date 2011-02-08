package com.esferalia.aon.payroll.core.nomina;

import java.util.List;

import com.code.aon.ql.Criteria;
import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IBonificacion;
import com.esferalia.aon.payroll.core.IEmpleado;
import com.esferalia.aon.payroll.core.IFiniquito;
import com.esferalia.aon.payroll.core.IFiniquitoDiferencia;
import com.esferalia.aon.payroll.core.INomina;
import com.esferalia.aon.payroll.core.INominaDiferencia;

public interface INominaDAO {
	
	void configure();
	
	INomina getNomina(NominaParams params) throws PayrollException;
	List<INomina> getNominas(NominaParams params) throws PayrollException;
	Criteria getCriteria(NominaParams params);
	
	void accept(IBonificacion bonifacion) throws PayrollException;

	Criteria getCriteriaNominaDiferencia(NominaParams params);

	INominaDiferencia getNominaDiferencia(NominaParams params)
		throws PayrollException;
	List<INominaDiferencia> getNominasDiferencia(NominaParams params)
		throws PayrollException;

	IFiniquito getFiniquito(IEmpleado empleado) throws PayrollException;
	IFiniquitoDiferencia getFiniquitoDiferencia(IEmpleado empleado)
		throws PayrollException;


	
}
