package com.esferalia.aon.occam.api.model.type;

public enum FiscalModelDeclarationType {

	//-------------------------------------------------- FINANCE    -ASK BANK		
	 NEGATIVE 	("N","Negativa, cero \u00F3 sin. act."	,false		,false)
	,DEPOSIT  	("I","Ingreso"							,true		,true )
	,BANK     	("U","Domicializaci\u00F3n"				,true		,true )
	,DEPOSIT_CCT("G","Ingreso a anotar en CCT"			,false		,false)
	,TO_DEDUCE	("B","A deducir"						,false		,false)
	
	,COMPENSATE	("C", "A compensar"						,false		,false)
	,PAYBACK	("D", "A devolver"						,true		,true )
	,PAYBACK_CCT("V", "Devoluci\u00F3n a anotar en CCT"	,false		,false)
	;


	private String value;
	private String description;
	private boolean mustCreateFinance;
	private boolean bankRequired;

	private FiscalModelDeclarationType(String value,String description,boolean mustCreateFinance,boolean bankRequired) {
		this.value = value;
		this.description = description;
		this.mustCreateFinance = mustCreateFinance;
		this.bankRequired = bankRequired;
	}
	
	public String getValue() {
		return value;
	}
	public String getDescription() {
		return description;
	}
	public boolean mustCreateFinance() {
		return mustCreateFinance;
	}
	public boolean isBankRequired() {
		return bankRequired;
	}
	
	public static FiscalModelDeclarationType safeValueOf( String value ) {
		if (value == null) return null;
		for (FiscalModelDeclarationType t : FiscalModelDeclarationType.values()) {
			if (t.getValue().equals(value)) return t;
		}
		return null;
	}
	
}
