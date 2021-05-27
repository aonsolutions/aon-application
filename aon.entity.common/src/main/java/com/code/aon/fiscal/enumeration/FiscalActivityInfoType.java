package com.code.aon.fiscal.enumeration;

public enum FiscalActivityInfoType {
	
	INFO,
	VAT_MODULE,
	IRPF_MODULE,
	VAT_INFO,
	IRPF_INFO,
	MODULE_DETAIL,
	OBSOLETE_M_3_1_1___D_E_T_A_I_L,
	OBSOLETE_M_3_1_1___F_A_R_M_E_R___D_E_T_A_I_L,
	
	// Valido sólo en el enumerado FiscalActivityInfoKey,
	// puesto que los modulos pueden ser tanto de IVA como de IRPF.
	// No se debe grabar el valor en la tabla.
	MODULE;
}