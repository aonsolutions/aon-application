package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum AfiActionType implements IResourceable, IStringEnum {

	/**
	 * MA - Alta sucesiva
	 */
	MA("MA"),

	/**
	 * MB - Baja
	 */
	MB("MB"),

	/**
	 * MG - Cambio de grupo de cotización
	 */
	MG("MG"),

	/**
	 * MC - Cambio de contrato (tipo/coeficiente)
	 */
	MC("MC"),

	/**
	 * MT - Cambio de ocupación
	 */
	MT("MT"),

	/**
	 * CCP - Cambio de Categoría Profesional
	 */
	CCP("CCP"),

	/**
	 * MJR - Mecanización de Jornadas Reales (régimen 0163)
	 */
	MJR("MJR"),

	/**
	 * CIT - Cierre de Períodos de Incapacidad Temporal
	 */
	CIT("CIT"),

	/**
	 * MHU - Mecanización de HUelga
	 */
	MHU("MHU"),
	
	/**
	 * MIN - Mecanización de Inactividad
	 */
	MIN("MIN")

	;

	/** Message key prefix. */
	private static final String MSG_KEY_PREFIX = "aon_enum_afi_action_type_";

	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	private String value;

	AfiActionType(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

}
