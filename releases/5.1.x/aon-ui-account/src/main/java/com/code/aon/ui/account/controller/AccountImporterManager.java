package com.code.aon.ui.account.controller;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;

public class AccountImporterManager {
	
	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(Account.class);
		}
		return bean;
	}
	
	public void addAccount(Account account) throws ManagerBeanException {
		if (account != null) {
			Account a = (Account) getManagerBean().get(account.getId());
			if (a == null) {
				bean.insert(account);
			} else {
				a.setDescription(account.getDescription());
				a.setAlias(account.getAlias());
				bean.update(a);
			}
		}
	}
	
}
