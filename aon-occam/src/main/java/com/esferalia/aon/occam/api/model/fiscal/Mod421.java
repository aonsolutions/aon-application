package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod421Key;
import com.esferalia.aon.watson.util.AonNumberUtils;

public class Mod421 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = 7778773305876917802L;
	
	private boolean invoicesBound;
	private boolean diffCalculationMandatory;
	
//	private LinkedList<Mod421ActivityFarmer> activityFarmerList;
	private LinkedList<Mod421Activity> activityList;
	
//	private HashMap<String, Double> tempMap = new HashMap<>();
	
	public Mod421() {
		super();
		setModel(FiscalModelType.M421);
	}
	
	@Override
	public boolean isReplacedNumberAvailable() {
		return  getAdministration() != null 
				&& (isComplementaryDeclarationAvailable() || isReplacementDeclarationAvailable()) 
				&& (isCanarias())
				&& (isComplementary() || isReplacement()); 
	}
	
//	public boolean isDevReg() {
//		return getAmount(Mod421Key.CM_002) == 1;
//	}
	
	public boolean isManualDeclaration() {
		return getAmount(Mod421Key.X00) == 1;
	}
	public void setManualDeclaration(boolean manual) {
		ensureDetail(Mod421Key.X00).setAmount(manual ? 1 : 0);
	}
	
//	public VATRegime getDefaultVATRegime() {
//		return  VATRegime.safeValueOf( (byte) getAmount(Mod421Key.CM_005) );
//	}
//	public void setDefaultVatRegime(VATRegime defaultVatRegime) {
//		ensureDetail(Mod421Key.CM_005).setAmount(defaultVatRegime == null?0:defaultVatRegime.ordinal());
//	}
	
	public LinkedList<Mod421Activity> getActivityList() {
		return activityList;
	}
	public void setActivityList(LinkedList<Mod421Activity> activityList) {
		this.activityList = activityList;
	}
	
//	public LinkedList<Mod421ActivityFarmer> getActivityFarmerList() {
//		return activityFarmerList;
//	}
//	public void setActivityFarmerList(LinkedList<Mod421ActivityFarmer> activityFarmerList) {
//		this.activityFarmerList = activityFarmerList;
//	}
	
//	public boolean isEnrolledInDevolutionRegistry() {
//		return getAmount(Mod421Key.CM_002) == 1;
//	}

//	public Mod421Key getProratePercentKey() {
//		return Mod421Key.CM_003;
//	}
//	public Mod421Key getProrateTypeKey() {
//		return Mod421Key.CM_006;
//	}
//	public Mod421Key getPreviousProratePercentKey() {
//		return Mod421Key.CM_007;
//	}

	public boolean isToCompensate() {
		return (canBeSent() || isSent()) 
			&& getDeclarationResultType() == FiscalModelDeclarationType.COMPENSATE;
	}
	
	public boolean isToDeposit() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT
			|| getDeclarationResultType() == FiscalModelDeclarationType.BANK
			|| getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT_CCT
			|| getDeclarationResultType() == FiscalModelDeclarationType.DEFERRAL);
	}
	
	public boolean isToPayback() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK
			|| getDeclarationResultType() == FiscalModelDeclarationType.PAYBACK_CCT);
	}
	
//	public boolean hasProrate() {
//		// A partir de 2026, se añade un check para indicar expresamente si se debe aplicar prorrata, sea el porcentaje que sea.
//		if (getYear() >= 2026) {
//			return getAmount(Mod421Key.CM_008) == 1; 
//		} else {
//			return (getProratePercent() != 0 && getProratePercent() != 100)
//				|| (isLastPeriod() && hasPreviousProrate())
//				|| (!isLastPeriod() && hasPreviousProrate() && isDraft());
//		}
//	}	
//	public void setProrate(boolean prorate) {
//		ensureDetail(Mod421Key.CM_008).setAmount(prorate ? 1 : 0);
//	}
	
//	public boolean isSpecialProrate() {
//		String prorateType = getDescription(getProrateTypeKey());
//		return AonStringUtils.isNotBlank(prorateType) && AonStringUtils.equals(prorateType,"E");
//	}
//	public String getSpecialProrateValue() {
//		return isSpecialProrate()?"E":"G";
//	}
//	public void setSpecialProrateValue(boolean value ) {
//		putDescription(getProrateTypeKey(), (value?"E":"G") );
//	}
//	
//	public double getProratePercent() {
//		Mod421Key key = getProratePercentKey();
//		double proratePercent = getAmount(key); 
//		return proratePercent;
//	}
//	public Mod421 setProratePercent(double prorratePercent) {
//		ensureDetail(getProratePercentKey()).setAmount( prorratePercent );
//		return this;
//	}
	
//	public double getPreviousProratePercent() {
//		Mod421Key key = getPreviousProratePercentKey();
//		double previousProratePercent = getAmount(key); 
//		return previousProratePercent;
//	}
//	public Mod421 setPreviousProratePercent(double previousProrratePercent) {
//		ensureDetail(getPreviousProratePercentKey()).setAmount( previousProrratePercent );
//		return this;
//	}
// 
//	public boolean hasPreviousProrate() {
//		// Solo se utiliza hasta 2025, a partir de 2026 se añade un check para indicar expresamente si se debe aplicar prorrata, sea el porcentaje que sea.
//		if (getYear() >= 2026) {
//			return false;  
//		} else {
//			return getPreviousProratePercent() != 0 && getPreviousProratePercent() != 100;
//		}
//	}

	@Override
	public boolean isStrictToDeposit() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}

	public boolean isDiffCalculationMandatory() {
		return diffCalculationMandatory;
	}
	public Mod421 setDiffCalculationMandatory(boolean diffCalculationMandatory) {
		this.diffCalculationMandatory = diffCalculationMandatory;
		return this;
	}
	
//	public boolean isDraft() {
//		return AonNumberUtils.equals(getAmount(Mod421Key.CM_073), 1.0);
//	}
//	public void setDraft(boolean draft) {
//		ensureDetail(Mod421Key.CM_073).setAmount(draft?1:0);
//	}
	
	@Override
	public boolean isDiffCalculationDisabled() {
		return AonNumberUtils.equals(getAmount(Mod421Key.X01), 1.0);
	}
	public boolean isDiffCalculationEnabled() {
		return !isDiffCalculationDisabled();
	}

	@Override
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		ensureDetail(Mod421Key.X01).setAmount(diffCalculationDisabled ? 1 : 0);
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
	public Mod421Key getDeclarationTypeKey() {
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
	
	public boolean hasInvoicesBound() {
		return invoicesBound;
	}
	public Mod421 setInvoicesBound(boolean invoicesBound) {
		this.invoicesBound = invoicesBound;
		return this;
	}
	
//	public HashMap<String, Double> getTempMap() {
//		return tempMap;
//	}
	
	@Override
	public String getTownCode() {
    	return ensureDetail(Mod421Key.X02).getDescription();
	}
	
	@Override
	public FiscalModel setTownCode(String townCode) {
		ensureDetail(Mod421Key.X02).setDescription(townCode);	
		return this;
	}
	
}
