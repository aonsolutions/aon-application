package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod390HF extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	public Mod390HF() {
		super();
		setModel(FiscalModelType.M390_HF);
	}
	public VATRegime getDefaultVATRegime() {
		return  VATRegime.safeValueOf( (byte) getAmount(Mod303Key.CM_005) );
	}
	public void setDefaultVatRegime(VATRegime defaultVatRegime) {
		ensureDetail(Mod303Key.CM_005).setAmount(defaultVatRegime == null?0:defaultVatRegime.ordinal());
	}
	
	@Override
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAraba()) return true;
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
//		else if (isAraba()) return getAmount(Mod303Key.AR_C080);
		else if (isBizkaia()) return  getAmount(Mod390Key.BZ_C110);
//		else if (isGipuzkoa()) return  getAmount(Mod303Key.GP_C035);
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
