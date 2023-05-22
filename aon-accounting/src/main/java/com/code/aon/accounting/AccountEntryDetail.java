package com.code.aon.accounting;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import com.code.aon.account.IAccount;
import com.code.aon.AonVersion;
import com.code.aon.common.audit.IAuditable;
import com.code.aon.common.util.CommonUtil;
import com.esferalia.aon.entity.master.AccountEntryDetailDB;

@Entity
@Table(name="account_entry_detail")
public class AccountEntryDetail extends AccountEntryDetailDB implements IAccount, IAuditable {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private Integer savedAccountId;

	@Transient
	public Integer getSavedAccountId() {
		return savedAccountId;
	}
	public void setSavedAccountId(Integer savedAccountId) {
		this.savedAccountId = savedAccountId;
	}

	public void setDebit(double debit) {
		if (debit != 0) {
			if (debit < 0) {
				super.setCredit(CommonUtil.round(0 - CommonUtil.round(debit)));
				debit = 0;
			} else {
				super.setCredit(0);
			}
		}
		super.setDebit(debit);
	}

	public void setCredit(double credit) {
		if (credit != 0) {
			if (credit < 0) {
				super.setDebit(CommonUtil.round(0 - CommonUtil.round(credit)));
				credit = 0;
			} else {
				super.setDebit(0);
			}
		}
		super.setCredit(credit);
	}

	@Transient
	public double getUnpaidBalance() {
		if (getDebit() > getCredit()) {
			return CommonUtil.round(getDebit() - getCredit());
		}
		return 0;
	}
	
	@Transient
	public double getCreditBalance() {
		if (getCredit() > getDebit()) {
			return CommonUtil.round(getCredit() - getDebit());
		}
		return 0;
	}
}
