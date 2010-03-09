package com.esferalia.aon.payroll.core.enumeration;

public enum ContingencyType {
	
	// Enfermedad común
	COMMON_DISEASE,

	// Accidente laboral
	INDUSTRIAL_ACCIDENT,

	// Accidente no laboral
	COMMON_ACCIDENT,

	// Maternidad
	MATERNITY;

	public boolean isAccident() {
		return (this == INDUSTRIAL_ACCIDENT || this == COMMON_ACCIDENT);
	}
}
	