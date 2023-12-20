package com.esferalia.aon.occam.api.model.type;

public enum Mod1902022Key {
	 A ("A - Rendimientos del trabajo: Empleados por cuenta ajena en general."
		 ,null
	 )
	,B ("B - Rendimientos del trabajo: Pensionistas y perceptores de haberes pasivos y dem\u00E1s ...."
		,new String[] { "01", "02", "03"}
	)
	,C ("C - Rendimientos del trabajo: Prestaciones o subsidios por desempleo."
		,null
	)
	,D ("D - Rendimientos del trabajo: Prestaciones por desempleo abonadas en la modalidad de pago \u00FAnico."
		,null
	)
	,E ("E - Rendimientos del trabajo: Consejeros y administradores."
		,new String[] { "01", "02", "03", "04"  }
	)
	,F ("F - Rendimientos del trabajo: Cursos, conferencias, seminarios y similares y elaboraci\u00F3n de obras literarias, art\u00EDsticas o cient\u00EDficas."
		,new String[] { "01", "02" }
	) 
	,G ("G - Rendimientos de actividades econ\u00F3micas: Actividades profesionales."
		,new String[] { "01", "02", "03"}
	)
	,H ("H - Rendimientos de actividades econ\u00F3micas: actividades agr\u00EDcolas, ganaderas y forestales ..."
		,new String[] { "01", "02", "03", "04" }
	)
	,I ("I - Rendimientos de actividades econ\u00F3micas: rendimientos a que se refiere el art\u00EDculo 75.2, letra b), del Reglamento del Impuesto."
		,new String[] { "01", "02" }
	)
	,J ("J - Imputaci\u00F3n de rentas por la cesi\u00F3n de derechos de imagen"
		,null
)
	,K ("K - Premios y ganancias patrimoniales de los vecinos derivadas de los aprovechamientos forestales en montes p\u00FAblicos."
		,new String[] { "01", "02", "03" }
	)
	,L ("L - Rentas exentas y dietas exceptuadas de gravamen."
		,new String[] { 
			"01", "02", "03", "04", "05", "06", "07", "08", "09", "10", 
			"11", "12", "13", "14", "15", "16", "17", "18" , "19", "20", 
			"21", "22", "23", "24", "25", "26", "27", "28" , "29", "30", }
	)
	;

	private String description;
	private String[] subKeys;

	private Mod1902022Key(String description, String[] subKeys) {
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
	
}
