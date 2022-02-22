package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;

import com.esferalia.aon.occam.api.model.type.Mod111Key;

public class Mod111 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;

	public Mod111() {
		super();
		setModel(FiscalModelType.M111);
	}
	
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
	public boolean isReplacedNumberAvailable() {
		if (getAdministration() == null) return false;
		return  (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	@Override
	@Deprecated
	public double getResult() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResult())"); 
	}
	@Override
	@Deprecated
	public Mod111Key getDeclarationTypeKey() {
		throw new UnsupportedOperationException("Unsupported method! (use getDeclarationResultType())"); 
	}
	@Override
	@Deprecated
	public void setDefaultDeclarationType(){
		throw new UnsupportedOperationException("Unsupported method! (use declaration.initializeDeclarationType())");
	}
	
//	@Override
//	public double getResult() {
//		if (getAdministration() == null) return 0;
//		else if (isAraba()) return getAmount(Mod111Key.AR_C87);
//		else if (isAEAT()) return getAmount(Mod111Key.CT_C30);
//		else if (isBizkaia()) return  getAmount(Mod111Key.BZ_C39);
//		else if (isGipuzkoa()) return getAmount(Mod111Key.GP_C29);
//		else if (isNavarra()) return getAmount(Mod111Key.NF_A1);
//		return 0;
//	}
//	
//	@Override
//	public Mod111Key getDeclarationTypeKey() {
//		if (getAdministration() == null) return null;
//		else if (isAraba()) return Mod111Key.AR_TIP;
//		else if (isAEAT()) return Mod111Key.CT_TIP;
//		else if (isBizkaia()) return Mod111Key.BZ_TIP;
//		else if (isGipuzkoa()) return Mod111Key.GP_TIP;
//		else if (isNavarra()) return Mod111Key.NF_TIP;
//		return null;
//	}
	
//	@Override
//	public void setDefaultDeclarationType(){
//		if (AonMathUtils.isGreatherThanZero(getDeclarationResult() )) {
//			setDeclarationResultType(FiscalModelDeclarationType.DEPOSIT);
//		} else {
//			setDeclarationResultType(FiscalModelDeclarationType.NEGATIVE);
//		}
//	}
	
	@Override
	public boolean isDiffCalculationAvailable() {
		// Disponible poder elegir si se cálcula por diferencia
		return true;
	}
	
	@Override
	public boolean isDiffCalculationDisabled() {
		return getAmount(Mod111Key.CM_001) == 1;
	}

	@Override
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		ensureDetail(Mod111Key.CM_001).setAmount(diffCalculationDisabled?1:0);
	}
	
}
