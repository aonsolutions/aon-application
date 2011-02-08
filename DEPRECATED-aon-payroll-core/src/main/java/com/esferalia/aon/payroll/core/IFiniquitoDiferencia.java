package com.esferalia.aon.payroll.core;

import java.io.Serializable;
import java.util.Date;

public interface IFiniquitoDiferencia extends Serializable{

	Integer getId();
	void setId(Integer cdg);

	Date getFechaBaja();
	void setFechaBaja(Date fecbaj);

	Double getImporteVacaciones();
	void setImporteVacaciones(Double importeVacaciones);

	Integer getDiasVacaciones();
	void setDiasVacaciones(Integer diasVacaciones);

	Double getBaseContingenciasGenerales();
	void setBaseContingenciasGenerales(Double baseContingenciasGenerales);

	Double getBaseAccidentesTrabajo();
	void setBaseAccidentesTrabajo(Double baseAccidentesTrabajo);

	IEmpleado getEmpleado();
	void setEmpleado(IEmpleado empleado);

	
}
