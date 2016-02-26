package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod123Key;

public class Mod123 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	public Mod123() {
		super();
		setModel(FiscalModelType.M123);
	}
	
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return true;
		else if (isAraba()) return true;
		else if (isBizkaia()) return false;
		else if (isGipuzkoa()) return false;
		else if (isNavarra()) return false;
		return false;
	}

	public boolean isReplacementDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAraba()) return true;
		else if (isAEAT()) return false;
		else if (isBizkaia()) return false;
		else if (isGipuzkoa()) return false;
		else if (isNavarra()) return false;
		return false;
	}
	
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAraba()) return getAmount(Mod123Key.AR_C10);
		else if (isAEAT()) return getAmount(Mod123Key.CT_C06);
		else if (isBizkaia()) return  getAmount(Mod123Key.BZ_C06);
		else if (isGipuzkoa()) return getAmount(Mod123Key.GP_C09);
		else if (isNavarra()) return getAmount(Mod123Key.NF_C01);
		return 0;
	}
	
	public Mod123Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAraba()) return Mod123Key.AR_TIP;
		else if (isAEAT()) return Mod123Key.CT_TIP;
		else if (isBizkaia()) return Mod123Key.BZ_TIP;
		else if (isGipuzkoa()) return Mod123Key.GP_TIP;
		else if (isNavarra()) return Mod123Key.NF_TIP;
		return null;
	}
	

}
