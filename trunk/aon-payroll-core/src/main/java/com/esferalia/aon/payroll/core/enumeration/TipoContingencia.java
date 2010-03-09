package com.esferalia.aon.payroll.core.enumeration;

public enum TipoContingencia {
	
	// Enfermedad común
	ENFERMEDAD_COMUN,

	// Accidente laboral
	ACCIDENTE_LABORAL,

	// Accidente no laboral
	ACCIDENTE_NO_LABORAL,

	// Maternidad
	MATERNIDAD;

	public boolean isAccident() {
		return (this == ACCIDENTE_LABORAL || this == ACCIDENTE_NO_LABORAL);
	}
}
	