package net.aonsolutions.aon.sii;

public enum SIIDataVariable {

	INVOICE_SUMINISTRO_OK("invoice_sum_ok"),
	INVOICE_SUMINISTRO_ERROR("invoice_sum_ko"),
	INVOICE_BAJA_OK("invoice_baja_ok"),
	INVOICE_BAJA_ERROR("invoice_baja_ko"),
	
	INVOICE_COBROS_PAGOS_OK("invoice_cobros_pagos_ok"),
	INVOICE_COBROS_PAGOS_PARTIAL("invoice_cobros_pagos_p"),
	INVOICE_COBROS_PAGOS_ERROR("invoice_cobros_pagos_ko"),
	
	INVOICE_SUMINISTRO_BIENES_INVERSION_OK("invoice_sum_bien_ok"),
	INVOICE_SUMINISTRO_BIENES_INVERSION_ERROR("invoice_sum_bien_ko"),
	INVOICE_BAJA_BIENES_OK("invoice_baja_bien_ok"),
	INVOICE_BAJA_BIENES_ERROR("invoice_baja_bien_ko"),
	
	INVOICE_SUMINISTRO_OPERACIONES_INTRACOMUNITARIAS_OK("invoice_sum_com_ok"),
	INVOICE_SUMINISTRO_OPERACIONES_INTRACOMUNITARIAS_ERROR("invoice_sum_com_ko"),
	INVOICE_BAJA_OPERACIONES_INTRACOMUNITARIAS_OK("invoice_baja_com_ok"),
	INVOICE_BAJA_OPERACIONES_INTRACOMUNITARIAS_ERROR("invoice_baja_com_ko"),
	
	INVOICE_SUMINISTRO_METALICO_OK("invoice_sum_met_ok"),
	INVOICE_SUMINISTRO_METALICO_ERROR("invoice_sum_met_ko"),
	INVOICE_BAJA_METALICO_OK("invoice_baja_met_ok"),
	INVOICE_BAJA_METALICO_ERROR("invoice_baja_met_ko"),

	INVOICE_SUMINISTRO_SEGUROS_OK("invoice_sum_seguros_ok"),
	INVOICE_SUMINISTRO_SEGUROS_ERROR("invoice_sum_seguros_ko"),
	INVOICE_BAJA_SEGUROS_OK("invoice_baja_seguros_ok"),
	INVOICE_BAJA_SEGUROS_ERROR("invoice_baja_seguros_ko"),

	INVOICE_SUMINISTRO_AGENCIAS_OK("invoice_sum_agencia_ok"),
	INVOICE_SUMINISTRO_AGENCIAS_ERROR("invoice_sum_agencia_ko"),
	INVOICE_BAJA_AGENCIAS_OK("invoice_baja_agencia_ok"),
	INVOICE_BAJA_AGENCIAS_ERROR("invoice_baja_agencia_ko"),

	;
	
	String variable;
	
	
	private SIIDataVariable(String variable) {
		this.variable = variable;
	}
	
	public byte value() {
		return (byte) this.ordinal();
	}
	
	public String getVariable() {
		return variable;
	}
}
