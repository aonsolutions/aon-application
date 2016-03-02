package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;

public class Mod130 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private IRPFRegime regime;
	private LinkedList<FiscalModel> deponents;
	
	public Mod130() {
		super();
		setModel(FiscalModelType.M130);
	}
	
	public IRPFRegime getRegime() {
		return regime;
	}
	public void setRegime(IRPFRegime regime) {
		this.regime = regime;
	}

	public LinkedList<FiscalModel> getDeponents() {
		return deponents;
	}
	public void setDeponents(LinkedList<FiscalModel> deponents) {
		this.deponents = deponents;
	}

	@Override
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return true;
		return false;
	}

	@Override
	public boolean isReplacementDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return false;
		return false;
	}
	
	@Override
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAEAT()) return getAmount(Mod130Key.C19);
		return 0;
	}
	
	@Override
	public Mod130Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAEAT()) return Mod130Key.CT_TIP;
		return null;
	}

}
