package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod131Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod131 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private LinkedList<FiscalModel> deponents;
	private LinkedList<Mod131Activity> activities;
	
	public Mod131() {
		super();
		setModel(FiscalModelType.M131);
	}
	
	public LinkedList<FiscalModel> getDeponents() {
		return deponents;
	}
	public void setDeponents(LinkedList<FiscalModel> deponents) {
		this.deponents = deponents;
	}

	public LinkedList<Mod131Activity> getActivities() {
		return activities;
	}
	public void setActivities(LinkedList<Mod131Activity> activities) {
		this.activities = activities;
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
			&& !AonMathUtils.isGreatherThanZero(getResult())
			&& (getPeriod() == Period.T1
			 || getPeriod() == Period.T2
			 || getPeriod() == Period.T3)
			;
	}
	
	@Override
	public boolean isNegativeAvailable() {
		if (getAdministration() == null) return false;
		return isAEAT() 
			&& !AonMathUtils.isGreatherThanZero(getResult()) 
			&& getPeriod() == Period.T4; 
	}

	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAEAT()) return getAmount(Mod131Key.C15);
		return 0;
	}
	
	@Override
	public Mod131Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAEAT()) return Mod131Key.CT_TIP;
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
				setDeclarationType(FiscalModelDeclarationType.TO_DEDUCE);
			}
		}
	}
}
