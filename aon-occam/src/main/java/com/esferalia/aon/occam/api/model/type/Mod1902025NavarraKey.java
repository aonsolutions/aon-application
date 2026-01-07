package com.esferalia.aon.occam.api.model.type;

public enum Mod1902025NavarraKey {
	 A ("A - Rendimientos del trabajo: Empleados por cuenta ajena en general."
		,null
	 )
	,B ("B - Rendimientos del trabajo: Pensionistas y perceptores de haberes pasivos y dem\u00E1s ...."
		,new String[] { "01", "03", "04", "90", "99" }
	)
	,C ("C - Rendimientos del trabajo: Prestaciones o subsidios por desempleo."
		,new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09" }
	)
	,D ("D - Rendimientos del trabajo: Prestaciones por desempleo abonadas en la modalidad de pago \u00FAnico."
		,null
	)
	,E ("E - Rendimientos del trabajo: Consejeros y administradores."
		,new String[] { "01", "02", "03", "04" }
	)
	,F ("F - Rendimientos del trabajo: Otros rendimientos de trabajo, incluidas percepciones por elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."
		,new String[] { "01", "03", "04", "05", "06", "07" }
	) 
	,G ("G - Rendimientos de actividades econ\u00F3micas: Actividades profesionales."
		,new String[] { "01", "02", "04", "05", "06", "07", "08", "09"}
	)
	,H ("H - Rendimientos de actividades econ\u00F3micas: Actividades agr\u00EDcolas, ganaderas y forestales ..."
		,new String[] { "01", "02", "03", "04" }
	)
	,I ("I - Rendimientos de actividades empresariales: Propiedad intelectual o industrial, asistencia t\u00E9cnica, arrendamiento de bienes muebles, negocios o minas ..."
		,new String[] { "01", "02", "03" }
	)
	,J ("J - Imputaci\u00F3n de rentas por la cesi\u00F3n de derechos de imagen"
		,null
	)
	,K ("K - Premios."
		,new String[] { "01", "03" }
	)
	,L ("L - Rentas exentas y dietas exceptuadas de gravamen."
		,new String[] { "01", "02", "03", "04", "05", "06" , "07", "08", "09", "10", "11", "12" , "13", "14", "15", "16", "17", "19", "20", "22", "23", "24", "25", "26", "28", "29", "30", "31", "32", "33", "99" }
	)
	,Y ("Y - Retribuci\u00F3n en especie por la bonificaci\u00F3n de intereses de pr\u00E9stamos concedidos a los empleados."
			,new String[] { "01", "02", "03" }
	)
	,Z ("Z - Contribuciones o aportaciones imputables fiscalmente a los trabajadores por empresas o entidades acogidas a sistemas alternativos de cobertura ..."
			,new String[] { "01", "02" }
	)
	;

	private String description;
	private String[] subKeys;

	private Mod1902025NavarraKey(String description, String[] subKeys) {
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
	
	public static Mod1902025NavarraKey getDefaultKeyForProfessionalRetentions() {
		return Mod1902025NavarraKey.G;
	}
	public static String getDefaultSubkeyForProfessionalRetentions() {
		return Mod1902025NavarraKey.G.subKeys[0];
	}
	public static String getDefaultSubkeyForNewProfessionalRetentions() {
		return Mod1902025NavarraKey.G.subKeys[2];
	}
	public static Mod1902025NavarraKey getDefaultKeyForFarmerRetentions() {
		return Mod1902025NavarraKey.H;
	}
	public static String getDefaultSubkeyForFarmerRetentions() {
		return Mod1902025NavarraKey.H.subKeys[0];
	}
	public static Mod1902025NavarraKey getDefaultKeyForTransportRetentions() {
		return Mod1902025NavarraKey.H;
	}
	public static String getDefaultSubkeyForTransportRetentions() {
		return Mod1902025NavarraKey.H.subKeys[3];
	}
}
