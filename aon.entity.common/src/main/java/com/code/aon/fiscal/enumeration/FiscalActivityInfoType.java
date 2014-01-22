package com.code.aon.fiscal.enumeration;

public enum FiscalActivityInfoType {
	
	INFO,
	VAT_MODULE,
	IRPF_MODULE,
	VAT_INFO,
	IRPF_INFO,
	MODULE_DETAIL,
	M311_DETAIL,
	
	// Valido sólo en el enumerado FiscalActivityInfoKey,
	// puesto que los modulos pueden ser tanto de IVA como de IRPF.
	// No se debe grabar el valor en la tabla.
	MODULE;
}