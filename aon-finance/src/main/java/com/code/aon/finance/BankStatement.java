package com.code.aon.finance;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.code.aon.common.AonVersion;
import com.code.aon.finance.enumeration.StatementConcept;
import com.code.aon.finance.enumeration.StatementReliability;
import com.code.aon.finance.enumeration.StatementStatus;
import com.esferalia.aon.entity.master.BankStatementDB;

@Entity
@Table(name="bank_statement")
public class BankStatement extends BankStatementDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showBankStatementLink;
	private boolean showAccountEntry;

	@Transient
	public boolean isReturned() {
		return getCommonConcept() == StatementConcept.RETURNED;
	}
	@Transient
	public boolean isCollectionBatch() {
		return getCommonConcept() == StatementConcept.COLLECTION_BATCH;
	}

	@Transient
	public boolean isExact() {
		return getReliability() == StatementReliability.VERY_HIGH;
	}
	@Transient
	public boolean isApproximate() {
		return getReliability() == StatementReliability.HIGH;
	}
	@Transient
	public boolean isAmbiguous() {
		return getReliability() == StatementReliability.MEDIUM;
	}
	@Transient
	public boolean isInexact() {
		return getReliability() == StatementReliability.LOW;
	}

	@Transient
	public boolean isPending() {
		return getStatus() == StatementStatus.PENDING;
	}
	@Transient
	public boolean isChecked() {
		return getStatus() == StatementStatus.CHECKED;
	}
	@Transient
	public boolean isRecorded() {
		return getStatus() == StatementStatus.RECORDED;
	}

	@Transient
	public boolean isShowBankStatementLink() {
		return showBankStatementLink;
	}
	public void setShowBankStatementLink(boolean value) {
		this.showBankStatementLink = value;
	}

	@Transient
	public boolean isShowAccountEntry() {
		return showAccountEntry;
	}
	public void setShowAccountEntry(boolean value) {
		this.showAccountEntry = value;
	}

}