package com.esferalia.aon.watson;

import java.io.Serializable;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public enum AonError implements Serializable{
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED  
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF
	
	
	// -----------------------------------------------------------
	// --------------------- SECUROTY ----------------------------
	// -----------------------------------------------------------
	 READ_FORBIDDEN("El acceso de lectura al recurso solicitado, ha sido denegado.")
	,WRITE_FORBIDDEN("El acceso de escritura al recurso solicitado, ha sido denegado.")

	// -----------------------------------------------------------
	// --------------------- COMMON ------------------------------
	// -----------------------------------------------------------
	,EMPTY_ENTERPRISE("No se ha indicado una empresa.")
	,EMPTY_DATE_FROM("No se ha indicado una fecha 'desde'.")
	,EMPTY_DATE_TO("No se ha indicado una fecha 'hasta'.")
	,EMPTY_DOMAIN("El dominio es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_YEAR("El ejercicio es un dato obligatorio, no puede estar vac\u00EDo")
	,INVALID_YEAR("El ejercicio debe tener un valor real")
	,EMPTY_PERIOD("El periodo es un dato obligatorio, no puede estar vac\u00EDo")
	,WRONG_PERIOD("No se ha encontrado un periodo contable para la fecha \"{0}\"")
	,EMPTY_EPIGRAPH("El ep\u00EDgrafe es un dato obligatorio, no puede estar vac\u00EDo")
	,INVALID_LENGTH("La longitud del dato \"{0}\" no puede superar los {1} car\u00E1cteres")
	
	// -----------------------------------------------------------
	// --------------------- ENUMERATION -------------------------
	// -----------------------------------------------------------
	,SECURITY_LEVEL_INVALID("Valor incorrecto para el enumerado 'Nivel de seguridad - Confidencial' ({0})")
	,ACCOUNT_PERIOD_TYPE_INVALID("Valor incorrecto para el enumerado 'Estado' ({0})")
	,ACCOUNT_ENTRY_TYPE_INVALID("Valor incorrecto para el enumerado 'Tipo de asiento' ({0})")
	
	// -----------------------------------------------------------
	// --------------------- ACCOUNTING --------------------------
	// -----------------------------------------------------------
	
	// -------------------------------------------- -------ACCOUNT
	,ACCOUNT_EMPTY_CODE("La cuenta contable es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_EMPTY_DESCRIPTION("La descripci\u00F3n de la cuenta contable es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_INVALID_LENGTH("La longitud de la cuenta {0} es incorrecta, debe tener una longitud de 1, 2, 3, 4 \u00F3 9 caracteres. ")
	,ACCOUNT_DUPLICATED_CODE("No se puede crear/modificar la cuenta ({0}) porque ya existe una con el mismo c\u00F3digo ({1})")
	,ACCOUNT_NO_NUMERIC("La cuenta contable s\u00F3lo puede estar compuesta por caracteres num\u00E9ricos.")
	,ACCOUNT_LOW_LEVEL_NOT_PRESENT("Imposible crear/modificar cuenta. No existe cuenta correspondiente de nivel inferior.")
	
	// -------------------------------------------- ACCOUNT_PERIOD
	,ACCOUNT_PERIOD_EMPTY_INITIATION_DATE("La fecha de inicio del ejercicio es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_PERIOD_EMPTY_DEADLINE("La fecha fin del ejercicio es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_PERIOD_WRONG_RANGE("La fecha de inicio debe ser anterior a la fecha fin.")
	,ACCOUNT_PERIOD_START_OVERLAP("Solape con la fecha de inicio y el periodo {0}")
	,ACCOUNT_PERIOD_END_OVERLAP("Solape con la fecha fin y el periodo {0}")
	,ACCOUNT_PERIOD_UNKOWN_FOR_DATE("No se encuentra un ejercicio contable para la fecha {0,date,dd/MM/yyyy}")
	// --------------------------------------------- ACCOUNT_ENTRY
	,ACCOUNT_ENTRY_WRONG_DOMAIN("El ejercicio del asiento no existe o no es v\u00E1lido para el dominio ({0}).")
	,ACCOUNT_ENTRY_EMPTY_DATE("La fecha del asiento es un dato obligatorio, no puede estar vac\u00EDa")
	,ACCOUNT_ENTRY_EMPTY_PERIOD("El ejercicio del asiento es un dato obligatorio, no puede estar vac\u00EDo")
	,ACCOUNT_ENTRY_EMPTY_TYPE("El tipo de asiento es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_ENTRY_EMPTY_DETAILS("No se puede grabar un asiento contable sin l\u00EDneas.")
	,ACCOUNT_ENTRY_NO_SETTLED("No se puede grabar un asiento contable descuadrado.")
	,ACCOUNT_ENTRY_DATE_IN_PERIOD("La Fecha del Asiento no est\u00E1 dentro del periodo asignado al ejercicio {0}")
	,ACCOUNT_ENTRY_PERIOD_INACTIVE("El Ejercicio {0} est\u00E1 inactivo.")
	,ACCOUNT_ENTRY_PERIOD_OPERATING("No se permite la introducci\u00F3n, modificaci\u00F3n o borrado de asientos en el ejercicio {0}, porque ya se ha realizado el asiento de explotaci\u00F3n.")
	,ACCOUNT_ENTRY_PERIOD_CLOSING("No se permite la introducci\u00F3n, modificaci\u00F3n  o borrado de asientos en el ejercicio {0} porque ya se ha realizado el asiento de cierre.")
	,ACCOUNT_ENTRY_AUTOMATIC_ENTRY_DELETE("No se permite el borrado de asientos autom\u00E1ticos.")
	,ACCOUNT_ENTRY_EMPTY_CONCEPT("El concepto del apunte es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_ENTRY_OVERFLOW_CONCEPT("El concepto del apunte no puede tener más de 32 caracteres.")
	,ACCOUNT_ENTRY_EMPTY_ACCOUNT("La cuenta contable del apunte es un dato obligatorio, no puede estar vac\u00EDo. (LINEA={0,number},CONCEPTO={1},DEBE={2,number},HABER={3,number})")
	,ACCOUNT_ENTRY_NO_EXP_ACCOUNT("La cuenta contable de explotaci\u00F3nn es un dato obligatorio, no puede estar vac\u00EDa.")
	,ACCOUNT_ENTRY_NO_TAX_ACCOUNT("La cuenta contable de IVA / IRPF es un dato obligatorio, no puede estar vac\u00EDa.")
	,ACCOUNT_ENTRY_NOT_FOUND("Asiento contable no encontrado.")
	,ACCOUNT_ENTRY_ACCOUNT_NOT_FOUND("Cuenta contable no encontrada en el dominio del asiento. (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_ACCOUNT_INVALID_LENGTH("La cuenta contable debe ser de \u00FAltimo nivel (9 d\u00EDgitos). (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_ACCOUNT_INACTIVE("La cuenta contable est\u00E1 desactivada. (ID={0}, [{1} - {2}])")
	,ACCOUNT_ENTRY_SALARY_NO_LINES("No se han definido l\u00EDneas en el apunte de n\u00F3minas.")
	,ACCOUNT_ENTRY_SALARY_NO_ACCOUNT("L\u00EDnea de apunte de n\u00F3minas sin cuenta contable o la cuenta asignada al tipo no se puede recuperar. "
			+ "Revise la configuraci\u00F3n de los par\u00E1metros contables relativos a las cuentas por defecto de laboral. "
			+ "Dato: (TIPO={0}, CANTIDAD={1,number},P\u00C1RAMETRO={2})")
	// --------------------------------------------- FISCAL ACTIVITY
	,DUPLICATE_EPIGRAPH("Ya existe el ep\u00EDgrafe {1} en el a\u00F1o {0}")	
	// -----------------------------------------------------------
	// --------------------- FISCAL ------------------------------
	// -----------------------------------------------------------
	,FISCAL_NO_REPLACED_DECLARATION("No existe una declaraci\u00F3n a la que sustituir/complementar.")
	,FISCAL_DECLARATION_ALREADY_REPLACED("Ya existe una declaraci\u00F3n sustitutiva.")
	,FISCAL_DECLARATION_ALREADY_EXISTS("Ya existe una declaraci\u00F3n en el periodo.")
	// --------------------------------------------- PRODUCT
	,DUPLICATE_PRODUCT_CODE("Ya existe un Producto con el mismo C\u00F3digo {0}.")
	,DUPLICATE_PRODUCT_CODE_DOMAIN("Ya existe un Producto con el mismo C\u00F3digo en el Dominio: {0}.")
	,EMPTY_PRODUCT_CODE("El C\u00F3digo del Producto es un dato obligatorio, no puede estar vac\u00EDo.")
	,EMPTY_PRODUCT_NAME("El Nombre del Producto es un dato obligatorio, no puede estar vac\u00EDo.")
	,EMPTY_PRODUCT("El Producto es un dato obligatorio, no puede estar vac\u00EDo.")
	,EMPTY_TAG("La Etiqueta es un dato obligatorio, no puede estar vac\u00EDo.")
	,EXIST_PRODUCT("El Producto no existe en el mismo dominio.")
	,EXIST_TAG("La etiqueta no existe en el mismo dominio.")
	,DUPLICATE_PRODUCT_TAG("Ya existe un Producto con la misma etiqueta {0}.")
	,DUPLICATE_BARCODE("Ya existe un Producto con el mismo C\u00F3digo de Barras {0}.")
	,DUPLICATE_BARCODE_DOMAIN("Ya existe un Producto con el mismo C\u00F3digo de Barras en el Dominio {0}.")
	,DUPLICATE_DETAILS("Ya existe el Detalle {0}.")
	,DUPLICATE_SERIAL_NUMBER("Ya existe el n\u00FAmero de serie {0}.")
	,EMPTY_WORKPLACE("El Lugar de trabajo es un dato obligatorio, no puede estar vac\u00EDo")

	// --------------------------------------------- INVOICE
	,INVOICE_EMPTY_DATE("La fecha de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TAX_DATE("La fecha I.V.A. de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TYPE("El tipo de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_REGISTRY("El titular de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_SCOPE("El \u00E1mbito de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_DUPLICATED_SERIES_NUMBER("Ya existe una factura con esa Serie/N\u00FAmero.")
	,INVOICE_DUPLICATED_REFERENCE_CODE("Ya existe una factura del titular con ese N\u00FAmero de referencia.")
	,INVOICE_OPERATIONS_DEADLINE("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. ")
	,INVOICE_FIVE_YEARS("El a\u00F1o de la factura no es v\u00E1lido, es anterior cinco a\u00F1os al actual") 
	,INVOICE_EMPTY_SOURCE("No se ha indicado un origen (INVOICE_DETAIL.SOURCE) en la l\u00EDnea de factura (Error interno)")
	,INVOICE_EMPTY_WORKPLACE("No se ha indicado un centro de trabajo en la l\u00EDnea de factura.")
	,INVOICE_EMPTY_REFERENCE_CODE("No se ha indicado un n\u00FAmero de factura.")
	,INVOICE_EMPTY_TRANSACTION("No se ha indicado un tipo de transacci\u00F3n de factura (Nacional, Intracom., Extracom, ...")
	,INVOICE_NOT_FOUND("Factura no encontrada.")
	,INVOICE_RECTIFIED_NOT_FOUND("Factura rectificada no encontrada.")
	,INVOICE_INVALID_RECTIFICATION_DATA("Los datos necesarios para rectificar la factura, no son correctos.")
	,INVOICE_INVALID_RECTIFICATION_TYPE("No se permite rectificar una factura con el tipo {0}.")
	,INVOICE_CANT_DELETE_RECTIFIED("No es posible borrar la factura porque est\u00E1 rectificada. Borre primero la factura rectificativa.")
	// --------------------------------------------- FINANCE
	,FINANCE_NOT_FOUND("Vencimiento no encontrado.")	
	,FINANCE_CAN_NOT_BE_SETTLED("El vencimiento no se puede saldar, no est\u00E1 pendiente ni devuelto.")
	,FINANCE_CAN_NOT_BE_PAID("El vencimiento no se puede pagar, no est\u00E1 pendiente ni devuelto.")	
	,FINANCE_CAN_NOT_BE_RETURNED("El vencimiento no se puede devolver, no est\u00E1 pagado ni remesado.")
	,FINANCE_CAN_NOT_BE_UNDOING("El vencimiento no se dejar pendiente, no est\u00E1 saldado. {0}.")
	,FINANCE_AMOUNT_ZERO("El importe del vencimiento no puede ser cero.")	
	,FINANCE_EMPTY_SCOPE("No es posible encontrar un \u00E1mbito v\u00E1lido para el vencimiento.")
	,FINANCE_WRONG_IBAN_LENGTH("Longitud de IBAN incorrecta.")
	,FINANCE_WRONG_ACCOUNT_BANK("Cuenta Bancaria incorrecta.")
	,FINANCE_WRONG_IBAN("IBAN incorrecto.")
	,DELETE_STATUS_WRONG("No se permite el borrado de vencimientos que no est\u00E9n pendientes.")
	,FINANCE_TRACKING_WITHOUT_FINANCE("En el tracking no existe informaci\u00F3n sobre el vencimiento al que pertenece.")
	,FINANCE_TRACKING_LATER_TRACKINGS("No se puede borrar el movimiento, tiene movimientos posteriores.")
	,FINANCE_TRACKING_NO_BANK_ACCOUNT("No se puede pagar el vencimiento, no se ha indicado la cuenta contable del banco o caja.")
	,FINANCE_TRACKING_NO_REGISTRY_ACCOUNT("No se puede pagar el vencimiento, no se ha indicado la cuenta contable del titular del vencimiento.")
	,FINANCE_TRACKING_RECORDED("No se puede borrar el movimiento, est\u00E1 contabilizado y no tiene permisos de borrado.")
	,FINANCE_ENTRY_LATER_TRACKINGS("No se puede borrar el asiento, alguno de los vencimientos tiene movimientos posteriores.")
	,FINANCE_ENTRY_FROM_FBATCH("No se puede borrar el asiento. Procede de remesa, deshaga el apunte desde la pantalla de remesas.")
	,INVOICE_FINANCES_AMOUNT("La suma de los importes de los vencimientos no coincide con el total factura")
	;
	
	
	private String msg;
	private AonError(String msg) {
		this.msg = msg;
	}
	
	public String getMessage() {
		return this.msg;
	}
	
	public String format(Object...arguments) {
		return MessageFormat.format(msg, arguments);
	}

	public static void main(String[] args) {
		Date now = new Date();
		System.out.println( WRONG_PERIOD.format( new SimpleDateFormat("dd/MM/yyyy").format( now )) );
	}
}
