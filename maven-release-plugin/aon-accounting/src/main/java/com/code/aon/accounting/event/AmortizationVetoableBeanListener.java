package com.code.aon.accounting.event;

import com.code.aon.account.Account;
import com.code.aon.account.util.AccountUtil;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationType;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.ql.util.ExpressionException;

public class AmortizationVetoableBeanListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanInserted(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {

			Amortization to = (Amortization) evt.getTo();

			AmortizationType at = to.getAmortizationType();
			to.setPercentage(at.getPercentage());

			AccountUtil util = new AccountUtil();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);

			if (to.getFixedAssetAccount() == null) {
				Account account = new Account();
				account.setId(util.obtainNextAccountId(at.getFixedAssetAccount().getId()));
				account.setDescription(to.getDescription());
				to.setFixedAssetAccount((Account) accountBean.insert(account));
			}

			if (to.getAccumulatedAccount() == null) {
				Account account = new Account();
				account.setId(util.obtainNextAccountId(at.getAccumulatedAccount().getId()));
				account.setDescription("Amortización Acumulada " + to.getDescription());
				to.setAccumulatedAccount((Account) accountBean.insert(account));
			}

			if (to.getAllocationAccount() == null) {
				Account account = new Account();
				account.setId(util.obtainNextAccountId(at.getAllocationAccount().getId()));
				account.setDescription("Amortización " + to.getDescription());
				to.setAllocationAccount((Account) accountBean.insert(account));
			}

			if (to.getSecurityLevel() == null) {
				to.setSecurityLevel( SecurityLevel.OFFICIAL );
			}
		} catch (ManagerBeanException e) {
			e.printStackTrace();
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		} catch (ExpressionException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		Amortization a = (Amortization) evt.getTo();
		if (a.getSecurityLevel() == null) {
			a.setSecurityLevel( SecurityLevel.OFFICIAL );
		}
	}

}