package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public class Mod115 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	private boolean alcatrazBound;

	public Mod115() {
		super();
		setModel(FiscalModelType.M115);
	}
	
	@Override
	public boolean isReplacedNumberAvailable() {
		return  getAdministration() != null 
				&& (isComplementaryDeclarationAvailable() || isReplacementDeclarationAvailable()) 
				&& (isAEAT() || isAraba() || isNavarra())
				&& (isComplementary() || isReplacement()); 
	}

	@Override
	public boolean isStrictToDeposit() {
		return (isFinished() || isCustomerAccepted() ||isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}

	public boolean isAlcatrazBound() {
		return alcatrazBound;
	}
	public Mod115 setAlcatrazBound(boolean alcatrazBound) {
		this.alcatrazBound = alcatrazBound;
		return this;
	}
	
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResult())");
	}
	
	@Override
	@Deprecated
	public Mod115Key getDeclarationTypeKey() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResultType())");
	}
	
	@Override
	@Deprecated
	public void setDefaultDeclarationType(){
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}

	@Override
	@Deprecated
	public boolean isDiffCalculationAvailable() {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}
	
	@Override
	@Deprecated
	public boolean isDiffCalculationDisabled() {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}

	@Override
	@Deprecated
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		throw new UnsupportedOperationException("Unsupported method! (Now diff is implicit)");
	}
	
}
