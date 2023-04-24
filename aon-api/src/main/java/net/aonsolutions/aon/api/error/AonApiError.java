package net.aonsolutions.aon.api.error;

public enum AonApiError {

	UNAUTHORIZED("No está autorizado."),
	EXPIRED_TOKEN("La sesión ha expirado."),
	ROUTE_ERROR("La ruta introducida es incorrecta."),
	METHOD_NOT_SUPPORTED("El método no está soportado."),
	EMPTY_DATA("No existen datos para la consulta."),
	
    INCORRECT_PASSWORD("La Contraseña no coincide."),
    NOT_EXIST_USER("El usuario no existe."),
    NOT_VALID_EMAIL("No es un correo electrónico válido.");
	
	String message;
	
	private AonApiError(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public AonApiError setMessage(String message) {
		this.message = message;
		return this;
	}
}
