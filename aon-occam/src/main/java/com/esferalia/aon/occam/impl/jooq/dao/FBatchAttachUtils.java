package com.esferalia.aon.occam.impl.jooq.dao;

import java.text.Normalizer;
import java.util.Locale;

import com.esferalia.aon.occam.api.model.type.FBatchType;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FBatchAttachUtils {

	private static final String XML_LABEL_SUFFIX = " (XML)";
	private static final int    MAX_LENGTH       = 100;

	/** Nombre del adjunto: TIPO_DESCRIPCION (sin extension; la pone el MimeType). */
	public static String buildDescription(Byte payment, Byte type, String fbatchDescription) {
		FBatchType fbatchType = FBatchType.of(payment, type);

		String label = fbatchType == null ? "SEPA" : fbatchType.getFileTypeDescription(type);
		if (label.endsWith(XML_LABEL_SUFFIX))
			label = label.substring(0, label.length() - XML_LABEL_SUFFIX.length());

		String description = normalize(fbatchDescription);
		String name = AonStringUtils.isBlank(description) ? label : label + "_" + description;

		return name.length() > MAX_LENGTH ? name.substring(0, MAX_LENGTH) : name;
	}

	/** Mayusculas, sin acentos, espacios a guion, solo [A-Z0-9_-]. */
	public static String normalize(String input) {
	    if (AonStringUtils.isBlank(input)) return "";

	    return Normalizer.normalize(input, Normalizer.Form.NFD)
	            .replaceAll("\\p{M}", "")          // acentos y diacriticos
	            .toUpperCase(Locale.ROOT)
	            .replaceAll("[^A-Z0-9 _-]", " ")   // el resto pasa a espacio
	            .replaceAll("\\s+", " ")           // colapsa (tabs y saltos incluidos)
	            .replaceAll("-{2,}", "-")
	            .trim()
	            .replaceAll("^[-_]+|[-_]+$", "")
	            .trim();
	}
}