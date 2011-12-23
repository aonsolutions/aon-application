package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.util.CommonUtil;
import com.code.aon.fiscal.enumeration.Period;
import com.code.aon.fiscal.enumeration.RentingStatus;
import com.esferalia.aon.entity.master.RentingDB;

@Entity
@Table(name="fs_renting")
public class Renting extends RentingDB {
	
	private static final long serialVersionUID = 1L;

    @Transient
    public boolean isAnual() {
    	return (getPeriod() == Period.YEAR);
    }
    
	@Transient
	public boolean isExtraDeclaration() {
		return (isComplementary() || isReplacement() );
	}

    @Transient
	public boolean isFinished() {
		return getStatus() == RentingStatus.FINISHED;
	}

	@Transient
	public Renting getRenting() {
		return this;
	}
	
	public void initializeAccumulatedAmounts() {
		setLessorCountAccumulated(0);
		setRentingAmountAccumulated(0);
		setRetentionAccumulated(0);
		setLessorCountInKindAccumulated(0);
		setRemunerationInKindAccumulated(0);
		setAccountDepositAccumulated(0);
	}

	public void calculate() {
		setLessorCountResult( CommonUtil.round(getLessorCountAccumulated() - getLessorCountDeclared()));
		setRentingAmountResult(CommonUtil.round( getRentingAmountAccumulated() - getRentingAmountDeclared()));
		setRetentionResult( CommonUtil.round( getRetentionAccumulated() - getRetentionDeclared()));
		setLessorCountInKindResult( CommonUtil.round( getLessorCountInKindAccumulated() - getLessorCountInKindDeclared()));
		setRemunerationInKindResult( CommonUtil.round( getRemunerationInKindAccumulated() - getRemunerationInKindDeclared()));
		setAccountDepositResult( CommonUtil.round( getAccountDepositAccumulated() - getAccountDepositDeclared()));
		
		setLessorCount( CommonUtil.round(getLessorCountResult() + getLessorCountAdjust()));
		setRentingAmount(CommonUtil.round( getRentingAmountResult() + getRentingAmountAdjust()));
		setRetention( CommonUtil.round( getRetentionResult() + getRetentionAdjust()));
		setLessorCountInKind( CommonUtil.round( getLessorCountInKindResult() + getLessorCountInKindAdjust()));
		setRemunerationInKind( CommonUtil.round( getRemunerationInKindResult() + getRemunerationInKindAdjust()));
		setAccountDeposit( CommonUtil.round( getAccountDepositResult() + getAccountDepositAdjust()));
		
		setTotalTaxDebt( CommonUtil.round( getDeposit() + getExtraCharge() + getDelayInterest()) );
	}

	@Transient
	public double getDeposit() {
		return CommonUtil.round( getRetention() + getAccountDeposit() );
	}
	
}
