package com.esferalia.aon.payroll.core.it;

import com.esferalia.aon.payroll.PayrollException;
import com.esferalia.aon.payroll.core.IContrato;
import com.esferalia.aon.payroll.core.enumeration.Periodicidad;



public interface IParteITCalculator {

	boolean accept(IParteIT it,IContrato contrato);
	
	Double getBaseRetribucionPeriodoAnterior(IParteIT it) throws PayrollException;

	Integer getDiasPeriodoAnterior(IParteIT it) throws PayrollException;

	Double getBaseReguladoraDiaria(IParteIT it) throws PayrollException;

	Double getBaseDiariaContingenciasComunes(IParteIT it) throws PayrollException;

	Double getBaseDiariaAccidentesTrabajo(IParteIT it) throws PayrollException;

	Double getPrestacionDiaria60(IParteIT it) throws PayrollException;

	Double getPrestacionDiaria75(IParteIT it) throws PayrollException;
	
	Periodicidad getProrrateoCotizacion(IParteIT it) throws PayrollException;

}
