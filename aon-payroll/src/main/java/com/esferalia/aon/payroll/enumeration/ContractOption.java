package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum ContractOption implements IResourceable {
	
	INDEFINITE(ContractType.PE170),
	BONUS_INDEFINITE(ContractType.PE221_16_30, 
			ContractType.PE221_GT_45, ContractType.PE181, 
			ContractType.PE213_SHOE, ContractType.PE213_TOY_FURNITURE, 
			ContractType.PE183, ContractType.PE185),
	BONUS_INDEFINITE_TEMPORAL(ContractType.PE174_EXCLUSION, 
			ContractType.PE174_VIOLENCE, ContractType.PE202, 
			ContractType.PE218_INSERTION, ContractType.PE218_ORDINARY),
	EDUCATIONAL(ContractType.PE175, ContractType.PE176),
	PART_DURATION(ContractType.PE177_WORK_SERVICE,
			ContractType.PE177_PRODUCTION, ContractType.PE177_INTERIM,
			ContractType.PE177_INTERIM_SUSTITUTION, ContractType.PE179,
			ContractType.PE206, ContractType.PE177_FORMATION,
			ContractType.PE177_VIOLENCE, ContractType.PE193,
			ContractType.PE220),
	TAKEOVER(ContractType.PE182),
	DISABLED_PERSON(ContractType.PE186,
			ContractType.PE205, ContractType.PE187,
			ContractType.PE175, ContractType.PE175,
			ContractType.PE177_DISABLED_PERSON_SUSTITUTION, ContractType.PE201,
			ContractType.PE166, ContractType.PE190),
	INVESTIGATION(ContractType.PE195,
			ContractType.PE196),
	ADVANCE_RETIREMENT_SUSTITUTION(),
	OTHERS(),
	ANNEX(ContractType.PE200,
			ContractType.PE192, ContractType.PE191,
			ContractType.PE197, ContractType.PE204,
			ContractType.PE203, ContractType.PE217);
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_option_";
	
	private ContractType[] type;
	
	private ContractOption(ContractType... type) {
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
