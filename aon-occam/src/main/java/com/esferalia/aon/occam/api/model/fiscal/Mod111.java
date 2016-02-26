package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Mod111 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	public Mod111() {
		super();
		setModel(FiscalModelType.M111);
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
		else if (isAraba()) return getAmount(Mod111Key.AR_C87);
		else if (isAEAT()) return getAmount(Mod111Key.CT_C30);
		else if (isBizkaia()) return  getAmount(Mod111Key.BZ_C39);
		else if (isGipuzkoa()) return getAmount(Mod111Key.GP_C29);
		else if (isNavarra()) return getAmount(Mod111Key.NF_A1);
		return 0;
	}
	
	public Mod111Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAraba()) return Mod111Key.AR_TIP;
		else if (isAEAT()) return Mod111Key.CT_TIP;
		else if (isBizkaia()) return Mod111Key.BZ_TIP;
		else if (isGipuzkoa()) return Mod111Key.GP_TIP;
		else if (isNavarra()) return Mod111Key.NF_TIP;
		return null;
	}
	
}
