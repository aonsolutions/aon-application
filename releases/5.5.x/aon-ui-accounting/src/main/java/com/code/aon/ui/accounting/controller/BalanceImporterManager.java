package com.code.aon.ui.accounting.controller;

import com.code.aon.accounting.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;

public class BalanceImporterManager {
	
	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(Balance.class);
		}
		return bean;
	}
	
	public void addBalance(Balance balance) throws ManagerBeanException {
		if (balance != null) {
			balance = (Balance) HibernateUtil.getSession( HibernateUtil.getSessionFactoryName()).merge(balance);  
			getManagerBean().insertOrUpdate(balance);
//			Balance b = (Balance) getManagerBean().get(balance.getId());
//			if (b == null) {
//				bean.insert(balance);
//			} else {
//				b.setName(balance.getName());
//				b.setRemovable(balance.isRemovable());
//				b.setType(balance.getType());
//				b.setLines(balance.getLines());
//				
//				bean.update(b);
//			}
		}
	}
}
