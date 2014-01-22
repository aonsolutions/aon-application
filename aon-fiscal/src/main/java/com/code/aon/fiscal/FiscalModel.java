package com.code.aon.fiscal;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.config.enumeration.Administration;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.fiscal.enumeration.FiscalModelStatus;
import com.code.aon.fiscal.enumeration.FiscalModelType;
import com.esferalia.aon.entity.master.FiscalModelDB;

@Entity
@Table(name="fs_model")
public class FiscalModel extends FiscalModelDB implements IAuditable {
	
	private static final long serialVersionUID = 1L;
	
	private boolean readRetentionFromAccount;
	private int receiverCount;
	
	public FiscalModel() {
		setStatus(FiscalModelStatus.PENDING);
		setReplacement(false);
		setComplementary(false);
		setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	@Transient
	public boolean isReadOnly() {
		return (getStatus() != FiscalModelStatus.PENDING);
	}

	@Transient
	public boolean isPending() {
		return (getStatus() == FiscalModelStatus.PENDING);
	}

	@Transient
	public boolean isFinished() {
		return (getStatus() == FiscalModelStatus.FINISHED);
	}

	@Transient
	public boolean isExtraDeclaration() {
		return (isReplacement() || isComplementary());
	}

	@Transient
	public boolean isCashBasis() {
		return (getFinance() != null && getFinance().getPayMethod() != null && getFinance().getPayMethod().getType() == PayMethodType.CASH_BASIS);
	}

	@Transient
	public boolean isModel111() {
		return (getModel() == FiscalModelType.M111);
	}

	@Transient
	public boolean isModel311() {
		return (getModel() == FiscalModelType.M311);
	}

	@Transient
	public boolean isReadRetentionFromAccount() {
		return readRetentionFromAccount;
	}

	public void setReadRetentionFromAccount(boolean readRetentionFromAccount) {
		this.readRetentionFromAccount = readRetentionFromAccount;
	}

	@Transient
	public int getReceiverCount() {
		return receiverCount;
	}

	public void setReceiverCount(int receiverCount) {
		this.receiverCount = receiverCount;
	}
	
	@Transient
	public boolean isFromCommonTerritory() {
		return (getAdministration() == Administration.COMMON_TERRITORY);
	}
}
