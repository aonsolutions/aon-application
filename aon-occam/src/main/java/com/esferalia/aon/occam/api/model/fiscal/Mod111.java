package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

public class Mod111 extends FiscalModel implements Serializable {
	
	public Mod111() {
		super();
		setModel(FiscalModelType.M111);
	}
	
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		if (isAEAT()) return true;
		if (isAraba()) return true;
		if (isBizkaia()) return false;
		if (isGipuzkoa()) return false;
		if (isNavarra()) return false;
		return false;
	}

	public boolean isReplacementDeclarationAvailable() {
		if (getAdministration() == null) return false;
		if (isAraba()) return true;
		if (isAEAT()) return false;
		if (isBizkaia()) return false;
		if (isGipuzkoa()) return false;
		if (isNavarra()) return false;
		return false;
	}
	
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary()); 
	}

	private static final long serialVersionUID = 3614782856588153510L;
}
