package com.esferalia.aon.gwt.payroll.shared;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;

public class ErrorDescription {

	public static interface Visitor<T> {

		T visitInfo(InfoDescription error);

		T visitError(ErrorDescription error);

		T visitWarning(WarningDescription error);

		T visitSuccess(SuccessDescription error);
	}

	private String cause;
	private String message;
	private String solution;

	public String getCause() {
		return cause;
	}

	public ErrorDescription setCause(String cause) {
		this.cause = cause;
		return this;
	}

	public String getMessage() {
		return message;
	}

	public ErrorDescription setMessage(String message) {
		this.message = message;
		return this;
	}

	public String getSolution() {
		return solution;
	}

	public ErrorDescription setSolution(String solution) {
		this.solution = solution;
		return this;
	}

	public <T> T accept(ErrorDescription.Visitor<T> visitor) {
		return visitor.visitError(this);
	}

	public static class InfoDescription extends ErrorDescription {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitInfo(this);
		}
	}

	public static class SuccessDescription extends ErrorDescription {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitSuccess(this);
		}
	}

	public static class WarningDescription extends ErrorDescription {
		@Override
		public <T> T accept(Visitor<T> visitor) {
			return visitor.visitWarning(this);
		}
	}
	
	
	//@formatter:off
	@SuppressWarnings("serial")
	public static final Map<String, ErrorDescription> ERROR_DESCRIPTIONS = 
			new HashMap<String, ErrorDescription>(){{
	// ------------------------------------------------------------------------
	// 				ERRORES QUE IMPIDEN EL TRATAMIENTO DEL ENVÍO
	// (En todos estos casos el envío, con todas las liquidaciones no se trata)
	//
	
	// ------------------------------------------------------------------------
	// 				ERRORES QUE IMPIDEN EL TRATAMIENTO DE LA LIQUIDACIÓN
	// 				(En todos estos supuestos la liquidación no se trata) 
	//
		put(/*R*/"2470", new ErrorDescription()
					.setMessage("C.C.C. inexistente")
					.setCause("El c\u00F3digo de cuenta de cotizaci\u00F3nenviado no "
					+"existe en Afiliaci\u00F3n")
					.setSolution("Comprobar los datos del CCC"));
		
	
	// ------------------------------------------------------------------------
	// 				RESULTADO DEL TRATAMIENTO DE LA LIQUIDACIóN
	// (La liquidación se ha tratado, el resultado puede ser: correcta, con errores, 
	// 		o puede no darse de alta por contener algún error que lo impida) 
	
		put(/*R*/"9529", new WarningDescription()
				.setMessage("La liquidaci\u00F3n est\u00E1 calculada")
				.setCause("La liquidaci\u00F3n est\u00E1 conciliada en su totalidad")
				.setSolution("Los datos aportados han permitido calcular todos "
				+"los trabajadores de la liquidaci\u00F3n"));
		
		put(/*A*/"9708", new WarningDescription() 
				.setMessage("No se remite borrador de la Relaci\u00F3n Nominal de Trabajadores "
				+ "por no haber solicitado expresamente su env\u00EDo ")
				.setCause("Este aviso se genera cuando el usuario no ha consignado en el fichero de bases, "
				+ "fichero de solicitud de borrador o fichero de confirmaci\u00F3n el nodo opcional 'SolicitudRecepcionRNT'. "
				+ "Por defecto el Sistema no remite los borradores de los RNT. ")
				.setSolution("Si con posterioridad desea recibir el borrador de la RNT deber\u00E1 solicitar "
				+ "el borrador de la liquidaci\u00F3n, a trav\u00E9s del servicio creado a tal efecto o mediante el "
				+ "env\u00EDo de un fichero cumplimentando el valor S en el nodo 'SolicitudRecepcionRNT'."));

		

		// ------------------------------------------------------------------------
	// 								MENSAJES DE AVISO
	
		put(/*A*/"9761", new SuccessDescription()
				.setMessage("Liquidaci\u00F3n confirmada")
				.setCause("La liquidaci\u00F3n ha sido confirmada ha instancias del usuario")
				.setSolution("La liquidaci\u00F3n esta confirmada"));
	
		put(/*A*/"9763", new SuccessDescription()
				.setMessage("Liquidaci\u00F3n confirmada autom\u00E1ticamente")
				.setCause("Este mensaje se genera a partir del d\u00EDa 28 en los "
				+"procesos de env\u00EDos de ficheros de bases, solicitud de "
				+"rectificaci\u00F3n, solicitud de confirmaci\u00F3n, as\u00ED como env\u00EDo "
				+"de fichero de solicitud de borrador cuando se han podido "
				+"calcular todos los trabajadores de la liquidaci\u00F3n. "
				+"En este caso, si todos los trabajadores est\u00E1n calculados "
				+"el Sistema confirma autom\u00E1ticamente la liquidaci\u00F3n."
				+"Tambi\u00E9n se genera esta mensaje como respuesta a las "
				+"solicitudes de borrador realizadas a partir del d\u00EDa 28 "
				+"del mes a trav\u00E9s del Servicio on-line cuando existen "
				+"trabajadores sin calcular (confirmaci\u00F3n de liquidaci\u00F3n "
				+"parcial) "
				)
				.setSolution("La liquidaci\u00F3n ha quedado confirmada"));
	
		put(/*R*/"9566", new SuccessDescription() 
				.setMessage("Documento ya confirmado")
				.setCause("Se genera como respuesta a cualquier fichero que act\u00FAe "
				+ "sobre la liquidaci\u00F3n (excepto el de rectificaci\u00F3n) cuando la "
				+ "liquidaci\u00F3n ya ha sido confirmada")
				.setSolution("Si procede enviar nuevos datos, remitir un fichero de "
				+ "rectificaci\u00F3n (s\u00F3lo posible durante el mes de presentaci\u00F3n)."));

		put(/*A*/"9523", new InfoDescription()
				.setMessage("Fin de plazo de Cargo en Cuenta, se tramita pago "
				+"electr\u00F3nico de oficio. ")
				.setCause("El C\u00F3digo de Cuenta de Cotizaci\u00F3n tiene asignado "
				+"modalidad de pago \"cargo en cuenta\" pero la confirmaci\u00F3n "
				+"se produce una vez cerrado el plazo para tramitar esa "
				+"modalidad de pago. La liquidaci\u00F3n queda confirmada y se "
				+"tramita con modalidad de pago \"pago electr\u00F3nico\"")
				.setSolution("Para esa liquidaci\u00F3n ya no es posible la "
				+"tramitaci\u00F3n por Cargo en Cuenta. En meses sucesivos recordar "
				+"enviar el fichero de solicitud de confirmaci\u00F3n hasta el d\u00EDa "
				+"20 de cada mes. "));
	
		// ------------------------------------------------------------------------
		// 								MENSAJES DE INFO
		
		put(/*A*/"9999", new InfoDescription() 
				.setMessage("Liquidaci\u00F3n no iniciada")
				.setCause("No existen mensajes para Liquidaci\u00F3n")
				.setSolution("Envie la Solicitud del Fichero de Trabajadores e importe los Mensajes Recibidos."));
		put(/*R*/"9998", new InfoDescription() 
				.setMessage("Peticiu00F3n procesada correctamente")
				.setCause("")
				.setSolution(""));
	}};
	//@formatter:on

	public static ErrorDescription getErrorDescription(String code){
		String key = code.substring(1); 
		return ERROR_DESCRIPTIONS.get(key);
	}
	
}