package net.aonsolutions.aon.tedi;

import java.io.Serializable;
import java.text.MessageFormat;

import com.esferalia.aon.occam.api.model.tedi.TediContext;
import com.esferalia.aon.occam.api.model.tedi.TediContextKey;
import com.esferalia.aon.occam.api.model.tedi.TediLevel;
import com.esferalia.aon.occam.api.model.tedi.TediError;

public enum TediErrorMessages implements Serializable {
	// Á --> \u00C1 á --> \u00E1 
	// É --> \u00C9 é --> \u00E9 
	// Í --> \u00CD í --> \u00ED 
	// Ó --> \u00D3 ó --> \u00F3 
	// Ú --> \u00DA ú --> \u00FA ... acento
	// Ü --> \u00DC ü --> \u00fc ... diéresis
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00BA ª --> \u00AA 
	// ¿ --> \u00BF
	
	C001("No se ha indicado el dato \"{0}\" y es obligatorio"),
	C002("La longitud de el dato \"{0}\" supera los {1} caracteres m\u00E1ximos permitidos"),
	C003("El sistema ha inicializado el dato \"{0}\" con el valor [{1}]"),
	C004("Formato inv\u00E1lido en el dato \"{0}\""),
	C005("Ya existe una factura con esa Serie/N\u00FAmero."),
	C006("Ya existe una factura del titular con ese N\u00FAmero de referencia."),
	C007("La fecha de la factura rebasa la fecha l\u00EDmite de operaciones indicada en la configuraci\u00F3n de empresa. "),
	C008("El a\u00F1o de la factura no es v\u00E1lido, es anterior cinco a\u00F1os al actual"),
	C500("Error desconocido");
	;
	private String message;

	private TediErrorMessages(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}
	
	public String format(Object ... args ) {
		return MessageFormat.format(getMessage(), args);
	}
	
	public TediError err(TediContextKey key) {
		return err(key, key.getDescription());
	}
	public TediError err(TediContextKey key, Object ... args) {
		return err( new TediContext(key), format(args));
	}
	public TediError err(TediContext context, Object ... args) {
		return err( context, format(args));
	}
	public TediError err(TediContext context, String message) {
		return add(context, TediLevel.ERR, message);
	}
	
	public TediError wrn(TediContextKey key) {
		return wrn(key, key.getDescription());
	}
	public TediError wrn(TediContextKey key, Object ... args) {
		return wrn( new TediContext(key), format(args));
	}
	public TediError wrn(TediContext context, String message) {
		return add(context, TediLevel.WRN, message);
	}

	public TediError inf(TediContextKey key) {
		return inf(key, key.getDescription());
	}
	public TediError inf(TediContextKey key, Object ... args) {
		return inf( new TediContext(key), format(args));
	}
	public TediError inf(TediContext context, String message) {
		return add(context, TediLevel.INF, message);
	}
	
	public TediError add(TediContext context,TediLevel level,String message) {
		return new TediError(context,level,this.toString(), message);
	}	
	
}