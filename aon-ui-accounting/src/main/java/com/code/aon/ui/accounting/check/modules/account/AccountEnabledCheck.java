package com.code.aon.ui.accounting.check.modules.account;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class AccountEnabledCheck implements ICheckModule {

	private String label = "Chequeo de integridad de cuentas contables.";
	private String accountEntryEnabled = "Cuenta de nivel inferior, permite apuntes.";
	private String accountNotEntryEnabled = "Cuenta de nivel superior, no permite apuntes.";
	private String notEntryEnabledFixLabel = "Permitir Apuntes";
	private String entryEnabledFixLabel = "No Permitir Apuntes";
	
	private List<ICheckEntry> list;
	private boolean enabled;
	
	@Override
	public void onExecute(CheckParams params) throws AonCheckException {
		try {
			list = getNotEnabledAccounts();
			list.addAll( getEnabledAccounts() );
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(), e);
		}
	}

	private Collection<? extends ICheckEntry> getEnabledAccounts() throws ManagerBeanException {
		List<ICheckEntry> list = new LinkedList<ICheckEntry>();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
		String select = 
			"SELECT a.id"
			+" FROM account a "
			+" WHERE a.entryEnabled = 1 "
			+" AND a.level < 5 ";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
		List<?> queryList = query
				.addScalar("id", Hibernate.INTEGER)
				.list();
		Iterator<?> iterator = queryList.iterator();
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		while (iterator.hasNext()) {
			Integer id = (Integer) iterator.next();
			Account account  = (Account) bean.get(id);
			AccountEnabledCheckEntry e = new AccountEnabledCheckEntry();
			e.setMessage( accountEntryEnabled );
			e.setTo(account);
			e.setFixActionLabel(entryEnabledFixLabel);
			list.add(e);
		}
		return list;
	}

	private List<ICheckEntry> getNotEnabledAccounts() throws ManagerBeanException {
		List<ICheckEntry> list = new LinkedList<ICheckEntry>();
		String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
		String select = 
			"SELECT a.id"
			+" FROM account a "
			+" WHERE a.entryEnabled = 0 "
			+" AND a.level = 5 ";
		SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
		List<?> queryList = query
				.addScalar("id", Hibernate.INTEGER)
				.list();
		Iterator<?> iterator = queryList.iterator();
		IManagerBean bean = BeanManager.getManagerBean(Account.class);
		while (iterator.hasNext()) {
			Integer id = (Integer) iterator.next();
			Account account  = (Account) bean.get(id);
			AccountEnabledCheckEntry e = new AccountEnabledCheckEntry();
			e.setMessage( accountNotEntryEnabled );
			e.setFixActionLabel(notEntryEnabledFixLabel);
			e.setTo(account);
			list.add(e);
		}
		return list;
	}

	@Override
	public List<ICheckEntry> getCheckList() {
		return list;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public CheckCategory getCategory() {
		return CheckCategory.ACCOUNT;
	}

}