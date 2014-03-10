package com.code.aon.accounting.event;

import java.util.Iterator;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;
import org.hibernate.Session;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.util.AccountHelperManager;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;

public class AccountHelperBeanVetoListener extends ManagerBeanVetoListenerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private AccountHelperManager manager;

	private AccountHelperManager getManager() {
		if (manager == null) {
			manager = new AccountHelperManager();
		}
		return manager;
	}

	@Override
	public void vetoableBeanUpdated(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		try {
			AccountEntryDetail detail = (AccountEntryDetail) evt.getTo();

			String select = "select entryDetail.account, entryDetail.balancing_account "
					+ " from account_entry_detail as entryDetail where entryDetail.id = "
					+ detail.getId();
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			SQLQuery query = session.createSQLQuery(select);
			List<?> list = query.addScalar("account", Hibernate.INTEGER).addScalar("balancing_account",
					Hibernate.INTEGER).list();
			Iterator<?> iterator = list.iterator();
			if (iterator.hasNext()) {
				Object[] obj = (Object[]) iterator.next();
				Integer accountId = (Integer) obj[0];
				Integer balancingAccountId = (Integer) obj[1];
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Account account = accountId==null?null:(Account) accountBean.get(accountId);
				Account balancingAccount = balancingAccountId==null?null:(Account) accountBean.get(balancingAccountId);
				if (account != null && balancingAccount != null) {
					getManager().subtractOccurrence(account, balancingAccount);
				}
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e);
		}
	}

}
