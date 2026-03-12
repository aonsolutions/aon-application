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
	
	private LinkedList<Mod421Activity> activityList;
	
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
	
	public boolean isManualDeclaration() {
		return getAmount(Mod421Key.X00) == 1;
	}
	public void setManualDeclaration(boolean manual) {
		ensureDetail(Mod421Key.X00).setAmount(manual ? 1 : 0);
	}
	
	public LinkedList<Mod421Activity> getActivityList() {
		return activityList;
	}
	public void setActivityList(LinkedList<Mod421Activity> activityList) {
		this.activityList = activityList;
	}
	
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
	// ******************************************
	// ******************************************
	// ******************************************
	
	public boolean hasInvoicesBound() {
		return invoicesBound;
	}
	public Mod421 setInvoicesBound(boolean invoicesBound) {
		this.invoicesBound = invoicesBound;
		return this;
	}
	
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
