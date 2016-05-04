package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod130 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private LinkedHashMap<String,Mod130> deponents;
	
	public Mod130() {
		super();
		setModel(FiscalModelType.M130);
	}
	
	public IRPFRegime getRegime() {
		return getAmount(Mod130Key.P0)==1?IRPFRegime.SIMPLIFIED:IRPFRegime.NORMAL;
	}
	public void setRegime(IRPFRegime regime) {
		putAmount(Mod130Key.P0,regime==IRPFRegime.SIMPLIFIED?1:0);
	}

	public LinkedHashMap<String,Mod130> getDeponents() {
		return deponents;
	}
	public void setDeponents(LinkedHashMap<String,Mod130> deponents) {
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
	public boolean isToDeduceAvailable() {
		if (getAdministration() == null) return false;
		return isAEAT()
			&& AonMathUtils.isLessThanZero(getResult())
			&& (getPeriod() == Period.T1
			 || getPeriod() == Period.T2
			 || getPeriod() == Period.T3)
			;
	}
	
	@Override
	public boolean isNegativeAvailable() {
		if (getAdministration() == null) return false;
		return isAEAT() 
			&& ((AonMathUtils.isZero(getResult()))
			 || (AonMathUtils.isLessThanZero(getResult()) && getPeriod() == Period.T4));
	}

	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAEAT()) return getAmount(Mod130Key.C19);
		else if (isBizkaia()) return getAmount(Mod130Key.C28);
		return 0;
	}
	
	@Override
	public Mod130Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAEAT()) return Mod130Key.CT_TIP;
		else if (isBizkaia()) return Mod130Key.CT_TIP;
		return null;
	}

	@Override
	public void setDefaultDeclarationType(){
		if (AonMathUtils.isGreatherThanZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			if (getPeriod() == Period.T4) {
				setDeclarationType(FiscalModelDeclarationType.NEGATIVE);	
			} else {
				if (AonMathUtils.isZero(getResult() )) {
					setDeclarationType(FiscalModelDeclarationType.NEGATIVE);	
				} else {
					setDeclarationType(FiscalModelDeclarationType.TO_DEDUCE);
				}
			}
		}
	}
}
