package com.esferalia.aon.payroll.enumeration;

import java.util.Locale;
import java.util.ResourceBundle;

import com.code.aon.common.enumeration.IResourceable;

public enum _ContractType implements IResourceable {
	
	PE166(ContractModel.PE166, 
			ContractCode.C139, ContractCode.C239),
			
	PE170(ContractModel.PE170, 
			ContractCode.C100, ContractCode.C200),
	
	PE174_EXCLUSION(ContractModel.PE174, 
			ContractCode.C150, ContractCode.C250,
			ContractCode.C350, ContractCode.C450,
			ContractCode.C550),
	
	PE174_VIOLENCE(ContractModel.PE174, 
			ContractCode.C150, ContractCode.C250,
			ContractCode.C350, ContractCode.C450,
			ContractCode.C550),
	
	PE175(ContractModel.PE175), 
	
	PE176(ContractModel.PE176), 
	
	PE177_WORK_SERVICE(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
			
	PE177_PRODUCTION(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
	
	PE177_INTERIM(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
	
	PE177_INTERIM_SUSTITUTION(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
			
	PE177_FORMATION(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
	
	PE177_VIOLENCE(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 

	PE177_DISABLED_PERSON_SUSTITUTION(ContractModel.PE177, 
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C501,
			ContractCode.C502, ContractCode.C510,
			ContractCode.C540), 
					
	PE179(ContractModel.PE179, 
			ContractCode.C410, ContractCode.C510),
			
	PE181(ContractModel.PE181, 
			ContractCode.C350, ContractCode.C300),

	PE182(ContractModel.PE182, 
			ContractCode.C441, ContractCode.C541),
			
	PE183(ContractModel.PE183, 
			ContractCode.C109, ContractCode.C209, 
			ContractCode.C189, ContractCode.C289),

	PE185(ContractModel.PE185, 
			ContractCode.C309, ContractCode.C389),
	
	PE186(ContractModel.PE186, 
			ContractCode.C130, ContractCode.C230,
			ContractCode.C330),
			
	PE187(ContractModel.PE187, 
			ContractCode.C430, ContractCode.C530),
			
	PE190(ContractModel.PE190, 
			ContractCode.C150, ContractCode.C250,
			ContractCode.C401, ContractCode.C402,
			ContractCode.C410, ContractCode.C420,
			ContractCode.C430, ContractCode.C421,
			ContractCode.C501, ContractCode.C502,
			ContractCode.C510, ContractCode.C520,
			ContractCode.C530, ContractCode.C540,
			ContractCode.C541, ContractCode.C990),
			
	PE191(ContractModel.PE191),
	
	PE192(ContractModel.PE192),
			
	PE193(ContractModel.PE193, 
			ContractCode.C450, ContractCode.C550),
					
	PE195(ContractModel.PE195, 
			ContractCode.C420, ContractCode.C520),
			
	PE196(ContractModel.PE196, 
			ContractCode.C401, ContractCode.C501),
			
	PE197(ContractModel.PE197),
	
	PE200 (ContractModel.PE200),
			
	PE201 (ContractModel.PE201, 
			ContractCode.C109, ContractCode.C209,
			ContractCode.C309, ContractCode.C189,
			ContractCode.C289, ContractCode.C389),
			
	PE202 (ContractModel.PE202, 
			ContractCode.C150, ContractCode.C100,
			ContractCode.C450, ContractCode.C401,
			ContractCode.C402, ContractCode.C410,
			ContractCode.C421, ContractCode.C420,
			ContractCode.C990),
	
	PE203 (ContractModel.PE203),

	PE204 (ContractModel.PE204),
	
	PE205(ContractModel.PE205, 
			ContractCode.C130, ContractCode.C230,
			ContractCode.C330),
			
	PE206 (ContractModel.PE206, 
			ContractCode.C401, ContractCode.C402, 
			ContractCode.C410, ContractCode.C420,
			ContractCode.C421, ContractCode.C450,
			ContractCode.C501, ContractCode.C502, 
			ContractCode.C510, ContractCode.C520,
			ContractCode.C550, ContractCode.C990),
			
	PE217 (ContractModel.PE217),
					
	PE220(ContractModel.PE220, 
			ContractCode.C450, ContractCode.C550),
			
	PE221_GT_45(ContractModel.PE221, 
			ContractCode.C150, ContractCode.C250),
	
	PE221_16_30(ContractModel.PE221, 
			ContractCode.C150, ContractCode.C250),

	PE213_SHOE(ContractModel.PE213, 
			ContractCode.C150, ContractCode.C250, 
			ContractCode.C350),
	
	PE213_TOY_FURNITURE(ContractModel.PE213, 
			ContractCode.C150, ContractCode.C250, 
			ContractCode.C350),
					
	PE218_ORDINARY(ContractModel.PE218),
	PE218_INSERTION(ContractModel.PE218),
	;
	
	/** Message file base path. */
    private static final String BASE_NAME = "com.esferalia.aon.payroll.i18n.messages";
    
    /** Message key prefix. */
    private static final String MSG_KEY_PREFIX = "aon_enum_contract_type_";
	
	private ContractModel model;
	private ContractCode [] codes;
	
	private _ContractType(ContractModel model, ContractCode... codes) {
		this.model = model;
		this.codes = codes;
	}
	
	@Override
	public String getName(Locale locale) {
		ResourceBundle bundle = ResourceBundle.getBundle(BASE_NAME, locale); 
		return bundle.getString(MSG_KEY_PREFIX + toString());
	}
	
	public ContractModel getModel(){
		return model;
	}
	
	public ContractCode[] getCodes(){
		return codes;
	}
	
	
}
