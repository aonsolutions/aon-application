package com.esferalia.aon.payroll.core.nomina;

import java.io.Serializable;

import com.esferalia.aon.payroll.core.INomina;

public interface INominaCalculator extends Serializable{
	
	INominaDAO getNominaDAO();
	INomina calculateNomina(NominaParams params);
	

}
