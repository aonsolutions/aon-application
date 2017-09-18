package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonMathUtils;

public class Mod303 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	private LinkedList<Mod303ActivityFarmer> activityFarmerList;
	private LinkedList<Mod303Activity> activityList;
	
	public Mod303() {
		super();
		setModel(FiscalModelType.M303);
	}
	public VATRegime getDefaultVATRegime() {
		return  VATRegime.safeValueOf( (byte) getAmount(Mod303Key.CM_005) );
	}
	public void setDefaultVatRegime(VATRegime defaultVatRegime) {
		ensureDetail(Mod303Key.CM_005).setAmount(defaultVatRegime == null?0:defaultVatRegime.ordinal());
	}
	public LinkedList<Mod303Activity> getActivityList() {
		return activityList;
	}
	public void setActivityList(LinkedList<Mod303Activity> activityList) {
		this.activityList = activityList;
	}
	
	public LinkedList<Mod303ActivityFarmer> getActivityFarmerList() {
		return activityFarmerList;
	}
	public void setActivityFarmerList(LinkedList<Mod303ActivityFarmer> activityFarmerList) {
		this.activityFarmerList = activityFarmerList;
	}
	
	public boolean isEnrolledInDevolutionRegistry() {
		return getAmount(Mod303Key.CM_002) == 1;
	}
	
	public boolean isDiffCalculationDisabled() {
		return getAmount(Mod303Key.CM_001) == 1;
	}

	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		ensureDetail(Mod303Key.CM_001).setAmount(diffCalculationDisabled?1:0);
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
		return (isComplementaryDeclarationAvailable() && isAEAT() && isComplementary() ); 
	}
	
	@Override
	public double getResult() {
		if (getAdministration() == null) return 0;
		else if (isAraba()) return getAmount(Mod303Key.AR_C080);
		else if (isAEAT()) return  getAmount(Mod303Key.CT_C71);
		else if (isBizkaia()) return  getAmount(Mod303Key.BZ_C036);
		else if (isGipuzkoa()) return  getAmount(Mod303Key.GP_C035);
		else if (isNavarra()) return 0;
		return 0;
	}
	
	@Override
	public Mod303Key getDeclarationTypeKey() {
		if (getAdministration() == null) return null;
		return Mod303Key.CM_004;
	}
	
	public Mod303Key getProrateKey() {
		return Mod303Key.CM_003;
	}
	
	public double getProratePercent() {
		double proratePercent = 100.0;
		Mod303Key key = getProrateKey();
		proratePercent = getAmount(key); 
		if (AonMathUtils.isZero(proratePercent)) proratePercent = 100.0;  
		return proratePercent;
	}
	
	@Override
	public void setDefaultDeclarationType(){
		if (AonMathUtils.isGreatherThanZero(getResult() )) {
			setDeclarationType(FiscalModelDeclarationType.DEPOSIT);
		} else {
			setDeclarationType(
				(isEnrolledInDevolutionRegistry() || getPeriod() == Period.T4 || getPeriod() == Period.M12) 
					?FiscalModelDeclarationType.PAYBACK
					:FiscalModelDeclarationType.COMPENSATE
							);
		}
	}
	
	public boolean isOldMod303() {
		return (getMap() != null && (getMap().containsKey("303-AG1") || getMap().containsKey("303-AC1")));
	}
}
