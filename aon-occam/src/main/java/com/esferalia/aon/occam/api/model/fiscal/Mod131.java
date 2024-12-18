package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.stream.Collectors;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonMathUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod131 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private boolean alcatrazBound;
	private LinkedHashMap<String,Mod131> deponents;
	private LinkedList<Mod131Activity> activities;
	
	public Mod131() {
		super();
		setModel(FiscalModelType.M131);
	}
	
	public boolean isAlcatrazBound() {
		return alcatrazBound;
	}
	public void setAlcatrazBound(boolean alcatrazBound) {
		this.alcatrazBound = alcatrazBound;
	}

	public LinkedHashMap<String,Mod131> getDeponents() {
		return deponents;
	}
	public void setDeponents(LinkedHashMap<String,Mod131> deponents) {
		this.deponents = deponents;
	}
	public Mod131 ensureDeponent(Mod131 mod131) {
		String document = mod131.getDocument();
		if (!AonStringUtils.isBlank(document)) {
			if (getDeponents() == null) setDeponents(new LinkedHashMap<>());
			if (!getDeponents().containsKey(document)) {
				getDeponents().put(document, mod131);
			}
		}
		return mod131;
	}

	public LinkedList<Mod131Activity> getActivities() {
		return activities;
	}
	public void setActivities(LinkedList<Mod131Activity> activities) {
		this.activities = activities;
	}
	public LinkedList<Mod131Activity> getEffectiveActivities() {
		return AonCollectionUtils.stream(getActivities())
			.filter( act -> act.getEpigraph() != null)
			.collect(Collectors.toCollection(LinkedList::new));
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
	
	
	// ******************************************************
	// ******************************************************
	// ******************************************************
	
//	@Override
//	public boolean isComplementaryDeclarationAvailable() {
//		if (getAdministration() == null) return false;
//		else if (isAEAT()) return true;
//		return false;
//		
//	}
//	
//	@Override
//	public boolean isReplacementDeclarationAvailable() {
//		if (getAdministration() == null) return false;
//		else if (isAEAT()) return false;
//		return false;
//	}
//	
//	@Override
//	public boolean isReplacedNumberAvailable() {
//		if (getAdministration() == null) return false;
//		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
//	}
	
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResult())");
	}
	
	@Override
	@Deprecated
	public Mod131Key getDeclarationTypeKey() {
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

}
