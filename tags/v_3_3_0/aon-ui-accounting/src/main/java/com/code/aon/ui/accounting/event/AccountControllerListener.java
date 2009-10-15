package com.code.aon.ui.accounting.event;

import java.util.Iterator;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerListener extends ControllerAdapter {

	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		Account to = (Account)event.getController().getTo();
		int parentLevel = 0;
		switch (to.getId().length()) {
		case 12: parentLevel = 5;
			break;
		case 5: parentLevel = 3;
			break;
		case 3: parentLevel = 2;
			break;
		case 2: parentLevel = 1;
			break;
		}

		if (parentLevel > 0) {
			try {
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), to.getId().substring(0, parentLevel));

				Iterator iterator = accountBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					Account account = (Account)iterator.next();
					if (account.isEntryEnabled() && hasAccountEntryDetails(account)) {
						throw new ControllerListenerException("Imposible crear cuenta. La cuenta correspondiente de nivel inferior tiene apuntes contables.");
					}
				} else {
					throw new ControllerListenerException("Imposible crear cuenta. No existe cuenta correspondiente de nivel inferior.");
				}
			} catch (ManagerBeanException e) {
				throw new ControllerListenerException(e);
			}
		}
	}

	private boolean hasAccountEntryDetails(Account account) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), account.getId());

		return (accountEntryDetailBean.getList(criteria).size() > 0);
	}

}