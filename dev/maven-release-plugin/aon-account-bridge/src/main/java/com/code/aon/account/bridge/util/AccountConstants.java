package com.code.aon.account.bridge.util;

import java.util.LinkedList;
import java.util.List;

public class AccountConstants {

	public static final String CASH_ACCOUNT_PREFIX = "570";

	public static final String BANK_ACCOUNT_PREFIX = "5720";
	
	public static final String CUSTOMER_ACCOUNT_PREFIX = "4300";

	public static final String SUPPLIER_ACCOUNT_PREFIX = "4000";

	public static final String CREDITOR_ACCOUNT_PREFIX = "4100";

	public static final String FIXED_ASSETS_PREFIX = "21";
	
	public static final String SHORT_TERM_LOAN_ACCOUNT_PREFIX = "5200";

	private static List<String> systemAccounts;
	
	public static List<String> getSystemAccounts(){
		if(systemAccounts == null){
			systemAccounts = new LinkedList<String>();
			systemAccounts.add(CASH_ACCOUNT_PREFIX);
			systemAccounts.add(BANK_ACCOUNT_PREFIX);
			systemAccounts.add(CUSTOMER_ACCOUNT_PREFIX);
			systemAccounts.add(SUPPLIER_ACCOUNT_PREFIX);
			systemAccounts.add(CREDITOR_ACCOUNT_PREFIX);
			systemAccounts.add(FIXED_ASSETS_PREFIX);
			systemAccounts.add(SHORT_TERM_LOAN_ACCOUNT_PREFIX);
		}
		return systemAccounts;
	}
}