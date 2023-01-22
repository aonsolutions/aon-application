package com.esferalia.aon.occam.api.model.fiscal;

import java.io.Serializable;
import java.util.LinkedList;

import com.esferalia.aon.occam.api.model.type.FiscalModelDeclarationType;
import com.esferalia.aon.occam.api.model.type.Mod303Key;
import com.esferalia.aon.occam.api.model.type.VATRegime;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class Mod303 extends FiscalModel implements Serializable {
	
	private static final long serialVersionUID = -6579562925389189514L;
	
	private boolean invoicesBound;
	private boolean diffCalculationMandatory;
	
	private LinkedList<Mod303ActivityFarmer> activityFarmerList;
	private LinkedList<Mod303Activity> activityList;
	
	public Mod303() {
		super();
		setModel(FiscalModelType.M303);
	}
	@Override
	public boolean isReplacedNumberAvailable() {
		return  getAdministration() != null 
				&& (isComplementaryDeclarationAvailable() || isReplacementDeclarationAvailable()) 
				&& (isAEAT() || isAraba() || isNavarra())
				&& (isComplementary() || isReplacement()); 
	}
	
	public boolean isDevReg() {
		return getAmount(Mod303Key.CM_002) == 1;
	}
	
	public boolean isManualDeclaration() {
		return getAmount(Mod303Key.CM_000) == 1;
	}
	public void setManualDeclaration(boolean manual) {
		ensureDetail(Mod303Key.CM_000).setAmount(manual?1:0);
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
	
	public Mod303Key getProrateKey() {
		return Mod303Key.CM_003;
	}
	public Mod303Key getProrateTypeKey() {
		return Mod303Key.CM_006;
	}
	public Mod303Key getPreviousProrateKey() {
		return Mod303Key.CM_007;
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
	
	public boolean hasProrate() {
		return (getProratePercent() != 0 && getProratePercent() != 100)
			|| (isLastPeriod() && hasPreviousProrate())
			|| (!isLastPeriod() && hasPreviousProrate() && isDraft());
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
	
	public double getProratePercent() {
		Mod303Key key = getProrateKey();
		double proratePercent = getAmount(key); 
//		if (AonMathUtils.isZero(proratePercent)) proratePercent = 100.0;  
		return proratePercent;
	}
	public Mod303 setProratePercent(double prorratePercent) {
		ensureDetail(getProrateKey()).setAmount( prorratePercent );
		return this;
	}
	
	public double getPreviousProratePercent() {
		Mod303Key key = getPreviousProrateKey();
		double previousProratePercent = getAmount(key); 
//		if (AonMathUtils.isZero(previousProratePercent)) previousProratePercent = 100.0;  
		return previousProratePercent;
	}
	public Mod303 setPreviousProratePercent(double previousProrratePercent) {
		ensureDetail(getPreviousProrateKey()).setAmount( previousProrratePercent );
		return this;
	}
	public boolean hasPreviousProrate() {
		return getPreviousProratePercent() != 0 && getPreviousProratePercent() != 100;
	}

	@Override
	public boolean isStrictToDeposit() {
		return (canBeSent() || isSent()) 
			&& (getDeclarationResultType() == FiscalModelDeclarationType.DEPOSIT);
	}

	public boolean isDiffCalculationMandatory() {
		return diffCalculationMandatory;
	}
	public Mod303 setDiffCalculationMandatory(boolean diffCalculationMandatory) {
		this.diffCalculationMandatory = diffCalculationMandatory;
		return this;
	}
	
	public boolean isDraft() {
		return AonNumberUtils.equals(getAmount(Mod303Key.CM_073), 1.0);
	}
	public void setDraft(boolean draft) {
		ensureDetail(Mod303Key.CM_073).setAmount(draft?1:0);
	}
	
	@Override
	public boolean isDiffCalculationDisabled() {
		return AonNumberUtils.equals(getAmount(Mod303Key.CM_001), 1.0);
	}
	public boolean isDiffCalculationEnabled() {
		return !isDiffCalculationDisabled();
	}

	@Override
	public void setDiffCalculationDisabled(boolean diffCalculationDisabled) {
		ensureDetail(Mod303Key.CM_001).setAmount(diffCalculationDisabled?1:0);
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
	
	public boolean hasInvoicesBound() {
		return invoicesBound;
	}
	public Mod303 setInvoicesBound(boolean invoicesBound) {
		this.invoicesBound = invoicesBound;
		return this;
	}
}
