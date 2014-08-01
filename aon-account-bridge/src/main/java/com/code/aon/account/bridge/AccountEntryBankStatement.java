package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.code.aon.AonVersion;
import com.esferalia.aon.entity.master.AccountEntryBankStatementDB;

@Entity
@Table(name="account_entry_bank_statement")
public class AccountEntryBankStatement extends AccountEntryBankStatementDB {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

}