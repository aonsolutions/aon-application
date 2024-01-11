package net.aonsolutions.invofox.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class OCRError implements Serializable {
	
	private static final long serialVersionUID = 6671383047953334214L;
	
	private OCRSeverity severity;
	private String code;
	private OCRAdditionalInfo additionalInfo;
	private String timestamp;
	private String user;
	private String  info;
	private String transactionId;
	private List<OCRField> fields;
	
	public Optional<OCRSeverity> getSeverity() {
		return Optional.ofNullable(severity);
	}
	public OCRError setSeverity(OCRSeverity severity) {
		this.severity = severity;
		return this;
	}
	
	public Optional<String> getCode() {
		return Optional.ofNullable(code);
	}
	public OCRError setCode(String code) {
		this.code = code;
		return this;
	}
	
	public Optional<OCRAdditionalInfo> getAdditionalInfo() {
		return Optional.ofNullable(additionalInfo);
	}
	public OCRError setAdditionalInfo(OCRAdditionalInfo additionalInfo) {
		this.additionalInfo = additionalInfo;
		return this;
	}
	
	public Optional<String> getTimestamp() {
		return Optional.ofNullable(timestamp);
	}
	public OCRError setTimestamp(String timestamp) {
		this.timestamp = timestamp;
		return this;
	}
	
	public Optional<String> getUser() {
		return Optional.ofNullable(user);
	}
	public OCRError setUser(String user) {
		this.user = user;
		return this;
	}
	
	public Optional<String> getInfo() {
		return Optional.ofNullable(info);
	}
	public OCRError setInfo(String info) {
		this.info = info;
		return this;
	}
	
	public Optional<String> getTransactionId() {
		return Optional.ofNullable(transactionId);
	}
	public OCRError setTransactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public Optional<List<OCRField>> getFields() {
		return Optional.ofNullable(fields);
	}
	public OCRError setFields(List<OCRField> fields) {
		this.fields = fields;
		return this;
	}
	
	public Optional<String> getDescription() {
		return Optional.ofNullable(getErrorDescription(this));
	}
	
	private static String getErrorDescription(OCRError error) {
	    
	    String code = error.getCode().orElse("ERR_UNKNOW"); 
	    
	    
	    switch (code) {
	    case "ERR_FILE_TOO_BIG":
		return "El archivo subido es demasiado grande (máximo 50 MB)";
	    case "ERR_TOO_MANY_PAGES":
		return "El documento tiene demasiadas páginas (máximo 10)";
	    case "ERR_UNSUPPORTED_FILE_FORMAT":
		return "El form ato del documento no es compatible.";
	    case "ERR_EMPTY_VALUE": {
		return format(error, "El campo <b>%s</b> %s no tiene valor", "</br>" );
	    }
	    case "ERR_INCORRECT_VALUE": {
		return format(error, "Valor no permitido para el campo <b>%s</b> %s", "</br>" );
	    }
	    case "ERR_INVALID_FORMAT":
		return "El valor extraído tiene un formato no válido";
	    case "ERR_LOW_CONFIDENCE": {
		return format(error, "El campo <b>%s</b> %s tiene poca confianza.", "</br>" );
	    }
	    case "ERR_HANDWRITTEN_DOC":
		return "El documento está escrito a mano.";
	    case "ERR_INVALID_DOC_TYPE":
		return "Se desconoce el tipo de documento (no factura, ticket, etc.)";
	    case "ERR_CROPPED_DOC":
		return "El documento está recortado, por lo que falta información.";
	    case "ERR_MULTI_DOC_PAGE_FOUND":
		return "El archivo contiene varios documentos.";
	    case "ERR_PAGES_MISSING":
		return "Al documento le faltan páginas, por lo tanto está incompleto.";
	    case "ERR_BAD_QUALITY":
		return "El documento tiene mala calidad.";
	    case "ERR_DUPLICATED_DOCUMENT":
		return "El documento está duplicado.";
	    case "ERR_MISSING_INFO":
		return "Al documento le falta información requerida.";
	    case "ERR_BREAKDOWN_AMOUNT_MISSMATCH":
		int line = getLine(error);
		return String.format("Las cantidades %s de los desgloses no son  correctas.",
			line != -1 ? "de la línea " + line : "");
	    case "ERR_CLASSIFIER_DISCARD":
		return "Este documento no tiene un tipo válido.";
	    case "WARN_CLASSIFIER_FORCED_DEFTYPE":
		return "El clasificador uso el tipo por defecto.";

	    case "ERR_OTHER":
		return "Error generico";
	    default:
		return "Error desconocido '" + code + "'";
	    }

	}
	
	private static String format(OCRError error, String format, String delimiter ) {
	    return error.getFields().orElse(Collections.emptyList()).stream().map(f -> String
		    .format(format, f.getDescription().orElse(f.getName().orElse("")), f.getIndex().map( i -> " de la línea " + ++i ).orElse("") ))
		    .collect(Collectors.joining("</br>"));
	}
	
	private static int getLine(OCRError error) {
	    return error.getFields().orElse(Collections.emptyList()).stream()
		    .filter(f -> "breakdowns".equalsIgnoreCase(f.getPrefix().orElse("") ) )
		    .map(f -> f.getIndex().map(i -> i + 1 ).orElse(-1)).findFirst().orElse(-1) ;
	}	

}
