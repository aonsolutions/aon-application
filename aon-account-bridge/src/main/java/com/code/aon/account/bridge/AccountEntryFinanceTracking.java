package com.code.aon.account.bridge;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AccountEntryFinanceTrackingDB;

@Entity
@Table(name="account_entry_finance_tracking")
public class AccountEntryFinanceTracking extends AccountEntryFinanceTrackingDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}