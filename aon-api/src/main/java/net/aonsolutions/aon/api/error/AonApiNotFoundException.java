package net.aonsolutions.aon.api.error;

/**
 * Excepción para los recursos que no existen. Se responde con un 404.
 */
public class AonApiNotFoundException extends AonApiException {

	private static final long serialVersionUID = 5439515975168893951L;

	public AonApiNotFoundException() {
		super();
	}

	public AonApiNotFoundException(String message) {
		super(message);
	}
}
