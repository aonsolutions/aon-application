package com.code.aon.config.event;

import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.config.Bank;
import com.code.aon.config.dao.IConfigAlias;
import com.code.aon.ql.Criteria;

public class BankBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Bank bank = (Bank) evt.getTo();
		check(bank);
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Bank bank = (Bank) evt.getTo();
		check(bank);
	}

	private void check(Bank bank) throws ManagerBeanVetoListenerException {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Bank.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IConfigAlias.BANK_CODE), bank.getCode());
			if (bank.getId() != null) {
				criteria.addNotEqualExpression(bean.getFieldName(IConfigAlias.BANK_ID), bank.getId());	
			}
			List<?> list = bean.getList(criteria);
			if (list != null && list.size() > 0) {
				throw new ManagerBeanVetoListenerException("Ya existe un banco con el código de entidad '"+ bank.getCode() +"'");
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
		}
	}
	
}