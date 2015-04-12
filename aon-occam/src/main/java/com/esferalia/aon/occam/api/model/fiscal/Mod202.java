package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

public class Mod202 extends FiscalModel implements Serializable {

	private static final long serialVersionUID = 3614782856588153510L;

	private String cnae;
	private String cnaeDescription;
	private Date initialDate;
	
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
	
	public String getCnae() {
		return cnae;
	}
	public void setCnae(String cnae) {
		this.cnae = cnae;
	}

	public String getCnaeDescription() {
		return cnaeDescription;
	}
	public void setCnaeDescription(String cnaeDescription) {
		this.cnaeDescription = cnaeDescription;
	}

	public Date getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}
	
}
