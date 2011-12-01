package com.code.aon.ui.accounting.controller.amortization;

import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;

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
				bean.insert(amortizationType);
			} else {
				a.setDescription(amortizationType.getDescription());
				Account fix = getAccount(amortizationType.getFixedAssetAccount());
				a.setFixedAssetAccount(fix);
				Account acc = getAccount(amortizationType.getAccumulatedAccount());
				a.setAccumulatedAccount(acc);
				Account all = getAccount(amortizationType.getAllocationAccount());
				a.setAllocationAccount(all);
				a.setPercentage(amortizationType.getPercentage());
				
				bean.update(a);
			}
		}
	}

	private Account getAccount(Account account) throws ManagerBeanException {
		if (account == null || account.getCode() == null) {
			return null;
		}
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		Criteria c = new Criteria();
		c.addEqualExpression(bean.getFieldName(IAccountAlias.ACCOUNT_CODE), account.getCode());
		List<ITransferObject> list = bean.getList(c);
		if (list == null || list.isEmpty()) {
			return null;
		} 
		Account a = (Account) list.get(0);
		return a;
	}
	
}
