package com.code.aon.ui.accounting.controller;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.ManagerBeanException;

public interface ISpecialAccountEntry {

	public void loadEntry(AccountEntry entry) throws ManagerBeanException;
	public String getNavigationKey();
	
}	
	
