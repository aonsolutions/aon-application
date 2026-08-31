package com.esferalia.aon.occam.api.model.registry;

import com.esferalia.aon.watson.util.AonStringUtils;

/**
 * Utilidades sobre el texto de las notas de registro.
 *
 * El motivo y la fecha de expiracion no tienen columna propia: viajan dentro
 * de comments con el formato que genera CustomersServlet.saveStatusNote:
 *
 *   Cambio estado de X a Y.
 *   Motivo: <tagName>
 *   F. Expiracion: <fecha>
 */
public class RegistryNoteUtils {

	private static final String REASON_LABEL     = "Motivo:";
	private static final String EXPIRATION_LABEL = "F. Expiracion:";

	private RegistryNoteUtils() {
	}

	/** Motivo del cambio de estado, o null si la nota no sigue el formato. */
	public static String extractReason(String comments) {
		return extractLabel(comments, REASON_LABEL);
	}

	/**
	 * Fecha de expiracion tal cual se escribio en la nota (sin parsear).
	 * Solo la llevan las notas de BLOCKED: en ACTIVE/INACTIVE no hay fecha.
	 */
	public static String extractExpirationDate(String comments) {
		return extractLabel(comments, EXPIRATION_LABEL);
	}

	/** Devuelve el resto de la linea que empieza por label. */
	private static String extractLabel(String comments, String label) {
		if (AonStringUtils.isBlank(comments))
			return null;

		int start = comments.indexOf(label);
		if (start < 0)
			return null;

		start += label.length();

		int end = comments.indexOf('\n', start);
		if (end < 0)
			end = comments.length();

		String value = comments.substring(start, end).trim();

		return AonStringUtils.isBlank(value) ? null : value;
	}
}