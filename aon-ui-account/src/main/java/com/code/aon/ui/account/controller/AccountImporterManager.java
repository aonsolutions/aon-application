package com.code.aon.ui.account.controller;

import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

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
			Criteria c = new Criteria();
			c.addEqualExpression(getManagerBean().getFieldName(IAccountAlias.ACCOUNT_CODE), account.getCode());
			List<ITransferObject> list = getManagerBean().getList(c);
			if (list == null || list.isEmpty()) {
				bean.insert(account);
			} else {
				Account a = (Account) list.get(0);	
				a.setCode(account.getCode());
				a.setDescription(account.getDescription());
				a.setAlias(account.getAlias());
				bean.update(a);
			}
		}
	}
	
}
