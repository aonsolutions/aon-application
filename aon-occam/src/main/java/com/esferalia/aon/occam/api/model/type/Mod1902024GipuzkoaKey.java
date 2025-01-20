package com.esferalia.aon.occam.api.model.type;

public enum Mod1902024GipuzkoaKey {
	 A ("A - Rendimientos del trabajo: Empleados por cuenta ajena en general"
		 ,null
	 )
	,B ("B - Rendimientos del trabajo: Pensionistas y perceptores de haberes pasivos"
		,new String[] { "01", "03", "10", "11", "12", "13", "14", "20", "21", "22", "23", "24", "25", "26", "27"}
	)
	,C ("C - Rendimientos del trabajo: Prestaciones o subsidios por desempleo"
		,null
	)
	,E ("E - Rendimientos del trabajo: Personas consejeras y administradoras"
		,new String[] { "01", "04"  }
	)
	,F ("F - Rendimientos del trabajo: Cursos, conferencias, seminarios y similares y elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas"
		,new String[] { "01", "02", "03", "04", "05", "06", "07" }
	) 
	,G ("G - Rendimientos de actividades econ\u00F3micas: Rendimientos de actividades profesionales"
		,new String[] { "01", "02", "03", "04", "05", "06", "07", "08"}
	)
	,H ("H - Rendimientos de actividades econ\u00F3micas: Rendimientos de actividades agr\u00EDcolas, ganaderas y forestales y actividades en estimaci\u00F3n objetiva sujetas a retenci\u00F3n"
		,new String[] { "01", "02", "03", "04" }
	)
	,I ("I - Rendimientos de actividades econ\u00F3micas: Rendimientos a que se refiere el art\u00EDculo 93.2.b) del Reglamento del IRPF"
		,new String[] { "01", "02", "03" }
	)
	,K ("K - Premios"
		,new String[] { "01", "03" }
	)
	,L ("L - Rentas exentas y dietas exceptuadas de gravamen."
		,new String[] { 
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12", "13", "14", "15", "16", "17", "18" , "19",  
			"22", "23", "24", "27", "28" , "29", "35", "36", "37", "42", "44", "99"}
	)
	;

	private String description;
	private String[] subKeys;

	private Mod1902024GipuzkoaKey(String description, String[] subKeys) {
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
	
	public static Mod1902024GipuzkoaKey getDefaultKeyForProfessionalRetentions() {
		return Mod1902024GipuzkoaKey.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod1902024GipuzkoaKey.G.subKeys[0];
	}
	public static String getDefaultSubkeyForNewProfessionalRetentions() {
		return Mod1902024GipuzkoaKey.G.subKeys[2];
	}
	public static Mod1902024GipuzkoaKey getDefaultKeyForFarmerRetentions() {
		return Mod1902024GipuzkoaKey.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod1902024GipuzkoaKey.H.subKeys[0];
	}
	public static Mod1902024GipuzkoaKey getDefaultKeyForTransportRetentions() {
		return Mod1902024GipuzkoaKey.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod1902024GipuzkoaKey.H.subKeys[3];
	}
}
