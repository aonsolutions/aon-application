package com.code.aon.record.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractType implements IResourceable {
	
	/**
	 * Tiempo parcial
	 */
	PART_TIME,
	
	/**
	 * Formación
	 */
	LEARNING,
	
	/**
	 * Interinidad
	 */
	INTERIM,
	
	/**
	 * Contrato de Obra o servicio determinado
	 */
	SERVICE_CONTRACT,
	
	/**
	 * Temporal
	 */
	TEMPORARY,
	
	/**
	 * Indefinido
	 */
	INDEFINITE,
	
	/**
	 * Relevo
	 */
	RELIEF;
	
	/**
     * Ruta base del fichero de mensajes.
     */
    private static final String BASE_NAME = "com.code.aon.record.i18n.messages";

    /**
     * Prefijo de la clave de mensajes. 
     */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_";

	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
}