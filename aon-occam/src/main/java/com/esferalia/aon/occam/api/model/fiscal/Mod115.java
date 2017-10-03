package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod115 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	public Mod115() {
		super();
		setModel(FiscalModelType.M115);
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
		else if (isAraba()) return getAmount(Mod115Key.AR_C11);
		else if (isAEAT()) return getAmount(Mod115Key.CT_C05);
		else if (isBizkaia()) return  getAmount(Mod115Key.BZ_C07);
		else if (isGipuzkoa()) return getAmount(Mod115Key.GP_C07);
		else if (isNavarra()) return getAmount(Mod115Key.NF_C01);
		return 0;
	}
	
	public Mod115Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAraba()) return Mod115Key.AR_TIP;
		else if (isAEAT()) return Mod115Key.CT_TIP;
		else if (isBizkaia()) return Mod115Key.BZ_TIP;
		else if (isGipuzkoa()) return Mod115Key.GP_TIP;
		else if (isNavarra()) return Mod115Key.NF_TIP;
		return null;
	}
	
	public void setDefaultDeclarationType(){
		if (AonMathUtils.isGreatherThanZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
		}
	}

}
