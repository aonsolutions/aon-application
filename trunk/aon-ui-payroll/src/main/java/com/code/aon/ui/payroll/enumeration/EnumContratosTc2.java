package com.code.aon.ui.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

/**
 * The Enum CampaignStatus.
 */
public enum EnumContratosTc2 implements IResourceable {

	pendiente,
	otro,
	otromas;
	
	/*
    1 Desempleado inscrito en Oficina de Empleo
	2 Desempleado inscrito en Oficina de Empleo durante más de 12 meses
	3 Desempleado Subsidio Régimen Especial Agrario
	4 Beneficiario Prestación Desempleo durante más de un año
	5 Desempleado inscrito en Oficina de Empleo durante más de 6 meses
	6 Beneficiario Prestación Desempleo -contributiva o asistencial- al que falta un año o mas de percepción de la prestación
    */
	private static final String BASE_NAME = "com.code.aon.ui.payroll.i18n.messages";

	/** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_status_";
	
	/**
	 * Returns a <code>String</code> with the transalation <code>Locale</code>
	 * for the locale.
	 * 
	 * @param locale Required Locale.
	 * 
	 * @return String a <code>String</code>.
	 */
    public String getName(Locale locale) {
        ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
    }
}