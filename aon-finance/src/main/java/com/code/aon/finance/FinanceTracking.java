package com.code.aon.finance;


import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.esferalia.aon.entity.master.FinanceTrackingDB;

@Entity
@Table(name="finance_tracking")
public class FinanceTracking extends FinanceTrackingDB implements IConfidentialable{
	
	private static final long serialVersionUID = 1L;

	@Transient
	public boolean isBatched() {
		return (getType() == FinanceTrackingType.BATCHED);
	}

	@Transient
	public boolean isRecordable() {
		return (getType() == FinanceTrackingType.PAID || getType() == FinanceTrackingType.RETURNED);
	}

	@Transient
	public boolean isConfidential() {
		return getFinance().isConfidential();
	}
	@Transient
	public void setConfidential(boolean confidential) {
	}
}