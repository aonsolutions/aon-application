package com.esferalia.aon.gwt.template.server;

public enum ErrorMessage {
	
	FILE_NOT_VALID("El archivo no es válido."),
	TEMPLATE_COMPATIBILITY("El archivo importado no es compatible con la plantilla seleccionada."),
	WRONG_DATA("Dato Incorrecto."),
	TOO_LARGE("El valor es demasiado grande."),
	NOT_NUMERIC("El valor no es de tipo numérico."),
	CATEGORY_NOT_EXIST("La categoría no existe."),
	BRAND_NOT_EXIST("La marca no existe."),
	TAG_NOT_EXIST("La etiqueta no existe."),
	TAX_NOT_EXIST("El impuesto no existe.");
	
	String message;
	private ErrorMessage(String message) {
		this.message = message;
	}
	public String getMessage() {
		return message;
	}

	
}
