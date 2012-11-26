package com.code.aon.ui.accounting.controller.amortization;

import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AmortizationTypeImporterManager {
	
	private IManagerBean bean;
	
	private IManagerBean getManagerBean() throws ManagerBeanException {
		if (bean == null) {
			bean = BeanManager.getManagerBean(AmortizationType.class);
		}
		return bean;
	}
	
	public void addAmortizationType(AmortizationType amortizationType) throws ManagerBeanException {
		if (amortizationType != null) {
			AmortizationType a = (AmortizationType) getManagerBean().get(amortizationType.getId());
			if (a == null) {
				a = amortizationType;
				a.setId(null);
			}
			a.setDescription(amortizationType.getDescription());
			a.setPercentage(amortizationType.getPercentage());
			Account fix = getAccount(amortizationType.getFixedAssetAccount());
			a.setFixedAssetAccount(fix);
			Account acc = getAccount(amortizationType.getAccumulatedAccount());
			a.setAccumulatedAccount(acc);
			Account all = getAccount(amortizationType.getAllocationAccount());
			a.setAllocationAccount(all);
			bean.insertOrUpdate(a);
		}
	}

	private Account getAccount(Account account) throws ManagerBeanException {
		if (account == null || account.getCode() == null) {
			return null;
		}
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), account.getCode());
		List<ITransferObject> list = bean.getList(c);
		if (list == null || list.isEmpty()) {
			throw new ManagerBeanException("No existe la cuenta contable " + account.getCode());
		} 
		Account a = (Account) list.get(0);
		return a;
	}
	
}
