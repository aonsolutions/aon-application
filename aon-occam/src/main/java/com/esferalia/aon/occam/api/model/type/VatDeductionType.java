package com.esferalia.aon.occam.api.model.type;

import java.io.Serializable;

public enum VatDeductionType implements Serializable {

	WITH_RIGHT,		// CON DERECHO A DEDUCCION.
	WITHOUT_RIGHT, 	//  SIN DERECHO A DEDUCCION.
	NON_TAXABLE; 	// NO SUJETO
	
}