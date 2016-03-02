package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum FiscalModelKeyInfo implements Serializable {
	  NONE			(false, "Sin informaci\u00F3n")
	, INVOICE		(true,  "Ver desglose de retenciones en facturas")
	, DIFF_INVOICE	(true,  "Detalle del c\u00E1lculo por diferencia. Facturas - declarado")
	, SALARY		(true,  "Ver desglose de retenciones monetarias en n\u00F3minas")
	, SALARY_IN_KIND(true,  "Ver desglose de retenciones en especie en n\u00F3minas")
	, DIFF_SALARY	(true,  "Detalle del c\u00E1lculo por diferencia. N\u00F3minas - declarado")
	, COMPUTE		(false, "Ver desglose de c\u00E1lculos")
	, COMPUTE_KEY	(false, "Ver desglose de c\u00E1lculos")
	, TITLE 		(false, "Título")
	;
	
	private String label;

	private FiscalModelKeyInfo(boolean diffEnabled, String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}
