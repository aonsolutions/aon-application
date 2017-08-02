package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	public Mod303() {
		super();
		setModel(FiscalModelType.M303);
	}
	
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return true;
		else if (isAraba()) return true;
		else if (isBizkaia()) return true;
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
		return (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAraba()) return getAmount(Mod303Key.AR_C060);
		else if (isAEAT()) return  getAmount(Mod303Key.CT_C71);
		else if (isBizkaia()) return  getAmount(Mod303Key.BZ_C036);
		else if (isGipuzkoa()) return 0;
		else if (isNavarra()) return 0;
		return 0;
	}
	
	public Mod303Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAraba()) return Mod303Key.AR_X002;
		else if (isAEAT()) return Mod303Key.CT_X02;
		else if (isBizkaia()) return Mod303Key.BZ_X001;
		else if (isGipuzkoa()) return null;
		else if (isNavarra()) return null;
		return null;
	}
	
	public double getProratePercent() {
		double proratePercent = 100.0;
		if (isAEAT()) proratePercent = getAmount(Mod303Key.CT_X01);
		else if (isBizkaia()) proratePercent = getAmount(Mod303Key.BZ_C101);
		else if (isAraba()) proratePercent = getAmount(Mod303Key.AR_X001);
		if (AonMathUtils.isZero(proratePercent)) proratePercent = 100.0;  
		return proratePercent;
	}
	
}
