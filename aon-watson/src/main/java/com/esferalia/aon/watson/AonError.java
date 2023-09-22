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
	,EMPTY_STATUS("El estado es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_SCOPE("El \u00E1mbito es un dato obligatorio, no puede estar vac\u00EDa")
	,EMPTY_NAME("El nombre es un dato obligatorio, no puede estar vac\u00EDa")
	,EMPTY_YEAR("El ejercicio es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_DATA("El dato \"{0}\" es obligatorio, no puede estar vac\u00EDo")
	,INVALID_YEAR("El ejercicio debe tener un valor real")
	,EMPTY_PERIOD("El periodo es un dato obligatorio, no puede estar vac\u00EDo")
	,WRONG_PERIOD("No se ha encontrado un periodo contable para la fecha \"{0}\"")
	,EMPTY_EPIGRAPH("El ep\u00EDgrafe es un dato obligatorio, no puede estar vac\u00EDo")
	,INVALID_LENGTH("La longitud del dato \"{0}\" no puede superar los {1} car\u00E1cteres")
	,INVALID_FORMAT("El formato del dato \"{0}\" no es correcto: \"{1}\"")
	
	// -----------------------------------------------------------
	// --------------------- ENUMERATION -------------------------
	// -----------------------------------------------------------
	,SECURITY_LEVEL_INVALID("Valor incorrecto para el enumerado 'Nivel de seguridad - Confidencial' ({0})")
	,ACCOUNT_ENTRY_TYPE_INVALID("Valor incorrecto para el enumerado 'Tipo de asiento' ({0})")
	
	// -----------------------------------------------------------
	// --------------------- REGISTRY --------------------------
	// -----------------------------------------------------------
	,REGISTRY_OVERFLOW_DOCUMENT("El documento no puede tener m\u00E1s de 16 caracteres.")
	
	// -----------------------------------------------------------
	// --------------------- COMPANY ----------------------------
	// -----------------------------------------------------------
	,DUPLICATED_COMPANY_ROW("Ya existe el deto de la compa\u00F1ia para el dominio.")

	// -----------------------------------------------------------
	// --------------------- ACCOUNTING --------------------------
	// -----------------------------------------------------------
	
	// -------------------------------------------- -------ACCOUNT
	,ACCOUNT_EMPTY_CODE("La cuenta contable es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_EMPTY_DESCRIPTION("La descripci\u00F3n de la cuenta contable es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_INVALID_LENGTH("La longitud de la cuenta {0} es incorrecta, debe tener una longitud de 1, 2, 3, 4 \u00F3 9 caracteres. ")
	,ACCOUNT_DUPLICATED_CODE("No se puede crear/modificar la cuenta ({0}) porque ya existe una con el mismo c\u00F3digo ({1})")
	,ACCOUNT_NO_NUMERIC("La cuenta contable s\u00F3lo puede estar compuesta por caracteres num\u00E9ricos.")
	,ACCOUNT_LOW_LEVEL_NOT_PRESENT("Imposible crear/modificar cuenta. No existe cuenta correspondiente de nivel inferior. ({0})")
	,ACCOUNT_LOW_LEVEL_TOO_LOW("Imposible crear/modificar cuenta. No existe cuenta correspondiente del nivel m\u00EDnimo admitido ({0})")
	,ACCOUNT_PARENT_ACCOUNT("No se puede borrar una cuenta perteneciente al entorno padre.")
	,ACCOUNT_HIGH_LEVEL_PRESENT("No se puede borrar una cuenta con desglose de cuentas.")
	
	,ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_ACCOUNT("No se puede borrar una cuenta con referencias en asientos contables.")
	,ACCOUNT_PRESENT_IN_ACCOUNT_ENTRY_DETAIL_BAL_ACCOUNT("No se puede borrar una cuenta con referencias en contrapartidas de asientos contables.")
	,ACCOUNT_PRESENT_IN_AMORTIZATION_ACCOUNT("No se puede borrar una cuenta con referencias en fichas de amortizaci\u00F3n.")
	,ACCOUNT_PRESENT_IN_BANK_CONCEPT_ACCOUNT("No se puede borrar una cuenta con referencias en conceptos bancarios.")
	,ACCOUNT_PRESENT_IN_CREDITOR_ACCOUNT("No se puede borrar una cuenta vinculada a un acreedor.")
	,ACCOUNT_PRESENT_IN_CUSTOMER_ACCOUNT("No se puede borrar una cuenta vinculada a un cliente.")
	,ACCOUNT_PRESENT_IN_SUPPLIER_ACCOUNT("No se puede borrar una cuenta vinculada a un proveedor.")
	,ACCOUNT_PRESENT_IN_INVOICE_DETAIL_ACCOUNT("No se puede borrar una cuenta vinculada l\u00EDneas de facturas.")
	,ACCOUNT_PRESENT_IN_INVOICE_DUA_ACCOUNT("No se puede borrar una cuenta vinculada a una factura con DUA.")
	,ACCOUNT_PRESENT_IN_INVOICE_TAX_ACCOUNT("No se puede borrar una cuenta vinculada a impustos de facturas.")
	,ACCOUNT_PRESENT_IN_LOAN_ACCOUNT("No se puede borrar una cuenta vinculada a un pr\u00E9stamo.")
	,ACCOUNT_PRESENT_IN_PM_TYPE_ACCOUNT("No se puede borrar una cuenta vinculada a un tipo de forma de pago.")
	,ACCOUNT_PRESENT_IN_PRODUCT_ACCOUNT("No se puede borrar una cuenta vinculada a un producto.")
	,ACCOUNT_PRESENT_IN_RBANK_ACCOUNT("No se puede borrar una cuenta vinculada a un banco.")
	,ACCOUNT_PRESENT_IN_TAX_ACCOUNT("No se puede borrar una cuenta vinculada a un impuesto.")

	// -------------------------------------------- ACCOUNT_PERIOD
	,ACCOUNT_PERIOD_EMPTY_INITIATION_DATE("La fecha de inicio del ejercicio es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_PERIOD_EMPTY_DEADLINE("La fecha fin del ejercicio es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_PERIOD_WRONG_RANGE("La fecha de inicio debe ser anterior a la fecha fin.")
	,ACCOUNT_PERIOD_START_OVERLAP("Solape con la fecha de inicio y el periodo {0}")
	,ACCOUNT_PERIOD_END_OVERLAP("Solape con la fecha fin y el periodo {0}")
	,ACCOUNT_PERIOD_UNKOWN_FOR_DATE("No se encuentra un ejercicio contable para la fecha {0,date,dd/MM/yyyy}")
	,ACCOUNT_PERIOD_HAS_ENTRIES("No se puede borrar el ejercicio contable porque existen {0} apuntes vinculados")
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
	,ACCOUNT_ENTRY_BANK_STATEMENT_BOUND("Apunte vinculado a un extracto bancario")
	,ACCOUNT_ENTRY_EMPTY_CONCEPT("El concepto del apunte es un dato obligatorio, no puede estar vac\u00EDo.")
	,ACCOUNT_ENTRY_OVERFLOW_CONCEPT("El concepto del apunte no puede tener m\u00E1s de 32 caracteres.")
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
	,FISCAL_SUITABLE_DECLARATION("No hay declaraci\u00F3n disponible para: {0} {1} {2}")
	,FISCAL_NO_REPLACED_DECLARATION("No existe una declaraci\u00F3n a la que sustituir/complementar.")
	,FISCAL_DECLARATION_ALREADY_REPLACED("Ya existe una declaraci\u00F3n sustitutiva.")
	,FISCAL_DECLARATION_ALREADY_EXISTS("Ya existe una declaraci\u00F3n en el periodo \"Modelo {0}\"")
	,FISCAL_WRONG_STATUS_CHANGE("La declaraci\u00F3n no puede cambiar de estado \"{0}\" a estado \"{1}\"")
	,FISCAL_WRONG_STATUS_DELETION("No se puede borrar una declaraci\u00F3n en estado \"{0}\"")
	,FISCAL_WRONG_REPLACED_DELETION("No se puede borrar la declaraci\u00F3n. Existen declaraciones que complementano sustituyen a la que se pretende borrar.")

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

	// --------------------------------------------- BRAND
	, BRAND_NULL("El valor de la marca es nulo.")
	, BRAND_EMPTY("El valor de la marca esta vacío.")
	, BRAND_REPEAT("Ya existe una marca con el mismo nombre.")
	
	
	// --------------------------------------------- AMORTIZATION TYPE
	,AMORTIZATION_TYPE_NULL("El tipo de amortizacion es nulo.")
	,AMORTIZATION_TYPE_NULL_DESCRIPTION("La descripcion del tipo de amortizacion es nula")
	,AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT("El valor de la cuenta fija de activos es nulo")
	,AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT("El valor de la cuenta acumulada es nulo")
	,AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT("El valor de la cuenta de asignacion es nulo")	  
	// --------------------------------------------- INVOICE
	,INVOICE_EMPTY_DATE("La fecha de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TAX_DATE("La fecha I.V.A. de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TYPE("El tipo de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_REGISTRY("El titular de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_SCOPE("El \u00E1mbito de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_DUPLICATED_SERIES_NUMBER("Ya existe una factura con esa Serie/N\u00FAmero.")
	,INVOICE_DUPLICATED_REFERENCE_CODE("Ya existe una factura del titular con ese N\u00FAmero de referencia.")
	,INVOICE_OPERATIONS_DEADLINE("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. ")
	,INVOICE_TEN_YEARS("El a\u00F1o de la factura no es v\u00E1lido, es anterior diez a\u00F1os al actual") 
	,INVOICE_EMPTY_SOURCE("No se ha indicado un origen (INVOICE_DETAIL.SOURCE) en la l\u00EDnea de factura (Error interno)")
	,INVOICE_EMPTY_WORKPLACE("No se ha indicado un centro de trabajo en la l\u00EDnea de factura.")
	,INVOICE_EMPTY_REFERENCE_CODE("No se ha indicado un n\u00FAmero de factura.")
	,INVOICE_EMPTY_TRANSACTION("No se ha indicado un tipo de transacci\u00F3n de factura (Nacional, Intracom., Extracom, ...")
	,INVOICE_NOT_FOUND("Factura no encontrada.")
	,INVOICE_RECTIFIED_NOT_FOUND("Factura rectificada no encontrada.")
	,INVOICE_INVALID_RECTIFICATION_DATA("Los datos necesarios para rectificar la factura, no son correctos.")
	,INVOICE_INVALID_RECTIFICATION_TYPE("No se permite rectificar una factura con el tipo {0}.")
	,INVOICE_CANT_DELETE_RECTIFIED("No es posible borrar la factura porque est\u00E1 rectificada. Borre primero la factura rectificativa.")
	,INVOICE_CANT_DELETE_DUA_LINKED("No es posible borrar la factura porque est\u00E1 vinculada a un documento DUA.")
	,INVOICE_CANT_DELETE_SII("No es posible borrar la factura porque est\u00E1 enviada al SII. Dar de baja la factura en el SII.")
	,INVOICE_CANT_DELETE_TBAI("No es posible borrar la factura porque est\u00E1 enviada a Ticket Bai. Dar de baja la factura en Ticket Bai.")
	,INVOICE_CANT_DELETE_MODEL("Imposible borrar o modificar, la factura ha sido declarada en modelos fiscales: {0}")
	,TRACKING_CANT_DELETE_MODEL("Imposible borrar o modificar, el vto. ha sido declarada en modelos fiscales: {0}") 

	// --------------------------------------------- DUA
	,INVOICE_DUA_NATIONAL_INVOICE_EMPTY("No se ha indicado una factura nacional tipo DUA")
	,INVOICE_DUA_IMPORT_INVOICE_EMPTY("No se ha indicado una factura de importaci\u00F3n")
	,INVOICE_DUA_INFO_EMPTY("No se ha indicado la informaci\u00F3n necesaria en un factura DUA")
	,INVOICE_DUA_DUTY_ACCOUNT_EMPTY("No se ha indicado una cuenta contable para los aranceles")
	,INVOICE_DUA_VAT_ACCOUNT_EMPTY("No se ha indicado una cuenta contable para el IVA")
	// --------------------------------------------- FINANCE
	,FINANCE_NOT_FOUND("Vencimiento no encontrado.")	
	,FINANCE_CAN_NOT_BE_SETTLED("El vencimiento no se puede saldar, no est\u00E1 pendiente ni devuelto.")
	,FINANCE_CAN_NOT_BE_PAID("El vencimiento no se puede pagar, no est\u00E1 pendiente ni devuelto.")
	,FINANCE_CAN_NOT_BE_RETURNED("El vencimiento no se puede devolver, no est\u00E1 pagado ni remesado.")
	,FINANCE_CAN_NOT_BE_UNDOING("El vencimiento no se dejar pendiente, no est\u00E1 saldado. {0}.")
	,FINANCE_CAN_NOT_BE_FRACTIONED("El vencimiento no se puede fraccionar, no est\u00E1 pendiente ni devuelto.")
	,FINANCE_CAN_NOT_BE_FRACTIONED_AMOUNT("El vencimiento no se puede fraccionar, la suma de cantidades no coinciden con el importe del vencimiento.")
	,FINANCE_CAN_NOT_BE_FRACTIONED_LIST("El vencimiento no se puede fraccionar, la lista de fraccionamientos est\u00E1 vac\u00EDa.")
	,FINANCE_CAN_NOT_BE_FRACTIONED_ONE_ITEM("El vencimiento no se puede fraccionar, la lista de fraccionamientos s\u00F3lo contiene un elemento.")
	,FINANCE_AMOUNT_ZERO("El importe del vencimiento no puede ser cero.")	
	,FINANCE_EMPTY_SCOPE("No es posible encontrar un \u00E1mbito v\u00E1lido para el vencimiento.")
	,FINANCE_WRONG_IBAN_LENGTH("Longitud de IBAN incorrecta.")
	,FINANCE_WRONG_ACCOUNT_BANK("Cuenta Bancaria incorrecta.")
	,FINANCE_WRONG_IBAN("IBAN incorrecto.")
	,FINANCE_WRONG_PAYMENT("Si la factura es una venta, el tipo del vencimiento debe ser \"cobro\". En caso contario, \"pago\".")
	,DELETE_STATUS_WRONG("No se permite el borrado de vencimientos que no est\u00E9n pendientes.")
	,FINANCE_TRACKING_WITHOUT_DATE("En el tracking no existe informaci\u00F3n sobre la fecha del movimiento.")
	,FINANCE_TRACKING_WITHOUT_FINANCE("En el tracking no existe informaci\u00F3n sobre el vencimiento al que pertenece.")
	,FINANCE_TRACKING_LATER_TRACKINGS("No se puede borrar el movimiento, tiene movimientos posteriores.")
	,FINANCE_TRACKING_NO_BANK_ACCOUNT("No se puede pagar el vencimiento, no se ha indicado la cuenta contable del banco o caja.")
	,FINANCE_TRACKING_NO_REGISTRY_ACCOUNT("No se puede pagar el vencimiento, no se ha indicado la cuenta contable del titular del vencimiento.")
	,FINANCE_TRACKING_RECORDED("No se puede borrar el movimiento, est\u00E1 contabilizado y no tiene permisos de borrado.")
	,FINANCE_NEGATIVE_AMOUNT("La cantidad es un valor absoluto, no puede ser negativa")
	,FINANCE_EMPTY_COMMON_CONCEPT("El concepto com\u00FAn es obligatorio, no puede estar vac\u00EDo")
	,FINANCE_EMPTY_LOT_NUMBER("El n\u00FAmero de lote es obligatorio, no puede estar vac\u00EDo")
	,FINANCE_EMPTY_OPERATION_DATE("La fecha de operaci\u00F3n es obligatoria, no puede estar vac\u00EDa")
	,FINANCE_EMPTY_RBANK("La cuenta bancaria asociada es obligatorio, no puede estar vac\u00EDa")
	,FINANCE_ENTRY_LATER_TRACKINGS("No se puede borrar el asiento, alguno de los vencimientos tiene movimientos posteriores.")
	,FINANCE_ENTRY_FROM_FBATCH("No se puede borrar el asiento. Procede de remesa, deshaga el apunte desde la pantalla de remesas.")
	,INVOICE_FINANCES_AMOUNT("La suma de los importes de los vencimientos no coincide con el total factura")
	// --------------------------------------------- RAWDOC
	,EMPTY_RAWDOC_NATURE("La naturaleza del documento es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_RAWDOC_TYPE("El tipo del documento es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_RAWDOC_STATUS("El estado del documento es un dato obligatorio, no puede estar vac\u00EDo") 
	,EXIST_USER_WORKGROUP("El usuario ya pertenece al grupo.")
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
