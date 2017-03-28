package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.occam.api.model.type.CNAE2009;
import com.esferalia.aon.occam.api.model.type.Mod202Key;

public class Mod202 extends FiscalModel implements Serializable {

	private static final long serialVersionUID = 3614782856588153510L;

	private CNAE2009 cnae;
	private Date initialDate;
	
	public Mod202() {
		super();
		setModel(FiscalModelType.M202);
	}

	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return true;
		return false;
	}

	public boolean isReplacementDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return false;
		return false;
	}
	
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAEAT()) {
			double x00 = getAmount(Mod202Key.X00);
			if (x00 == 1 ) {
				return getAmount(Mod202Key.C34);
			} 
			return getAmount(Mod202Key.C03);
		}
		return 0;
	}
	
	public Mod202Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAEAT()) return Mod202Key.P01;
		return null;
	}
	
	
	
	
	/**
	 * El tipo de declaración para la presentación por lotes puede ser: 
	 * 		I (ingreso), 
	 * 		U (domiciliación), 
	 * 		G (Ingreso en C.C.T.) 
	 * 		N (Negativa/Sin actividad/Resultado cero)
	 */
	public String getAeatDeclarationType() {
		return getDescription(Mod202Key.P01);
	}
	
	public CNAE2009 getCnae() {
		return cnae;
	}
	public void setCnae(CNAE2009 cnae) {
		this.cnae = cnae;
	}

	public Date getInitialDate() {
		return initialDate;
	}
	public void setInitialDate(Date initialDate) {
		this.initialDate = initialDate;
	}

	public String getIban() {
		return getDescription(Mod202Key.P00);
	}
	public void setIban(String iban) {
		putDescription(Mod202Key.P00, iban);
	}
	

	@Override
	public void setDeclarationType(String type) {
		// Nothing
	}
	
}
