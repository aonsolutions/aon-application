package com.esferalia.aon.occam.api.model.type;

public enum Mod1902025BizkaiaKey {
	 A ("A - Rendimientos del trabajo: Empleados por cuenta ajena en general."
		 ,null
	 )
	,B ("B - Rendimientos del trabajo: Pensionistas y perceptores de haberes pasivos y dem\u00E1s ...."
		,new String[] { "01", "03", "10", "11", "12", "13", "14", "20", "21", "22", "23", "24", "25", "26", "27", "99"}
	)
	,C ("C - Rendimientos del trabajo: Prestaciones o subsidios por desempleo."
		,null
	)
	,E ("E - Rendimientos del trabajo: Consejeros y administradores."
		,new String[] { "01", "04"  }
	)
	,F ("F - Rendimientos del trabajo: Cursos, conferencias, seminarios y similares y elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."
		,new String[] { "01", "02", "03" }
	) 
	,G ("G - Rendimientos de actividades econ\u00F3micas: Actividades profesionales."
		,new String[] { "01", "02", "03", "04", "05", "06", "07", "08"}
	)
	,H ("H - Rendimientos de actividades econ\u00F3micas: actividades agr\u00EDcolas, ganaderas y forestales ..."
		,new String[] { "01", "02", "03", "04" }
	)
	,I ("I - Rendimientos de actividades econ\u00F3micas: rendimientos a que se refiere el art\u00EDculo 75.2, letra b), del Reglamento del Impuesto."
		,new String[] { "01", "02", "03" }
	)
	,K ("K - Premios y ganancias patrimoniales de los vecinos derivadas de los aprovechamientos forestales en montes p\u00FAblicos."
		,new String[] { "01", "03", "04", "05" }
	)
	,L ("L - Rentas exentas y dietas exceptuadas de gravamen."
		,new String[] { 
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10",	"11", "12", "13", "14", "15", "16", "17", "18", "19", 
			"22", "23", "24", "27", "28", "29", "35", "36", "40", "41", "42", "45", "46", "99"}
	)
	;

	private String description;
	private String[] subKeys;

	private Mod1902025BizkaiaKey(String description, String[] subKeys) {
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
	
	public static Mod1902025BizkaiaKey getDefaultKeyForProfessionalRetentions() {
		return Mod1902025BizkaiaKey.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod1902025BizkaiaKey.G.subKeys[0];
	}
	public static String getDefaultSubkeyForNewProfessionalRetentions() {
		return Mod1902025BizkaiaKey.G.subKeys[2];
	}
	public static Mod1902025BizkaiaKey getDefaultKeyForFarmerRetentions() {
		return Mod1902025BizkaiaKey.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod1902025BizkaiaKey.H.subKeys[0];
	}
	public static Mod1902025BizkaiaKey getDefaultKeyForTransportRetentions() {
		return Mod1902025BizkaiaKey.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod1902025BizkaiaKey.H.subKeys[3];
	}
}
