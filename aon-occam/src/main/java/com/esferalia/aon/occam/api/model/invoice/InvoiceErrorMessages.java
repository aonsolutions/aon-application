package com.esferalia.aon.occam.api.model.invoice;

import java.io.Serializable;
import java.text.MessageFormat;

public enum InvoiceErrorMessages implements Serializable {
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	
	C000("Error: \"{0}\" "),
	C001("No se ha indicado el dato \"{0}\" y es obligatorio"),
	C002("La longitud de el dato \"{0}\" supera los {1} caracteres m\u00E1ximos permitidos"),
	// C003("El sistema ha inicializado el dato \"{0}\" con el valor [{1}]"),
	C004("Formato inv\u00E1lido en el dato \"{0}\""),
	C005("Ya existe una factura con esa Serie/N\u00FAmero."),
	C006("Ya existe una factura del titular con ese N\u00FAmero de referencia."),
	C007("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. "),
	C008("El a\u00F1o de la factura no es v\u00E1lido, es anterior cinco a\u00F1os al actual"),
	C009("No se ha encontrado un {0} v\u00E1lido para el titular \"{1}\""),
	C010("La factura no tiene l\u00EDneas de detalle"),
	C011("La factura tiene varios posibles titulares"),
	C012("Apunte contable descuadrado"),
	C013("Apunte contable vacio"),
	C014("El importe del vencimiento no puede ser cero."),
	C015("Cuenta Bancaria incorrecta."),
	C016("No se ha podido determinar un centro de trabajo (workplace)"),
	C017("No se han podido determinar bases y cuotas de la factura"),
	C018("No se ha podido determinar el tipo de factura"),
	C019("La fecha del vencimiento no es correcta."),
	C020("La divisa de la factura no es euro"),
	C021("Titular <b>BLOQUEADO</b> ({0})"),
	
	C050("Error comunicaci\u00F3n: \"{0}\" \"{1}\""),
	C051("Factura comunicada con errores: \"{0}\" \"{1}\""),
	
	// Mensajes previos a la contabilización
	C200("No se puede Contabilizar. Hay un descuadre entre el total factura y la suma total de los vencimientos."),
	C201("El ejercicio del apunte contable está cerrado, en explotaci\u00F3n o inactivo."),
	C202("El titular de la factura suele generar facturas con bienes afectos."),
	
	C300("Mensaje OCR: {0}"),
	
	C500("Error desconocido");
	
	;
	private String message;

	private InvoiceErrorMessages(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	
	public String format(Object ... args ) {
		return MessageFormat.format(getMessage(), args);
	}
	
	public InvoiceError err(InvoiceErrorKey key) {
		return err(key, key.getDescription());
	}
	public InvoiceError err(InvoiceErrorKey key, Object ... args) {
		return err( new InvoiceErrorContext(key), format(args));
	}
	public InvoiceError err(InvoiceErrorContext context, Object ... args) {
		return err( context, format(args));
	}
	public InvoiceError err(InvoiceErrorContext context, String message) {
		return add(context, InvoiceErrorLevel.ERR, message);
	}
	
	public InvoiceError wrn(InvoiceErrorKey key) {
		return wrn(key, key.getDescription());
	}
	public InvoiceError wrn(InvoiceErrorKey key, Object ... args) {
		return wrn( new InvoiceErrorContext(key), format(args));
	}
	public InvoiceError wrn(InvoiceErrorContext context, String message) {
		return add(context, InvoiceErrorLevel.WRN, message);
	}

	public InvoiceError inf(InvoiceErrorKey key) {
		return inf(key, key.getDescription());
	}
	public InvoiceError inf(InvoiceErrorKey key, Object ... args) {
		return inf( new InvoiceErrorContext(key), format(args));
	}
	public InvoiceError inf(InvoiceErrorContext context, String message) {
		return add(context, InvoiceErrorLevel.INF, message);
	}
	
	public InvoiceError add(InvoiceErrorContext context,InvoiceErrorLevel level,String message) {
		return new InvoiceError(context,level,this.toString(), message);
	}	
	
}