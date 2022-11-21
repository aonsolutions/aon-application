package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Mod390Key;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod390HF extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	public Mod390HF() {
		super();
		setModel(FiscalModelType.M390_HF);
	}
	
	public boolean isManualDeclaration() {
		return getAmount(Mod390Key.CM_000) == 1;
	}
	public Mod390HF setManualDeclaration(boolean manual) {
		ensureDetail(Mod390Key.CM_000).setAmount(manual?1:0);
		return this;
	}

	public Mod390Key getProrateKey() {
		return Mod390Key.CM_003;
	}

	public double getProratePercent() {
		return getAmount(getProrateKey());
	}
	public Mod390HF setProratePercent(double prorratePercent) {
		ensureDetail(getProrateKey()).setAmount( prorratePercent );
		return this;
	}
	
	public boolean hasProrate() {
		return AonMathUtils.isNotZero(getProratePercent());
	}
	
	public Mod390Key getProrateTypeKey() {
		return Mod390Key.CM_006;
	}

	public boolean isSpecialProrate() {
		String prorateType = getDescription(getProrateTypeKey());
		return AonStringUtils.isNotBlank(prorateType) && AonStringUtils.equals(prorateType,"E");
	}
	public String getSpecialProrateValue() {
		return isSpecialProrate()?"E":"G";
	}
	public void setSpecialProrateValue(boolean value ) {
		putDescription(getProrateTypeKey(), (value?"E":"G") );
	}
	
	@Override
	public boolean isDiffCalculationDisabled() {
		return true;
	}

	// ******************************************	
	// ******************************************	
	// ******************************************	
	
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
	
	public boolean isToCompensate() {
		return (canBeSent() || isSent()) 
			&& getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE;
	}
	public boolean isToDeposit() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT
			|| getDeclarationResultType() == FiscalModelDeclarationType.BANK
			|| getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT_CCT);
	}
	
	public boolean isToPayback() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK
			|| getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK_CCT);
	}

	
	// ******************************************
	// ******************************************
	// ******************************************
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResult())");
	}
	
	@Override
	@Deprecated
	public Mod303Key getDeclarationTypeKey() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResultType())");
	}
	
	@Override
	@Deprecated
	public void setDefaultDeclarationType(){
		throw new UnsupportedOperationException("Unsupported method! (use setDeclarationResultType())");
	}
	@Override
	@Deprecated
	public FiscalModelDeclarationType getDeclarationType() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResultType())");
	}
	@Override
	@Deprecated
	public void setDeclarationType(FiscalModelDeclarationType type) {
		throw new UnsupportedOperationException("Unsupported method! (use setDeclarationResultType())");
	}
	
/*
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
*/
}
