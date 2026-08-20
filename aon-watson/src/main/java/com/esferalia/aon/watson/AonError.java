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
	// ª --> \u00AA º --> \u00BA
	// ¿ --> \u00BF
	
	
	// -----------------------------------------------------------
	// --------------------- SECURITY ----------------------------
	// -----------------------------------------------------------
	 READ_FORBIDDEN("El acceso de lectura al recurso solicitado, ha sido denegado.")
	,WRITE_FORBIDDEN("El acceso de escritura al recurso solicitado, ha sido denegado.")
	,NULL_FILTER("No se han indicado condiciones de b\u00FAsqueda.")
	,INVALID_DOMAIN("Dominio no encontrado")
	,INVALID_USER("Usuario no encontrado")
	,INVALID_INSERT("Los datos no se han grabado correctamente")
	,INVALID_UPDATE("Los datos no se han modificado correctamente")
	,INVALID_COMPANY("Company no encontrado")
	,NO_SCOPES_DEFINED_FOR_USER("No se han definido \u00E1mbitos para el usuario")
	,NOT_DIRTY("{0} NOT SAVED! (not dirty) id: {1}")

	// -----------------------------------------------------------
	// --------------------- COMMON ------------------------------
	// -----------------------------------------------------------
	,EMPTY_ID("No se ha indicado un identificador.")
	,EMPTY_ENTERPRISE("No se ha indicado una empresa.")
	,EMPTY_DATE_FROM("No se ha indicado una fecha 'desde'.")
	,EMPTY_DATE_TO("No se ha indicado una fecha 'hasta'.")
	,EMPTY_DOMAIN("El dominio es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_DESCRIPTION("La descripci\u00F3n es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_DATE("La fecha es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_EXP_ACCOUNT("La cuenta de explotaci\u00F3n es dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_CONCEPT("El concepto es un dato dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_AMOUNT("El importe es un dato dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_BANK_ACCOUNT("El banco o caja es un dato dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_CUSTOMER("Si indica un cliente, debe ser v\u00E1lido.")
	,EMPTY_STATUS("El estado es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_SCOPE("El \u00E1mbito es un dato obligatorio, no puede estar vac\u00EDa")
	,EMPTY_NAME("El nombre es un dato obligatorio, no puede estar vac\u00EDa")
	,EMPTY_YEAR("El ejercicio es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_DATA("El dato \"{0}\" es obligatorio, no puede estar vac\u00EDo")
	,EMPTY_SAVE("No se puede guardar un objeto vac\u00EDo.")
	,INVALID_YEAR("El ejercicio debe tener un valor real")
	,EMPTY_PERIOD("El periodo es un dato obligatorio, no puede estar vac\u00EDo")
	,WRONG_PERIOD("No se ha encontrado un periodo contable para la fecha \"{0}\"")
	,EMPTY_EPIGRAPH("El ep\u00EDgrafe es un dato obligatorio, no puede estar vac\u00EDo")
	,INVALID_LENGTH("La longitud del dato \"{0}\" no puede superar los {1} car\u00E1cteres")
	,INVALID_FORMAT("El formato del dato \"{0}\" no es correcto: \"{1}\"")
	,NOT_EXIST("No existe \"{0}\"")
	
	// -----------------------------------------------------------
	// --------------------- ENUMERATION -------------------------
	// -----------------------------------------------------------
	,SECURITY_LEVEL_INVALID("Valor incorrecto para el enumerado 'Nivel de seguridad - Confidencial' ({0})")
	,ACCOUNT_ENTRY_TYPE_INVALID("Valor incorrecto para el enumerado 'Tipo de asiento' ({0})")
	
	// -----------------------------------------------------------
	// --------------------- REGISTRY --------------------------
	// -----------------------------------------------------------
	,REGISTRY_EMPTY_DOCUMENT("El documento del titular est\u00E1 vac\u00EDo.")
	,REGISTRY_NOT_VALID_DOCUMENT("El documento del titular no es v\u00E1lido.")
	,REGISTRY_EMPTY_DOCUMENT_TYPE("El tipo de documento del titular est\u00E1 vac\u00EDo.")
	,REGISTRY_EMPTY_DOCUMENT_COUNTRY("El pa\u00EDs de documento del titular est\u00E1 vac\u00EDo.")
	,REGISTRY_INVALID_DOCUMENT("Formato inv\u00E1lido en el dato documento del titular.")
	,REGISTRY_OVERFLOW_DOCUMENT("El documento no puede tener m\u00E1s de 16 caracteres.")
	,REGISTRY_EMPTY_NAME("El nombre/raz\u00F3n social del titular est\u00E1 vac\u00EDo.")
	
	,REGISTRY_INVALID_SCOPE("El \u00E1mbito del titular no es v\u00E1lido para el dominio.") 
	
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
	,ACCOUNT_PERIOD_HAS_PREVIOUS_ENTRIES("No se puede modificar el ejercicio porque existen {0} apuntes anteriores a la fecha de inicio")
	,ACCOUNT_PERIOD_HAS_LATER_ENTRIES("No se puede modificar el ejercicio porque existen {0} apuntes posteriores a la fecha de inicio")
	
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
	
	
	// --------------------------------------------- AMORTIZATION
	,AMORTIZATION_NOT_FOUND("Fiche de amortizaci\u00F3n no encontrada.")
	,AMORTIZATION_EMPTY_PERCENTAGE("No se ha indicado un coeficiente v\u00E1lido")
	,AMORTIZATION_EMPTY_FIXED_ASSET_ACCOUNT("No se ha indicado tipo de amortizaci\u00F3n ni cuenta de inmovilizado")
	,AMORTIZATION_EMPTY_ACCUMULATED_ACCOUNT("No se ha indicado tipo de amortizaci\u00F3n ni cuenta de acumulado")
	,AMORTIZATION_EMPTY_ALLOCATION_ACCOUNT("No se ha indicado tipo de amortizaci\u00F3n ni cuenta de dotaci\u00F3n")
	,AMORTIZATION_DELETE_SCORED_DETAILS("Existen detalles de amortizaci\u00F3n contabilizados, no se puede borrar la ficha de amortizaci\u00F3n.")
	,AMORTIZATION_DELETE_LINKED_INVOICES("Existen facturas vinculadas a esta ficha de amortizaci\u00F3n, no se puede borrar.")
	,AMORTIZATION_DETAIL_NOT_FOUND("Detalle de amortizaci\u00F3n no encontrado.")
	,AMORTIZATION_NOT_PENDING_ALLOCATIONS("Existe una cuota posterior a la fecha de cancelación, bloqueada o contabilizada.")
	,AMORTIZATION_WRONG_DEADLINE("La fecha de baja debe ser posterior a la fecha de inicio")
	,AMORTIZATION_WRONG_SALE_AMOUNT("El importe de baja no puede ser negativo.")

	// --------------------------------------------- AMORTIZATION TYPE
	,AMORTIZATION_TYPE_NULL("El tipo de amortizaci\u00F3n es nulo.")
	,AMORTIZATION_TYPE_NULL_DESCRIPTION("La descripci\u00F3n del tipo de amortizacion es nula")
	,AMORTIZATION_TYPE_NULL_FIXED_ASSET_ACCOUNT("El valor de la cuenta de inmovilizado es nulo")
	,AMORTIZATION_TYPE_NULL_ACCUMULATED_ACCOUNT("El valor de la cuenta de acumulado es nulo")
	,AMORTIZATION_TYPE_NULL_ALLOCATION_ACCOUNT("El valor de la cuenta de dotaci\u00F3n es nulo")
	,AMORTIZATION_TYPE_NULL_PERCENTAGE("El valor del porcentaje es nulo")
	,AMORTIZATION_TYPE_FIXED_ASSET_ACCOUNT_LENGTH("La longitud del dato para la de cuenta de inmovilizado excede el permitido")
	,AMORTIZATION_TYPE_ACCUMULATED_ACCOUNT_LENGTH("La longitud del campo para la cuenta de acumulado excede el permitido")
	,AMORTIZATION_TYPE_ALLOCATION_ACCOUNT_LENGTH("La longitud del campo para la cuenta de dotaci\u00F3n excede el permitido")
	,AMORTIZATION_TYPE_FIXED_DESCRIPTION_LENGTH("La longitud del campo para la descripci\u00F3n excede el permitido")
	,AMORTIZATION_TYPE_PARAMS_NULL("Los par\u00E1metros son nulos")
	
	// --------------------------------------------- INVOICE
	,INVOICE_SAVE_ERROR("No se ha podido grabar la factura")
	,INVOICE_SAVE_DELETE_ERROR("No se ha podido modificar o borrar la factura")
	
	,INVOICE_EMPTY_DOMAIN("El dominio de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_DATE("La fecha de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TAX_DATE("La fecha I.V.A. de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_TYPE("El tipo de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_REGISTRY("El titular de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_EMPTY_SCOPE("El \u00E1mbito de la factura es un dato obligatorio, no puede estar vac\u00EDa")
	,INVOICE_DUPLICATED_SERIES_NUMBER("Ya existe una factura con esa Serie/N\u00FAmero. [{0}]")

	,INVOICE_DUPLICATED_REFERENCE_CODE("Ya existe una factura registrada del titular o del NIF con ese N\u00FAmero de referencia. [{0},{1},{2}]")
	,INVOICE_OPERATIONS_DEADLINE("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. ")
	,INVOICE_TEN_YEARS("El a\u00F1o de la factura no es v\u00E1lido, es anterior diez a\u00F1os al actual") 
	,INVOICE_TWO_YEARS("El a\u00F1o de la factura no es v\u00E1lido, es posterior dos a\u00F1os al actual") 
	,INVOICE_EMPTY_SOURCE("No se ha indicado un origen (INVOICE_DETAIL.SOURCE) en la l\u00EDnea de factura (Error interno)")
	,INVOICE_EMPTY_WORKPLACE("No se ha indicado un centro de trabajo en la l\u00EDnea de factura.")
	,INVOICE_EMPTY_REFERENCE_CODE("No se ha indicado un n\u00FAmero de factura.")
	,INVOICE_EMPTY_TRANSACTION("No se ha indicado un tipo de transacci\u00F3n de factura (Nacional, Intracom., Extracom, ...")
	,INVOICE_NOT_FOUND("Factura no encontrada.")
	,FEE_NOT_FOUND_FOR_INVOICE_DETAIL("No se ha encontrado la cuota vinculada a la l�nea de factura. (ID cuota: {0})")
	,INVOICE_RECTIFIED_NOT_FOUND("Factura rectificada no encontrada.")
	,INVOICE_RECTIFIED_ALREADY_RECTIFIED("La factura a rectificar ya ha sido rectificada por la factura {0} del d�a {1,date,dd/MM/yyyy}")
	,INVOICE_INVALID_RECTIFICATION_DATA("Los datos necesarios para rectificar la factura, no son correctos.")
	,INVOICE_INVALID_RECTIFICATION_DATE("La fecha de la factura rectificativa es anterior a la de la rectificada.")
	,INVOICE_INVALID_RECTIFICATION_TYPE("No se permite rectificar una factura con el tipo {0}.")
	,INVOICE_INVALID_RECTIFICATION_PROFORMA("No se permite rectificar una factura proforma")
	,INVOICE_CANT_DELETE_RECTIFIED("No es posible borrar la factura porque est\u00E1 rectificada. Borre primero la factura rectificativa.")
	,INVOICE_CANT_DELETE_DUA_LINKED("No es posible borrar la factura porque est\u00E1 vinculada a un documento DUA.")
	,INVOICE_CANT_DELETE_SII("No es posible borrar la factura porque est\u00E1 enviada al SII. Dar de baja la factura en el SII.")
	,INVOICE_CANT_DELETE_TBAI("No es posible borrar la factura porque est\u00E1 enviada a Ticket Bai. Dar de baja la factura en Ticket Bai.")
	,INVOICE_CANT_DELETE_VERIFACTU("No es posible borrar la factura porque est\u00E1 enviada a Verifactu. Dar de baja la factura en Verifactu.")
	,INVOICE_CANT_DELETE_MODEL("Imposible borrar o modificar, la factura ha sido declarada en modelos fiscales: {0}")
	
	,INVOICE_CANT_CANCEL_RECORDED("No es posible anular la factura porque est\u00E1 contabilizada. Descontabilice primero la factura.")	
	,INVOICE_CANT_CANCEL_RECTIFIED("No es posible anular la factura porque est\u00E1 rectificada. Anule primero la factura rectificativa.")
	,INVOICE_CANT_CANCEL_DUA_LINKED("No es posible anular la factura porque est\u00E1 vinculada a un documento DUA.")
	,INVOICE_CANT_CANCEL_SII("No es posible anular la factura porque no est\u00E1 anulada en el SII. Dar de baja la factura en el SII.")
	,INVOICE_CANT_CANCEL_TBAI("No es posible anular la factura porque no est\u00E1 anulada en Ticket Bai. Dar de baja la factura en Ticket Bai.")
	,INVOICE_CANT_CANCEL_VERIFACTU("No es posible anular la factura porque no est\u00E1 anulada en Verifactu. Dar de baja la factura en Verifactu.")
	,INVOICE_CANT_CANCEL_MODEL("Imposible anular, la factura ha sido declarada en modelos fiscales: {0}")
	,INVOICE_CANT_CANCEL_FINANCE("No es posible anular la factura porque est\u00E1 vinculada a un vencimiento no pendiente.")
	
	,INVOICE_EXP_DATE_BEFORE_DATE("La fecha de operaci\u00F3on es posterior a la fecha de expedici\u00F3on")
	,INVOICE_EXP_DATE_PAST_MONTH_LIMIT("La fecha de operaci\u00F3n es anterior al pasado mes")
	,INVOICE_EXP_DATE_CURRENT_MONTH_LIMIT("La fecha de operaci\u00F3n es anterior al mes en curso")
	,TRACKING_CANT_DELETE_MODEL("Imposible borrar o modificar, el vto. ha sido declarada en modelos fiscales: {0}")
	
	// --------------------------------------------- RECORDER
	,INVOICE_RECORDER_INVESTMENT("Factura marcada como inversi\u00F3n")
	,INVOICE_RECORDER_SURCHARGE("Factura con recargo de equivalencia")
	,INVOICE_RECORDER_TRANSACTION("Factura de tipo {0}")
	,INVOICE_RECORDER_WITHHOLDING("Factura con retenci\u00F3n")
	,INVOICE_RECORDER_PREPAYMENT("Factura con suplidos")
	,INVOICE_RECORDER_EXPENSE_ACCOUNT("Existen gastos sin cuenta asociada.")
	,INVOICE_RECORDER_EMPTY_DOMAIN("No se ha indicado un dominio y es obligatorio")
	,INVOICE_RECORDER_EMPTY_SCOPE("No se ha indicado un \u00E1mbito y es obligatorio")
	,INVOICE_RECORDER_REGISTRY_NOT_FOUND("No se ha encontrado un {0} v\u00E1lido para el titular \"{1}\"")
//	C003("El sistema ha inicializado el dato \"{0}\" con el valor [{1}]"),
//	C004("Formato inv\u00E1lido en el dato \"{0}\""),
//	C005("Ya existe una factura con esa Serie/N\u00FAmero."),
//	C006("Ya existe una factura del titular con ese N\u00FAmero de referencia."),
//	C007("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. "),
//	C008("El a\u00F1o de la factura no es v\u00E1lido, es anterior cinco a\u00F1os al actual"),
//	C010("La factura no tiene l\u00EDneas de detalle"),
//	C011("La factura tiene varios posibles titulares"),
//	C012("Apunte contable descuadrado"),
//	C013("Apunte contable vacio"),
//	C014("El importe del vencimiento no puede ser cero."),
//	C015("Cuenta Bancaria incorrecta."),
//	C016("No se ha podido determinar un centro de trabajo (workplace)"),
//	C017("No se han podido determinar bases y cuotas de la factura"),
//	C018("No se ha podido determinar el tipo de factura"),
//	C019("La fecha del vencimiento no es correcta."),
	
	
	// --------------------------------------------- CALCULATOR
	,INVOICE_CALC_REV_MORE("No se puede realizar el c\u00E1clulo inverso. Hay m\u00E1s de un detalle de fatura")
	,INVOICE_CALC_REV_ZERO("No se puede realizar el c\u00E1lculo inverso. No hay detalles de factura.")
	,INVOICE_CALC_REV_FARMER("El c\u00E1lculo inverso para retenciones de agricultura no est\u00E1 soportado.")
	,INVOICE_CALC_NO_WITHHOLDING_INFO("No hay infomarci\u00F3n para el c\u00E1lculo de la retenci\u00F3n")
	
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
	,FINANCE_UNKNOWN_PAYMENT("No se puede determinar si el vencimiento es un pago o un cobro")
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
	,DELETE_RADDRESS_INVOICE("No se puede borrar una direcci\u00F3n que est\u00E1 registrada en una factura")
	// --------------------------------------------- RAWDOC
	,EMPTY_RAWDOC_NATURE("La naturaleza del documento es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_RAWDOC_TYPE("El tipo del documento es un dato obligatorio, no puede estar vac\u00EDo")
	,EMPTY_RAWDOC_STATUS("El estado del documento es un dato obligatorio, no puede estar vac\u00EDo") 
	,EXIST_USER_WORKGROUP("El usuario ya pertenece al grupo.")
	// --------------------------------------------- QUESTION
	,QUESTION_NULL("El valor de la pregunta es nulo.")
	,QUESTION_EMPTY("El valor de la pregunta est\u00E1 vac\u00EDo.")
	,EMPTY_QUESTION_TEXT("El valor del texto de la pregunta est\u00E1 vac\u00EDo")
	,NULL_QUESTION_TEXT("El valor del texto de la pregunta es nulo")
	,NULL_TYPE("El valor del tipo de la pregunta es nulo")
	,INVALID_SIZE_QUESTION_TEXT("El tama\u00F1o del texto de la pregunta es inv\u00E1lido")
	,INVALID_SIZE_ALIAS("El tama\u00F1o del texto de la pregunta es inv\u00E1lido")
	,REPEATED_ALIAS("No puede haber dos preguntas con el mismo alias")

	//-----------------------------------------------UPLOAD OF DOCUMENT
	,NULL_FILE_UPLOADED("El archivo seleccionado es nulo")
	,NULL_FILES_UPLOADED("Los archivos seleccionados son nulos")
	,FILE_SIZE_EXCEEDED("El tama\u00F1o de archivo no puede superar los 5MB")
	,NULL_TEXT_RECEIVED("El texto recibido es nulo")
	,INVALID_DNI_FORMAT("El formato del campo para el dni no es valido")
	,INVALID_NATIONALITY_FORMAT("El formato del campo para la nacionalidad no es valido")
	,INVALID_NAME_FORMAT("El formato para el campo del nombre no es valido")
	,INVALID_FIRST_SURNAME("El formato para el campo del primer apellido no es valido")
	,INVALID_SECOND_SURNAME("El formato para el campo del segundo apellido no es valido")
	,NULL_NEXT_LINE("Error al cargar los datos, intentelo otra vez")
	,NULL_DATE_STRING("No se ha podido establecer la fecha, intentelo otra vez")
	,GROUP_NOT_MATCH("No se ha podido encontrar el dato, intentelo otra vez")

	// ---------------------------------------------- REGISTRY_BANK
	,REGISTRY_BANK_NULL("El registro no puede ser nulo")
	,REGISTRY_BANK_EMPTY("El registro no puede estar vac\u00EDo")
	,REGISTRY_BANK_NULL_DOMAIN("El dominio no puede ser nulo")
	,REGISTRY_BANK_NULL_REGISTRY("El registro no puede ser nulo")
	,REGISTRY_BANK_INVALID_BIC_SIZE("El tama\u00F1o del bic es inv\u00E1lido")
	,REGISTRY_BANK_INVALID_ALIAS_SIZE("El tama\u00F1o del alias es inv\u00E1lido")
	,REGISTRY_BANK_INVALID_SUFFIX_SIZE("El tama\u00F1o del sufijo es inv\u00E1lido")
	,REGISTRY_BANK_INVALID_REQUISITION_SIZE("El tama\u00F1o de la requisici\u00F3n es inv\u00E1lido")
	,REGISTRY_BANK_INVALID_SEPA_MANDATE_REF_SIZE("El tama\u00F1o de la referencia de la orden de domicilizaci\u00F3n de adeudo directo SEPA es inv\u00E1lido")
	,REGISTRY_BANK_INVALID("La cuenta bancaria es inv\u00E1lida")
	
	// --------------------------------------------- MARKETING CAMPAIGN
	,MARKETING_CAMPAIGN_NULL("El valor de la campa\u00f1a es nulo.")
	,NULL_MARKETING_CAMPAIGN_DESCRIPTION("La descripci\u00f3n de la campa\u00f1a es nulo.")
	,NULL_MARKETING_CAMPAIGN_ACTIVE("El estado activo de la campa\u00f1a es nulo.")
	,NULL_MARKETING_CAMPAIGN_SCOPE("El \u00e1mbito de la campa\u00f1a es nulo.")
	,INVALID_SIZE_MARKETING_CAMPAIGN_DESCRIPTION("El tama\u00F1o de la descripci\u00F3n es inv\u00E1lido")
	,REPEATED_DESCRIPTION("No puede haber dos campa\u00f1as con la misma descripci\u00f3n")
	
	// --------------------------------------------- MARKETING ACTION
	,MARKETING_ACTION_NULL("El valor de la acci\u00f3n es nulo.")
	,NULL_MARKETING_ACTION_DESCRIPTION("La descripci\u00f3n de la acci\u00f3n es nulo.")
	,NULL_MARKETING_CAMPAIGN_ACTION("La acci\u00f3n no esta vincula a una campa\u00f1a.")
	,NULL_MARKETING_ACTION_START_DATE("La fecha de inicio de la acci\u00f3n  es nulo.")
	,INVALID_SIZE_MARKETING_ACTION_DESCRIPTION("El tama\u00F1o de la descripci\u00F3n es inv\u00E1lido")
	,REPEATED_ACTION_DESCRIPTION("No puede haber dos acciones con la misma descripci\u00f3n")
	
	// --------------------------------------------- MARKETING ACTION
	,MARKETING_ACTION_TARGET_NULL("El valor del cliente potencial de la acci\u00f3n es nulo.")
	,NULL_MARKETING_ACTION_TARGET("Debe existir seleccionar un cliente potencial.")
	,NULL_MARKETING_CAMPAIGN_ACTION_TARGET("El cliente potencial no esta vinculado a una acci\u00f3n.")
	,REPEATED_ACTION_TARGET("No puede haber dos veces el mismo cliente potencial para la misma acci\u00f3n")
	
	// --------------------------------------------- SELLER
	,SELLER_NULL("El valor del agente comercial es nulo.")
	,NULL_SELLER_NAME("El Nombre / Raz\u00f3n Social del agente comercial es nulo.")
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
