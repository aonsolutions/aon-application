package com.code.aon.fiscal;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.AonVersion;
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
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private String companyDocument;
	private Double participationPercent;
	private boolean readRetentionFromAccount;
	private int receiverCount;
	private int receiverInKindCount;
	
	public FiscalModel() {
		setStatus(FiscalModelStatus.PENDING);
		setReplacement(false);
		setComplementary(false);
		setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
	@Transient
	public String getCompanyDocument() {
		return companyDocument;
	}

	public void setCompanyDocument(String companyDocument) {
		this.companyDocument = companyDocument;
	}

	@Transient
	public Double getParticipationPercent() {
		return participationPercent;
	}

	public void setParticipationPercent(Double participationPercent) {
		this.participationPercent = participationPercent;
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
	public boolean isModel303() {
		return (getModel() == FiscalModelType.M303);
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
	public int getReceiverInKindCount() {
		return receiverInKindCount;
	}

	public void setReceiverInKindCount(int receiverInKindCount) {
		this.receiverInKindCount = receiverInKindCount;
	}

	@Transient
	public boolean isFromCommonTerritory() {
		return (getAdministration() == Administration.COMMON_TERRITORY);
	}
}
