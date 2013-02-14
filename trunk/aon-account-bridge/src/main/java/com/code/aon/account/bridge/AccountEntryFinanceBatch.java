package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AccountEntryFinanceBatchDB;

@Entity
@Table(name="account_entry_fbatch")
public class AccountEntryFinanceBatch extends AccountEntryFinanceBatchDB {
	
	private static final long serialVersionUID = 1L;

}