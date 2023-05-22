package com.code.aon.finance;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.IConfidentialable;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.esferalia.aon.entity.master.FinanceTrackingDB;

@Entity
@Table(name="finance_tracking")
public class FinanceTracking extends FinanceTrackingDB implements IConfidentialable, IAuditable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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