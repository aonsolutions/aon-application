package net.aonsolutions.aon.sii;

public enum SIIDataVariable {

	INVOICE_SUMINISTRO_OK("invoice_sum_ok"),
	INVOICE_SUMINISTRO_ERROR("invoice_sum_ko"),
	INVOICE_BAJA_OK("invoice_baja_ok"),
	INVOICE_BAJA_ERROR("invoice_baja_ko"),
	INVOICE_COBROS_PAGOS_OK("invoice_cobros_pagos_ok"),
	INVOICE_COBROS_PAGOS_PARTIAL("invoice_cobros_pagos_p"),
	INVOICE_COBROS_PAGOS_ERROR("invoice_cobros_pagos_ko")
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
