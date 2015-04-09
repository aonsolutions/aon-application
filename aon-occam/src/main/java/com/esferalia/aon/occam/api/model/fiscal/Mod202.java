package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod202 extends FiscalModel implements Serializable {

	private static final long serialVersionUID = 3614782856588153510L;

	
	/**
	 * El tipo de declaración para la presentación por lotes puede ser: 
	 * 		I (ingreso), 
	 * 		U (domiciliación), 
	 * 		G (Ingreso en C.C.T.) 
	 * 		N (Negativa/Sin actividad/Resultado cero)
	 */
	public char getAeatDeclarationType() {
		
		return 'N';
	}
	
}
