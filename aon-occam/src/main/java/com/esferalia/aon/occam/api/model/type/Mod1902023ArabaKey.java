package com.esferalia.aon.occam.api.model.type;

public enum Mod1902023ArabaKey {
	 A ("A - Rendimientos del trabajo: Empleados por cuenta ajena en general"
		 ,null
	 )
	,B ("B - Pensionistas y perceptores de haberes pasivos y dem\u00E1s prestaciones previstas en el art\u00EDculo 18.a) de la Norma Foral del Impuesto"
		,new String[] { "01", "03", "10", "11", "12", "13", "14", "20", "21", "22", "23", "24", "25", "26", "27"}
	)
	,C ("C - Prestaciones o subsidios por desempleo"
		,null
	)
	,E ("E - Consejeros y administradores"
		,new String[] { "01", "04"  }
	)
	,F ("F - Cursos, conferencias, seminarios y similares y elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas"
		,new String[] { "01", "02", "03", "04" }
	) 
	,G ("G - Rendimientos de actividades econ\u00F3micas: Actividades profesionales"
		,new String[] { "01", "02", "03", "04", "05", "06", "07", "08"}
	)
	,H ("H - Rendimientos de actividades econ\u00F3micas: Actividades agr\u00EDcolas, ganaderas y forestales y actividades empresariales en E.O. sujetos a retenci\u00F3n"
		,new String[] { "01", "02", "03", "04" }
	)
	,I ("I - Rendimientos de actividades econ\u00F3micas: Rendimientos a que se refiere el art\u00EDculo 77.2, b) del Reglamento del Impuesto"
		,new String[] { "01", "02", "03" }
	)
	,J ("J - Imputaci\u00F3n de rentas por la cesi\u00F3n de derechos de imagen"
		,null
)
	,K ("K - Premios"
		,new String[] { "01", "03" }
	)
	,L ("L - Rentas exentas y dietas exceptuadas de gravamen"
		,new String[] { 
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",	"11", "12", "13", "14", "15", "16", "17", "18" , "19", "20", 
			"22", "23", "24", "27", "28" , "29", "35", "36", "41", "42", "44", "99"}
	)
	,Z ("Z - Rendimientos de trabajo derivados de participaciones de acciones, participaciones u otros derechos econ\u00F3micos especiales de cualquier tipo de Fondo de Inversi\u00F3n Alternativa"
		,null
	)
	;

	private String description;
	private String[] subKeys;

	private Mod1902023ArabaKey(String description, String[] subKeys) {
		this.description = description;
		this.subKeys = subKeys;
	}
	public String getDescription() {
		return description;
	}
	public boolean hasSubkeys() {
		return this.subKeys != null;
	}

	public String[] getSubKeys() {
		return subKeys;
	}

	public String getValue() {
		return toString();
	}
	
	public static Mod1902023ArabaKey getDefaultKeyForProfessionalRetentions() {
		return Mod1902023ArabaKey.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod1902023ArabaKey.G.subKeys[0];
	}
	public static String getDefaultSubkeyForNewProfessionalRetentions() {
		return Mod1902023ArabaKey.G.subKeys[2];
	}
	public static Mod1902023ArabaKey getDefaultKeyForFarmerRetentions() {
		return Mod1902023ArabaKey.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod1902023ArabaKey.H.subKeys[0];
	}
	public static Mod1902023ArabaKey getDefaultKeyForTransportRetentions() {
		return Mod1902023ArabaKey.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod1902023ArabaKey.H.subKeys[3];
	}
}
