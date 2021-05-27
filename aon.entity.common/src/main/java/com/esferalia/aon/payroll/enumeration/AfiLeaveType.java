package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringUtils;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum AfiLeaveType implements IResourceable, IStringEnum {

	/**
	 * 51 - Baja voluntaria/Dimisión - Dimisión del trabajado, cese
	 * voluntario del trabajador durante el período de prueba, rescisión de
	 * la relación laboral por voluntad del trabajador por motivos no incluidos
	 * en otra clave específica, incluidos los del artículo
	 */
	T_51("51"),

	/**
	 * 50 - ET.
	 */
	T_50("50"),

	/**
	 * 53 - Baja despido disciplinario individual - Baja por extinción del
	 * contrato por despido individual disciplinario regulado en le artículo 54
	 * ET (basado en le incumplimiento grave y culpable del trabajador).
	 */
	T_53("53"),

	/**
	 * 54 - Baja no voluntaria por otras causas Fin de contrato en situación de
	 * IT del trabajador, cierre legal de la empresa, despidos derivados
	 * de la muerte o jubilación del empresario
	 */
	T_54("54"),

	/**
	 * 55 - Baja por fusión - absorción empresa - Baja como consecuencia de
	 * fusiones, absorciones, segregaciones de empresas que implequen cambio de
	 * CCC.
	 */
	T_55("55"),

	/**
	 * 56 - Baja por fallecimiento
	 */
	T_56("56"),

	/**
	 * 58 - Baja por pase a la situación de pensionista - Jubilación e invalidez
	 * permanente
	 */
	T_58("58"),

	/**
	 * 63 - Baja por excedencia voluntaria/forzosa - Cualquier excedencia
	 * independientemente de la causa que la origine. (excepto excedencia
	 * maternal)
	 */
	T_63("63"),

	/**
	 * 65 - Baja por agotamiento I.T.
	 */
	T_65("65"),

	/**
	 * 67 - Baja por paro estacional - Sólo Régimen especial de trabajadores del
	 * mar
	 */
	T_67("67"),

	/**
	 * 68 - Baja por excedencia maternal/cuidado de hijos
	 */
	T_68("68"),

	/**
	 * 69 - Baja por suspensión temporal ERE - Suspensión de la relación laboral
	 * sin prestación o subsidio de desempleo y sin que exista acuerdo en ERE
	 * por el cual la empresa adquiera la obligación de mantenimiento de alta y
	 * cotización por el trabajador
	 */
	T_69("69"),

	/**
	 * 73 - Baja por cuidado de familiares
	 */
	T_73("73"),

	/**
	 * 74 - Baja por otras causas de suspensión - Mutuo acuerdo de las partes,
	 * causas de suspensión consignadas válidamente en el contrato, pase a
	 * ejercicio de cargo público representativo, privación de libertad del
	 * trabajador mientras no existe sentencia condenatoria, suspensión de
	 * empleo y sueldo.
	 */
	T_74("74"),

	/**
	 * 76 - Baja por excedencia violencia de género - Sólo funcionarios
	 */
	T_76("76"),

	/**
	 * 77 - Baja por despido colectivo - Despidos colectivos que se rigen por el
	 * rtículo 51 del ET, extinciones de contrato por causas económicas,
	 * técnicas, organizativas o deproducción, así como las extinciones
	 * derivadas de fuerza mayor del artículo 51.7 ET, despidos colectivos
	 * llevados a cabo durante un proceso de concurso de acreedores que se rigen
	 * por el artículo 64 de la Ley Concursal.
	 */
	T_77("77"),

	/**
	 * 80 - Suspensión por violencia de género - Artículo 45.1.n del ET
	 */
	T_80("80"),

	/**
	 * 85 - Baja por no superar el período de prueba - Baja por no superar el
	 * período de prueba del contrato en los términos establecidos en el
	 * artículo 14 ET con desistimiento del empresario.
	 */
	T_85("85"),

	/**
	 * 91 - Baja por despido por causas objetivas empresa - Extinción del
	 * contrato de uno o más trabajadores, por causas ecónomicas, técnicas,
	 * organizativas o de producción, sin alcanzar los umbrales del artículo 51
	 * ET para los que se utilizará la Clave 77. (Artículo 52 letra c) ET).
	 */
	T_91("91"),

	/**
	 * 92 - Baja por despido por causas objetivas trabajador - Extinciónes de
	 * contratos por ineptitud del trabajador (artículo 52 letra a) ET), por
	 * falta deadaptación a las modificaciones del puesto de trabajo (artículo
	 * 52 letra b) ET), o por faltas deasistencia (artículo 52 letra d) ET).
	 */
	T_92("92"),

	/**
	 * 93 - Baja por fin contrato temporal o de duración determinada - Bajas por
	 * finalización de contrato en las que no se ha producido un despido. Se
	 * incluirán los siguientes supuestos: fin de contrato de obra y
	 * servicio, fin de contrato eventual por circustancias de la
	 * producción, fin de contrato de interinidad
	 */
	T_93("93"),

	/**
	 * 94 - Baja por pase a inactividad fijos discontinuos - Bajas derivadas del
	 * cese de la actividad de los trabajadores fijos discontinuos al finalizar
	 * el período por el que fueron llamados para ejercer la actividad. La baja
	 * debe de comunicarse tantas veces como finalicen los llamamientos
	 * realizados por el empresario dentro de la relación laboral establecida.
	 */
	T_94("94"),

	/**
	 * 99 - Otras causas de baja - Sólo utilizable para situaciones de guarda
	 * legal y cambio de puesto de trabajo.
	 */
	T_99("99")

	;

	/** Message key prefix. */
	private static final String MSG_KEY_PREFIX = "aon_enum_afi_leave_type_";

	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale);
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}

	private String value;

	AfiLeaveType(String value) {
		this.value = value;
	}

	@Override
	public String getValue() {
		return value;
	}

}
