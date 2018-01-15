package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod390HF extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	public Mod390HF() {
		super();
		setModel(FiscalModelType.M390_HF);
	}
	
	public boolean isEnrolledInDevolutionRegistry() {
		if (getAdministration() == null) return false;
		else if (isAraba()) return getAmount(Mod390Key.AR_C918) == 1;
		else if (isBizkaia()) return getAmount(Mod390Key.BZ_C080) == 1;
		else if (isGipuzkoa()) return getAmount(Mod390Key.GP_A003) == 1;
		return false;
	}

	@Override
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAraba()) return false;
		else if (isBizkaia()) return true;
		else if (isGipuzkoa()) return false;
		else if (isNavarra()) return false;
		return false;
	}

	@Override
	public boolean isReplacementDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAraba()) return true;
		else if (isBizkaia()) return false;
		else if (isGipuzkoa()) return false;
		else if (isNavarra()) return false;
		return false;
	}
	
	@Override
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAraba()) return getAmount(Mod390Key.AR_C13X);
		else if (isBizkaia()) return  getAmount(Mod390Key.BZ_C110);
		else if (isGipuzkoa()) return  getAmount(Mod390Key.GP_C040);
		else if (isNavarra()) return 0;
		return 0;
	}
	
	@Override
	public Mod390Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		return Mod390Key.CM_004;
	}
	
	public Mod390Key getProrateKey() {
		return Mod390Key.CM_003;
	}

	public boolean isToCompensate() {
		return isFinished() && getDeclarationType() == FiscalModelDeclarationType.COMPENSATE;
	}
	public boolean isToDeposit() {
		return isFinished() && (getDeclarationType() == FiscalModelDeclarationType.DEPOSIT
				|| getDeclarationType() == FiscalModelDeclarationType.BANK
				|| getDeclarationType() == FiscalModelDeclarationType.DEPOSIT_CCT);
	}
	public boolean isToPayback() {
		return isFinished() && (getDeclarationType() == FiscalModelDeclarationType.PAYBACK
				|| getDeclarationType() == FiscalModelDeclarationType.PAYBACK_CCT);
	}
	
	public double getProratePercent() {
		double proratePercent = 100.0;
		Mod390Key key = getProrateKey();
		proratePercent = getAmount(key); 
		if (AonMathUtils.isZero(proratePercent)) proratePercent = 100.0;  
		return proratePercent;
	}
	
	@Override
	public void setDefaultDeclarationType(){
		if (AonMathUtils.isZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
		} else if (AonMathUtils.isGreatherThanZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			setDeclarationType(FiscalModelDeclarationType.PAYBACK);
		}
	}
}
