package com.code.aon.account.bridge;

import javax.persistence.Entity;
import javax.persistence.Table;

import com.esferalia.aon.entity.master.AccountEntryFinanceTrackingDB;

@Entity
@Table(name="account_entry_finance_tracking")
public class AccountEntryFinanceTracking extends AccountEntryFinanceTrackingDB {

	private static final long serialVersionUID = 1L;

}