package com.code.aon.fiscal.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;
import com.code.aon.common.enumeration.IStringEnum;

public enum WithholdingDetailSubkey  implements IResourceable, IStringEnum {

	//Se consignará esta subclave cuando se trate de percepciones consistentes en pensiones y haberes pasivos de los regímenes de la Seguridad Social o de Clases Pasivas.
	B01("B01",WithholdingDetailKey.B), 
	//Se consignará esta subclave en todas las percepciones de la clave B distintas de las que deban relacionarse bajo la subclave 01.
	B02("B02",WithholdingDetailKey.B),
	//Se consignará esta subclave cuando se trate de percepciones correspondientes a los premios literarios, científicos o artísticos no exentos del impuesto a que se refiere el artículo 14.1 letra g) del Reglamento del Impuesto sobre la Renta de las Personas Físicas.
	F01("F01",WithholdingDetailKey.F),
	//Se consignará esta subclave en todas las percepciones de la clave F distintas de las que deban relacionarse bajo la subclave 01.
	F02("F02",WithholdingDetailKey.F),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido con carácter general en el artículo 115.1 del Reglamento del Impuesto sobre la Renta de las Personas Físicas.
	G01("G01",WithholdingDetailKey.G),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido en el citado artículo del Reglamento del Impuesto para los rendimientos satisfechos a recaudadoras y recaudadores municipales, mediadores de seguros que utilicen los servicios de auxiliares externos y delegados comerciales de la entidad pública empresarial “Loterías y Apuestas del Estado”.
	G02("G02",WithholdingDetailKey.G),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido en el artículo 115.1 del Reglamento del Impuesto sobre la Renta de las Personas Físicas, para los rendimientos satisfechos a contribuyentes que inicien el ejercicio de actividades profesionales, tanto en el periodo impositivo en que se produzca dicho inicio como en los dos siguientes.
	G03("G03",WithholdingDetailKey.G),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido con carácter general en el artículo 115.4. del Reglamento del Impuesto sobre la Renta de las Personas Físicas.
	H01("H01",WithholdingDetailKey.H),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido en el artículo 115.4. del Reglamento del Impuesto sobre la Renta de las Personas Físicas, para los rendimientos que sean contraprestación de actividades ganaderas de engorde de porcino y avicultura.
	H02("H02",WithholdingDetailKey.H),
	//Se consignará esta subclave cuando se trate de percepciones a las que resulte aplicable el tipo de retención establecido en el artículo 115.5 del Reglamento del Impuesto sobre la Renta de las Personas Físicas, para los rendimientos que sean contraprestación de actividades forestales.
	H03("H03",WithholdingDetailKey.H),
	//Se consignará esta subclave cuando las percepciones satisfechas sean contraprestación de las actividades económicas en estimación objetiva recogidas en el 115.6,2º del Reglamento del Impuesto.
	H04("H04",WithholdingDetailKey.H),
	//Se consignará esta subclave cuando se trate de percepciones satisfechas por la persona o entidad declarante en concepto de rendimientos procedentes de la cesión del derecho a la explotación del derecho de imagen.
	I01("I01",WithholdingDetailKey.I),
	//Se consignará esta subclave cuando se trate de percepciones satisfechas por la persona o entidad declarante por cualquier otro de los conceptos a que se refiere el artículo 102.2 b) del Reglamento del Impuesto sobre la Renta de las Personas Físicas.
	I02("I02",WithholdingDetailKey.I),
	//Se consignará esta subclave cuando las percepciones correspondan a premios por la participación en juegos, concursos, rifas o combinaciones aleatorias.
	K01("K01",WithholdingDetailKey.K),
	//Se consignará esta subclave cuando las percepciones correspondan a ganacias patrimoniales obtenidas por los vecinas como consecuencia de aprovechamientos forestales.
	K02("K02",WithholdingDetailKey.K),
	//Dietas y asignaciones para gastos de viaje exceptuadas de gravamen conforme a lo previsto en el  artículo 13 del Reglamento del IRPF, y los rendimientos de trabajo recogidos en el artículo 9 punto 18 de la Norma Foral del Impuesto.
	L01("L01",WithholdingDetailKey.L),
	//Prestaciones públicas percibidas como consecuencia de actos de terrorismo que estén exentas en virtud de lo establecido en el punto 1 del artículo 9 de la NF del Impuesto.
	L02("L02",WithholdingDetailKey.L),
	//Ayudas percibidas por los afectados por el virus de la inmunodeficiencia humana a que se refiere el punto 14 del artículo 9 de la NF del Impuesto.
	L03("L03",WithholdingDetailKey.L),
	// Pensiones por lesiones o mutilaciones sufridas con ocasión o como consecuencia de la Guerra Civil 1936/1939 que estén exentas en virtud de lo establecido en el punto 11 del artículo 9 de la NF del Impuesto.
	L04("L04",WithholdingDetailKey.L),
	//Indemnizaciones por despido o cese del trabajador que estén exentas en virtud de lo establecido en el punto 4 del artículo 9 de la NF del Impuesto y el artículo 7 del Reglamento del IRPF.
	L05("L05",WithholdingDetailKey.L),
	//Prestaciones por incapacidad permanente absoluta o gran invalidez que estén exentas conforme a lo establecido en el punto 2 del artículo 9 de la NF del Impuesto.
	L06("L06",WithholdingDetailKey.L),
	//Pensiones por inutilidad o incapacidad permanente del régimen de clases pasivas a que se refiere el punto 3 del artículo 9 de la NF del Impuesto.
	L07("L07",WithholdingDetailKey.L),
	//Las prestaciones familiares de la Seguridad Social por hijo a cargo y demás prestaciones públicas por nacimiento, parto múltiple, adopción e hijos a cargo, así como las pensiones, haberes pasivos y demás prestaciones públicas por situación de orfandad y las prestaciones públicas por maternidad satisfechas por las Comunidades autónomas o las entidades locales, que estén exentas en virtud de lo establecido en el punto 12 del artículo 9 de la NF del Impuesto.
	L08("L08",WithholdingDetailKey.L),
	//Cantidades percibidas de instituciones públicas con motivo de acogimiento de personas que estén exentas en virtud de lo establecido en el artículo 9 punto 10 de la NF del Impuesto.
	L09("L09",WithholdingDetailKey.L),
	//Becas que estén exentas en virtud de lo establecido en el punto 9 del artículo 9 de la NF del Impuesto.
	L10("L10",WithholdingDetailKey.L),
	//Premios literarios, artísticos o científicos relevantes que resulten exentos en virtud de lo establecido en el punto 8 del artículo 9 de la NF del Impuesto, con las condiciones que reglamentariamente se determinen.
	L11("L11",WithholdingDetailKey.L),
	//Ayudas económicas a los y las deportistas de alto nivel que estén exentas en virtud de lo establecido en el punto 15 del artículo 9 de la NF del Impuesto y el artículo 9 del Reglamento del IRPF.
	L12("L12",WithholdingDetailKey.L),
	//Prestaciones por desempleo abonadas en la modalidad de pago único que estén exentas en virtud de lo establecido en el punto 13 del artículo 9 de la NF del Impuesto.
	L13("L13",WithholdingDetailKey.L),
	//Gratificaciones extraordinarias y prestaciones de carácter público por la participación en misiones internacionales de paz, o misiones humanitarias internacionales que estén exentas en virtud de lo establecido en el punto 16del artículo 9 de la NF del Impuesto  y del artículo 10 del Reglamento del IRPF
	L14("L14",WithholdingDetailKey.L),
	//Rendimientos del trabajo percibidos por trabajos realizados en el extranjero que estén exentos en virtud de lo establecido en el punto17 del artículo 9 de la NF del Impuesto  y el artículo 11 del Reglamento del IRPF.
	L15("L15",WithholdingDetailKey.L),
	//Prestaciones por entierro o sepelio que estén exentas en virtud de lo establecido en el punto 23 del artículo 9 de la NF del Impuesto.
	L16("L16",WithholdingDetailKey.L),
	//Ayudas a favor de las personas que hayan desarrollado la hepatitis C como consecuencia de haber recibido tratamiento en el ámbito del sistema sanitario público, que estén exentas en virtud de lo establecido en el punto 14 del artículo 9 de la NF del Impuesto.
	L17("L17",WithholdingDetailKey.L),
	//Los rendimientos del trabajo derivados de las prestaciones obtenidas en forma de renta por las personas con discapacidad que estén exentas en virtud del punto 26 del artículo 9 de la NF del Impuesto.
	L18("L18",WithholdingDetailKey.L),
	//Prestaciones económicas públicas vinculadas al servicio para cuidados en el entorno familiar y de asistencia personalizada que se derivan de la Ley de promoción de la autonomía personal y atención a las personas en situación de dependencia,  que estén exentas en virtud de lo establecido en el punto 27 del artículo 9 de la NF del Impuesto .
	L19("L19",WithholdingDetailKey.L),
	//Otras rentas exentas. Se incluirán en esta subclave las rentas exentas del Impuesto sobre la Renta de las Personas Físicas que, debiendo relacionarse en el modelo 190, sean distintas de las específicamente señaladas en las subclaves anteriores y posteriores.
	L21("L21",WithholdingDetailKey.L),
	//Pensiones no incluidas en la subclave 07 de este apartado que resulten exentas en virtud de lo establecido en el punto 4 del artículo 9 de la NF del Impuesto.
	L22("L22",WithholdingDetailKey.L),
	//Prestaciones por incapacidad permanente total cualificada que resulten exentas en virtud de lo establecido en el punto 2 del artículo 9 de la NF del Impuesto.
	L23("L23",WithholdingDetailKey.L);

	private String subkey;
	private WithholdingDetailKey key;
	
	private WithholdingDetailSubkey( String subkey,WithholdingDetailKey key) {
		this.subkey = subkey;
		this.key = key;
	}
	
	public String getSubkey() {
		return subkey;
	}
	public WithholdingDetailKey getKey() {
		return key;
	}
	
	@Override
	public String getValue() {
		return getSubkey();
	}
	/** Message file base path. */
    private static final String BASE_NAME = "com.code.aon.fiscal.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_withholding_detail_key_";

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