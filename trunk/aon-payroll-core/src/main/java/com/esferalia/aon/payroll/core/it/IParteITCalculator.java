package com.esferalia.aon.payroll.core.it;

import com.esferalia.aon.payroll.core.ITrabajo;
import com.esferalia.aon.payroll.core.nomina.CalculatorException;



public interface IParteITCalculator {

	boolean accept(IParteIT it,ITrabajo trabajo);
	
	Double getBaseRetribucionPeriodoAnterior(IParteIT it) throws CalculatorException;

	Integer getDiasPeriodoAnterior(IParteIT it) throws CalculatorException;

	Double getBaseReguladoraDiaria(IParteIT it) throws CalculatorException;

	Double getBaseDiariaContingenciasComunes(IParteIT it) throws CalculatorException;

	Double getBaseDiariaAccidentesTrabajo(IParteIT it) throws CalculatorException;

	Double getPrestacionDiaria60(IParteIT it) throws CalculatorException;

	Double getPrestacionDiaria75(IParteIT it) throws CalculatorException;

}
