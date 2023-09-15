package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedHashMap;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.IRPFRegime;
import com.esferalia.aon.occam.api.model.type.Mod130Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod130 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private boolean alcatrazBound;
	private LinkedHashMap<String,Mod130> deponents;
	
	public Mod130() {
		super();
		setModel(FiscalModelType.M130);
		setGenerateFromYearStart(true);
	}
	
	public boolean isAlcatrazBound() {
		return alcatrazBound;
	}
	public void setAlcatrazBound(boolean alcatrazBound) {
		this.alcatrazBound = alcatrazBound;
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
	public Mod130 ensureDeponent(Mod130 mod130) {
		String document = mod130.getDocument();
		if (!AonStringUtils.isBlank(document)) {
			if (getDeponents() == null) setDeponents(new LinkedHashMap<>());
			if (!getDeponents().containsKey(document)) {
				getDeponents().put(document, mod130);
			}
		}
		return mod130;
	}

	@Override
	public boolean isToDeduceAvailable() {
		if (getAdministration() == null) return false;
		return isAEAT()
			&& AonMathUtils.isLessThanZero(getDeclarationResult())
			&& (getPeriod() == Period.T1
			 || getPeriod() == Period.T2
			 || getPeriod() == Period.T3)
			;
	}
	
	@Override
	public boolean isNegativeAvailable() {
		if (getAdministration() == null) return false;
		return isAEAT() 
			&& ((AonMathUtils.isZero(getDeclarationResult()))
			 || (AonMathUtils.isLessThanZero(getDeclarationResult()) && getPeriod() == Period.T4));
	}

	@Override
	public boolean isStrictToDeposit() {
		return (isFinished() || isCustomerAccepted() ||isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}
	
	// ************************************************************
	// ************************************************************
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResult())");
	}
	
	@Override
	@Deprecated
	public Mod130Key getDeclarationTypeKey() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResultType())"); 
	}

	@Override
	@Deprecated
	public void setDefaultDeclarationType(){
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
//		if (AonMathUtils.isGreatherThanZero(getResult() )) {
//			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
//		} else {
//			if (getPeriod() == Period.T4) {
//				setDeclarationType(FiscalModelDeclarationType.NEGATIVE);	
//			} else {
//				if (AonMathUtils.isZero(getResult() )) {
//					setDeclarationType(FiscalModelDeclarationType.NEGATIVE);	
//				} else {
//					setDeclarationType(FiscalModelDeclarationType.TO_DEDUCE);
//				}
//			}
//		}
	}

	
//	@Override
//	public boolean isReplacedNumberAvailable() {
//		if (getAdministration() == null) return false;
//		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
//	}
	
}
