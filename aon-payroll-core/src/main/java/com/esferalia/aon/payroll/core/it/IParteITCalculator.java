package com.esferalia.aon.payroll.core.it;



public interface IParteITCalculator {

	boolean accept(IParteIT it);
	
	Double calculateBaseRetribucionPeriodoAnterior(IParteIT it);

	Integer getDiasPeriodoAnterior(IParteIT it);

	Double getBaseReguladoraDiaria(IParteIT it);

	Double getBaseDiariaContingenciasComunes(IParteIT it);

	Double getBaseDiariaAccidentesTrabajo(IParteIT it);

	Double getPrestacionDiaria60(IParteIT it);

	Double getPrestacionDiaria75(IParteIT it);

}
