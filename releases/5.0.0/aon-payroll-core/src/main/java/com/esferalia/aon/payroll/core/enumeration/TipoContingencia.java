package com.esferalia.aon.payroll.core.enumeration;

public enum TipoContingencia {
	
	// Enfermedad comun
	ENFERMEDAD_COMUN,
	// Accidente laboral
	ACCIDENTE_LABORAL,
	// Accidente no laboral
	ACCIDENTE_NO_LABORAL,
	// Maternidad
	MATERNIDAD,
	// Riesgo Embarazo
	EMBARAZO,
	// Paternidad
	PATERNIDAD,
	// Riesgo Lactancia	
	LACTANCIA;
	
	public boolean isAccident() {
		return (this == ACCIDENTE_LABORAL || this == ACCIDENTE_NO_LABORAL);
	}
}
	