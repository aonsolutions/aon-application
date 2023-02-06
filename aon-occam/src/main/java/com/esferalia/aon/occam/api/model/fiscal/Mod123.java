package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod115Key;

public class Mod123 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	private boolean alcatrazBound;

	public Mod123() {
		super();
		setModel(FiscalModelType.M123);
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
	public Mod123 setAlcatrazBound(boolean alcatrazBound) {
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
	
	/*
	@Override
	public boolean isComplementaryDeclarationAvailable() {
		if (getAdministration() == null) return false;
		else if (isAEAT()) return true;
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
		else if (isAEAT()) return false;
		else if (isBizkaia()) return false;
		else if (isGipuzkoa()) return false;
		else if (isNavarra()) return false;
		return false;
	}
	
	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAraba()) return getAmount(Mod123Key.AR_C10);
		else if (isAEAT()) return getAmount(Mod123Key.CT_C08);
		else if (isBizkaia()) return  getAmount(Mod123Key.BZ_C06);
		else if (isGipuzkoa()) return getAmount(Mod123Key.GP_C09);
		else if (isNavarra()) return getAmount(Mod123Key.NF_C01);
		return 0;
	}
	
	@Override
	public Mod123Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		else if (isAraba()) return Mod123Key.AR_TIP;
		else if (isAEAT()) return Mod123Key.CT_TIP;
		else if (isBizkaia()) return Mod123Key.BZ_TIP;
		else if (isGipuzkoa()) return Mod123Key.GP_TIP;
		else if (isNavarra()) return Mod123Key.NF_TIP;
		return null;
	}
	
	@Override
	public void setDefaultDeclarationType(){
		if (AonMathUtils.isGreatherThanZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			setDeclarationType(FiscalModelDeclarationType.NEGATIVE);
		}
	}
	
	@Override
	public boolean isDiffCalculationAvailable() {
		// Disponible poder elegir si se cálcula por diferencia
		return true;
	}
	
	// Hasta ahora se estaban calculando todos los 123 sin diferencias, 
	// por eso se graba ahora 1 si esta habilitado y 0 si no lo está
	// al contrario de como se hace en otros modelos
	
	@Override
	public boolean isDiffCalculationDisabled() {
		return getAmount(Mod123Key.CM_001) == 0;
	}

	@Override
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		ensureDetail(Mod123Key.CM_001).setAmount(diffCalculationDisabled?0:1);
	}

*/
}
