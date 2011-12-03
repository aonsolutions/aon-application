package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractTc2Group implements IResourceable {
	
	
	TEMPORAL_FULL_TIME, //Temporal tiempo completo
	TEMPORAL_PART_TIME, //	Temporal tiempo parcial
	INDEFINITE_FULL_TIME, //	Indefinido tiempo completo
	INDEFINITE_PART_TIME, //	Indefinido tiempo parcial
	HANDICAP, //	Minusválidos
	DISCONTINUOUS_FIXED, //	Fijo discontinuo
	TAKEOVER, //	Relevo
	SOCIAL_COLABORATION, //	Adscripción en colaboración social
	RETIREMENT_AT_64, //	Jubilación especial a los 64 años
	OTHERS, //	Otros contratos
	
	;
	
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_tc2_group_";
	
	private ContractType[] type;
	
	private ContractTc2Group(ContractType... type) {
		this.type = type;
	}
	
	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	public ContractType[] getTypes(){
		return type;
	}
	
	
}
