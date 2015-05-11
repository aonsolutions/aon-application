package com.esferalia.aon.occam.api.model.type;

import com.esferalia.aon.occam.api.model.fiscal.IFiscalModelKey;
import com.esferalia.aon.watson.util.AonStringUtils;

public enum Mod111Key implements IFiscalModelKey {
	// Á --> \u00C1 á --> \u00E1
	// É --> \u00C9 é --> \u00E9
	// Í --> \u00CD í --> \u00ED
	// Ó --> \u00D3 ó --> \u00F3
	// Ú --> \u00DA ú --> \u00FA
	// Ñ --> \u00D1 ñ --> \u00F1
	// º --> \u00AA ª --> \u00BA
	// ¿ --> \u00BF

	
	 AR_H1 ("111-AR-H1" ,null,Administration.ALAVA,"TRABAJO")
	,AR_H11("111-AR-H11",null,Administration.ALAVA,"Rendimientos procedentes del trabajo o servicios que se presten en el Territorio Hist\u00F3rico de \u00C1lava")
	,AR_C01("111-AR-01" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C02("111-AR-02" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C03("111-AR-03" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H12("111-AR-H12",null,Administration.ALAVA,"Pensiones ")
	,AR_C04("111-AR-04" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C05("111-AR-05" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C06("111-AR-06" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H13("111-AR-H13",null,Administration.ALAVA,"Retribuciones de los miembros del Consejo de Administraci\u00F3n y Juntas que hagan sus veces de entidades con domicilio fiscal en \u00C1lava")
	,AR_C07("111-AR-07" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C08("111-AR-08" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C09("111-AR-09" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H14("111-AR-H14",null,Administration.ALAVA,"Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributan en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del volumen de operaciones) ")
	,AR_C10("111-AR-10" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C11("111-AR-11" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C12("111-AR-12" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H5 ("111-AR-H2" ,null,Administration.ALAVA,"ACTIVIDADES PROFESIONALES")
	,AR_C13("111-AR-13" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C14("111-AR-14" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C15("111-AR-15" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H6 ("111-AR-H3" ,null,Administration.ALAVA,"ACTIVIDADES ECON\u00D3MICAS EN ESTIMACI\u00D3N OBJETIVA, MODALIDAD SIGNOS, \u00CDNDICES O M\u00D3DULOS")
	,AR_C16("111-AR-16" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C17("111-AR-17" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C18("111-AR-18" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H7 ("111-AR-H4" ,null,Administration.ALAVA,"ACTIV. AGR\u00CDCOLAS, GANADERAS Y FORESTALES")
	,AR_C19("111-AR-19" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C20("111-AR-20" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C21("111-AR-21" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H8 ("111-AR-H5" ,null,Administration.ALAVA,"PREMIOS")
	,AR_C22("111-AR-22" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C23("111-AR-23" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C24("111-AR-24" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_H9 ("111-AR-H6" ,null,Administration.ALAVA,"RETRIBUCIONES EN ESPECIE Y OTRAS")
	,AR_C25("111-AR-25" ,null,Administration.ALAVA,"N\u00AA de perceptores")
	,AR_C26("111-AR-26" ,null,Administration.ALAVA,"Importe percepciones")
	,AR_C27("111-AR-27" ,null,Administration.ALAVA,"Retenciones e ingresos a cuenta")
	,AR_C28("111-AR-28" ,null,Administration.ALAVA,"Total")
	,AR_C29("111-AR-29" ,null,Administration.ALAVA,"Recargo Pr\u00F3rroga")
	,AR_C30("111-AR-30" ,null,Administration.ALAVA,"Intereses demora")
	,AR_C31("111-AR-31" ,null,Administration.ALAVA,"Deuda tributaria a ingresar")
	
	,BZ_H1 ("111-BZ-H1" ,null,Administration.BIZKAIA,"Rendimientos procedentes de trabajos o servicios que se presten en Bizkaia ")
	,BZ_C01("111-BZ-01" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C02("111-BZ-02" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C03("111-BZ-03" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H2 ("111-BZ-H2" ,null,Administration.BIZKAIA,"Retribuciones de miembros de Consejos de Administraci\u00F3n y Juntas que hagan sus veces de empresas o entidades con domicilio fiscal en Bizkaia ")
	,BZ_C04("111-BZ-04" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C05("111-BZ-05" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C06("111-BZ-06" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H3 ("111-BZ-H3" ,null,Administration.BIZKAIA,"Retribuciones de las personas a que se refiere el apartado anterior de empresas o entidades que tributen en proporci\u00F3n al volumen de operaciones (previa aplicaci\u00F3n del porcentaje) ")
	,BZ_C07("111-BZ-07" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C08("111-BZ-08" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C09("111-BZ-09" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H4 ("111-BZ-H4" ,null,Administration.BIZKAIA,"Rendimientos de trabajo en per\u00EDodos inferiores al a\u00F1o, trabajos de temporada o trabajos circunstanciales ")
	,BZ_C10("111-BZ-10" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C11("111-BZ-11" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C12("111-BZ-12" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H5 ("111-BZ-H5" ,null,Administration.BIZKAIA,"Prestaciones por desempleo ")
	,BZ_C13("111-BZ-13" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C14("111-BZ-14" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C15("111-BZ-15" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H6 ("111-BZ-H6" ,null,Administration.BIZKAIA,"Pensiones y haberes pasivos ")
	,BZ_C16("111-BZ-16" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C17("111-BZ-17" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C18("111-BZ-18" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H7 ("111-BZ-H7" ,null,Administration.BIZKAIA,"Rendimientos satisfechos por contraprestaciones profesionales, art\u00EDsticas o deportivas y retribuciones de comisionistas, agentes comerciales, agentes de seguros y subagentes")
	,BZ_C19("111-BZ-19" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C20("111-BZ-20" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C21("111-BZ-21" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H8 ("111-BZ-H8" ,null,Administration.BIZKAIA,"Rendimientos de actividades econ\u00F3micas en estimaci\u00F3n objetiva, modalidad de signos, \u00EDndices o m\u00F3dulos")
	,BZ_C22("111-BZ-22" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C23("111-BZ-23" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C24("111-BZ-24" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H9 ("111-BZ-H9" ,null,Administration.BIZKAIA,"Retenciones sobre rendimientos de actividades agr\u00EDcolas, ganaderas y forestales")
	,BZ_C25("111-BZ-25" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C26("111-BZ-26" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C27("111-BZ-27" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H10("111-BZ-H10",null,Administration.BIZKAIA,"Retribuciones en especie ")
	,BZ_C28("111-BZ-28" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C29("111-BZ-29" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C30("111-BZ-30" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H11("111-BZ-H11",null,Administration.BIZKAIA,"Premios")
	,BZ_C31("111-BZ-31" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C32("111-BZ-32" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C33("111-BZ-33" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_H12("111-BZ-H12",null,Administration.BIZKAIA,"Rendimientos no comprendidos en apartados anteriores")
	,BZ_C34("111-BZ-34" ,null,Administration.BIZKAIA,"N\u00AA de contribuyentes")
	,BZ_C35("111-BZ-35" ,null,Administration.BIZKAIA,"Cantidades abonadas")
	,BZ_C36("111-BZ-36" ,null,Administration.BIZKAIA,"Retenciones")
	,BZ_C37("111-BZ-37" ,null,Administration.BIZKAIA,"Deuda tributaria a ingresar")
	
	,CT_H1 ("111-CT-H1" ,null,Administration.COMMON_TERRITORY,"I. Rendimientos del trabajo")
	,CT_H11("111-CT-H11",null,Administration.COMMON_TERRITORY,"Rendimientos dinerarios")
	,CT_C01("111-CT-01" ,"01",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C02("111-CT-02" ,"02",Administration.COMMON_TERRITORY,"Imp. percepciones")
	,CT_C03("111-CT-03" ,"03",Administration.COMMON_TERRITORY,"Imp. retenciones")
	,CT_H12("111-CT-H12",null,Administration.COMMON_TERRITORY,"Rendimientos en especie")
	,CT_C04("111-CT-04" ,"04",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C05("111-CT-05" ,"05",Administration.COMMON_TERRITORY,"Valor percep. especie")
	,CT_C06("111-CT-06" ,"06",Administration.COMMON_TERRITORY,"Imp. ingresos a cuenta")
	,CT_H2 ("111-CT-H2" ,null,Administration.COMMON_TERRITORY,"II. Rendimientos de actividades econ\u00F3micas")
	,CT_H21("111-CT-H21",null,Administration.COMMON_TERRITORY,"Rendimientos dinerarios")
	,CT_C07("111-CT-07" ,"07",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C08("111-CT-08" ,"08",Administration.COMMON_TERRITORY,"Imp. percepciones")
	,CT_C09("111-CT-09" ,"09",Administration.COMMON_TERRITORY,"Imp. retenciones")
	,CT_H22("111-CT-H22",null,Administration.COMMON_TERRITORY,"Rendimientos en especie")
	,CT_C10("111-CT-10" ,"10",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C11("111-CT-11" ,"11",Administration.COMMON_TERRITORY,"Valor percep. especie")
	,CT_C12("111-CT-12" ,"12",Administration.COMMON_TERRITORY,"Imp. ingresos a cuenta")
	,CT_H3 ("111-CT-H3" ,null,Administration.COMMON_TERRITORY,"III. Premios por la participaci\u00F3n en juegos, concursos, rifas o combinaciones aleatorias")
	,CT_H31("111-CT-H31",null,Administration.COMMON_TERRITORY,"Premios dinerarios")
	,CT_C13("111-CT-13" ,"13",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C14("111-CT-14" ,"14",Administration.COMMON_TERRITORY,"Imp. percepciones")
	,CT_C15("111-CT-15" ,"15",Administration.COMMON_TERRITORY,"Imp. retenciones")
	,CT_H32("111-CT-H32",null,Administration.COMMON_TERRITORY,"Premios en especie")
	,CT_C16("111-CT-16" ,"16",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C17("111-CT-17" ,"17",Administration.COMMON_TERRITORY,"Valor percep. especie")
	,CT_C18("111-CT-18" ,"18",Administration.COMMON_TERRITORY,"Imp. ingresos a cuenta")
	,CT_CH4("111-CT-H4" ,null,Administration.COMMON_TERRITORY,"IV. Ganancias patrimoniales derivadas de los aprovechamientos forestales de los vecinos en los montes p\u00FAblicos")
	,CT_H41("111-CT-H41",null,Administration.COMMON_TERRITORY,"Percepciones dinerarias")
	,CT_C19("111-CT-19" ,"19",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C20("111-CT-20" ,"20",Administration.COMMON_TERRITORY,"Imp. percepciones")
	,CT_C21("111-CT-21" ,"21",Administration.COMMON_TERRITORY,"Imp. retenciones")
	,CT_H42("111-CT-H42",null,Administration.COMMON_TERRITORY,"Percepciones en especie")
	,CT_C22("111-CT-22" ,"22",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C23("111-CT-23" ,"23",Administration.COMMON_TERRITORY,"Valor percep. especie")
	,CT_C24("111-CT-24" ,"24",Administration.COMMON_TERRITORY,"Imp. ingresos a cuenta")
	,CT_CT5("111-CT-H5" ,null,Administration.COMMON_TERRITORY,"V. Contraprestaciones por la cesi\u00F3n de derechos de imagen, ingresos a cuenta previstos en el art\u00EDculo 92.8 de la Ley del Impuesto ")
	,CT_H51("111-CT-H51",null,Administration.COMMON_TERRITORY,"Contrapartidas dinerarias o en especie")
	,CT_C25("111-CT-25" ,"25",Administration.COMMON_TERRITORY,"N\u00AA de Perceptores")
	,CT_C26("111-CT-26" ,"26",Administration.COMMON_TERRITORY,"Contrap. satisfechas")
	,CT_C27("111-CT-27" ,"27",Administration.COMMON_TERRITORY,"Imp. ingresos a cuenta")
	,CT_H6 ("111-CT-H6" ,null,Administration.COMMON_TERRITORY,"Total liquidaci\u00F3n")
	,CT_C28("111-CT-28" ,"28",Administration.COMMON_TERRITORY,"Suma de retenciones e ingresos a cuenta")
	,CT_C29("111-CT-29" ,"29",Administration.COMMON_TERRITORY,"A deducir (exclusivamentes en caso de decl. complm.): Resultados a ingresar de anteriores declaraciones por el mismo concepto, ejercicio y periodo.")
	,CT_C30("111-CT-30" ,"30",Administration.COMMON_TERRITORY,"Resultado a ingresar")
	
	,GP_H1 ("111-GP-H1" ,null,Administration.GIPUZKOA,"RETENCIONES")
	,GP_H11("111-GP-H11",null,Administration.GIPUZKOA,"RENDIMIENTOS DEL TRABAJO")
	,GP_C01("111-GP-01" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C02("111-GP-02" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C03("111-GP-03" ,null,Administration.GIPUZKOA,"Retenciones")
	,GP_H12("111-GP-H12",null,Administration.GIPUZKOA,"RENDIMIENTOS DE ACTIVIDADES ECONOMICAS")
	,GP_C04("111-GP-04" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C05("111-GP-05" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C06("111-GP-06" ,null,Administration.GIPUZKOA,"Retenciones")
	,GP_H13("111-GP-H13",null,Administration.GIPUZKOA,"RENDIMIENTOS DE ACTIV. AGR\u00CDCOLAS, GANADERAS Y FORESTALES")
	,GP_C07("111-GP-07" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C08("111-GP-08" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C09("111-GP-09" ,null,Administration.GIPUZKOA,"Retenciones")
	,GP_H14("111-GP-H14",null,Administration.GIPUZKOA,"PREMIOS")
	,GP_C10("111-GP-10" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C11("111-GP-11" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C12("111-GP-12" ,null,Administration.GIPUZKOA,"Retenciones")
	,GP_H2 ("111-GP-H2" ,null,Administration.GIPUZKOA,"INGRESOS A CUENTA")
	,GP_H21("111-GP-H21",null,Administration.GIPUZKOA,"RENDIMIENTOS DEL TRABAJO")
	,GP_C13("111-GP-13" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C14("111-GP-14" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C15("111-GP-15" ,null,Administration.GIPUZKOA,"Ingresos a cuenta")
	,GP_H22("111-GP-H22",null,Administration.GIPUZKOA,"RENDIMIENTOS DE ACTIVIDADES ECONOMICAS")
	,GP_C16("111-GP-16" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C17("111-GP-17" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C18("111-GP-18" ,null,Administration.GIPUZKOA,"Ingresos a cuenta")
	,GP_H23("111-GP-H23",null,Administration.GIPUZKOA,"RENDIMIENTOS DE ACTIV. AGR\u00CDCOLAS, GANADERAS Y FORESTALES")
	,GP_C19("111-GP-19" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C20("111-GP-20" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C21("111-GP-21" ,null,Administration.GIPUZKOA,"Ingresos a cuenta")
	,GP_H24("111-GP-H24",null,Administration.GIPUZKOA,"PREMIOS")
	,GP_C22("111-GP-22" ,null,Administration.GIPUZKOA,"N\u00AA de perceptores")
	,GP_C23("111-GP-23" ,null,Administration.GIPUZKOA,"Importe percepciones")
	,GP_C24("111-GP-24" ,null,Administration.GIPUZKOA,"Ingresos a cuenta")
	,GP_C25("111-GP-25" ,null,Administration.GIPUZKOA,"TOTAL A INGRESAR")
	
	,NF_A1("111-NF-A1",null,Administration.NAVARRA,"Cantidad a Ingresar")
	;
	
	private String value;
	private String box;
	private Administration admon;
	private String description;

	private Mod111Key(String value, String box, Administration admon,
			String description) {
		this.value = value;
		this.box = box;
		this.admon =  admon;
		this.description = description;
	}
	public String getValue() {
		return value;
	}
	public String getBox() {
		return box;
	}
	public String getDescription() {
		return description;
	}
	public boolean accept(Administration administration, Period period, int year) {
		return  (administration == null || this.admon == administration);
	}
	
	public static Mod111Key getKey(String value) {
		for (Mod111Key key : Mod111Key.values()) {
			if (AonStringUtils.equals(key.getValue(), value)) {
				return key;
			}
		}
		return null;
	}
}
