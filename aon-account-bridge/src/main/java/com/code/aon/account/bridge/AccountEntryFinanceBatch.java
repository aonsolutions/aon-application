package com.code.aon.account.bridge;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AccountEntryFinanceBatchDB;

@Entity
@Table(name="account_entry_fbatch")
public class AccountEntryFinanceBatch extends AccountEntryFinanceBatchDB {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}