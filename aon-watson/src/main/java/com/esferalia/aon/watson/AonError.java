package com.esferalia.aon.watson;


public enum AonError {
	 READ_FORBIDDEN("El acceso de lectura al recurso solicitado, ha sido denegado.")
	,WRITE_FORBIDDEN("El acceso de escritura al recurso solicitado, ha sido denegado.")
	
	// -----------------------------------------------------------
	// --------------------- ENUMERATION -------------------------
	// -----------------------------------------------------------
	, SECURITY_LEVEL_INVALID("Valor incorrecto para el enumerado 'Nivel de seguridad - Confidencial' ({0})")
	,ACCOUNT_PERIOD_TYPE_INVALID("Valor incorrecto para el enumerado 'Estado' ({0})")
	,ACCOUNT_ENTRY_TYPE_INVALID("Valor incorrecto para el enumerado 'Tipo de asiento' ({0})")
	// -----------------------------------------------------------
	// --------------------- ACCOUNTING --------------------------
	// -----------------------------------------------------------
	
	// ACCOUNT_PERIOD
	,ACCOUNT_PERIOD_EMPTY_INITIATION_DATE("La fecha de inicio del ejercicio es un dato obligatorio, no puede estar vacio.")
	,ACCOUNT_PERIOD_EMPTY_DEADLINE("La fecha fin del ejercicio es un dato obligatorio, no puede estar vacio.")
	,ACCOUNT_PERIOD_WRONG_RANGE("La fecha de inicio debe ser anterior a la fecha fin.")
	,ACCOUNT_PERIOD_START_OVERLAP("Solape con la fecha de inicio y el periodo {0}")
	,ACCOUNT_PERIOD_END_OVERLAP("Solape con la fecha fin y el periodo {0}")
	,ACCOUNT_PERIOD_UNKOWN_FOR_DATE("No se encuentra un ejercicio contable para la fecha {0,date,dd/MM/yyyy}")
	// ACCOUNT_ENTRY
	,ACCOUNT_ENTRY_DOMAIN("El dominio del asiento es un dato obligatorio, no puede estar vacio")
	,ACCOUNT_ENTRY_WRONG_DOMAIN("El ejericio del asiento no existe o no es válido para el dominio ({0}).")
	,ACCOUNT_ENTRY_EMPTY_DATE("La fecha del asiento es un dato obligatorio, no puede estar vacia")
	,ACCOUNT_ENTRY_EMPTY_PERIOD("El ejercicio del asiento es un dato obligatorio, no puede estar vacio")
	,ACCOUNT_ENTRY_EMPTY_TYPE("El tipo de asiento es un dato obligatorio, no puede estar vacio.")
	,ACCOUNT_ENTRY_DATE_IN_PERIOD("La Fecha del Asiento no está dentro del periodo asignado al ejercicio {0}")
	,ACCOUNT_ENTRY_PERIOD_INACTIVE("El Ejercicio {0} está inactivo.")
	,ACCOUNT_ENTRY_PERIOD_OPERATING("No se permite la introducción o modificación de asientos en el ejercicio {0}, porque ya se ha realizado el asiento de explotación.")
	,ACCOUNT_ENTRY_PERIOD_CLOSING("No se permite la introducción o modificación de asientos en el ejercicio {0} porque ya se ha realizado el asiento de cierre.")
	,ACCOUNT_ENTRY_EMPTY_CONCEPT("El concepto del apunte es un dato obligatorio, no puede estar vacio.")
	,ACCOUNT_ENTRY_EMPTY_ACCOUNT("La cuenta contable del apunte es un dato obligatorio, no puede estar vacio. (LINEA={0,number},CONCEPTO={1},DEBE={2,number},HABER={3,number})")
	,ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND("Cuenta contable no encontrada en el dominio del asiento. (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH("La cuenta contable debe ser de último nivel (9 dígitos). (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_ACCOUNT_INACTIVE("La cuenta contable está desactivada. (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_SALARY_NO_LINES("No se han definido líneas en el apunte de nóminas.")
	,ACCOUNT_ENTRY_SALARY_NO_ACCOUNT("Línea de apunte de nóminas sin cuenta contable y la cuenta asignada al tipo no se puede recuperar. (TIPO={0}, CANTIDAD={1,number}). Revise el valor del parámetro {2}.")
	// -----------------------------------------------------------
	// -----------------------------------------------------------
	,EMPTY_ENTERPRISE("No se ha indicado una empresa.")
	,EMPTY_DATE_FROM("No se ha indicado una fecha 'desde'.")
	,EMPTY_DATE_TO("No se ha indicado una fecha 'hasta'.")
	;
	
	
	private String msg;
	private AonError(String msg) {
		this.msg = msg;
	}
	
	public String getMessage() {
		return this.msg;
	}

}
