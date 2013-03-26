package com.code.aon.ui.accounting.check.modules.account.entry;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.hibernate.Hibernate;
import org.hibernate.SQLQuery;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.ui.accounting.check.AonCheckException;
import com.code.aon.ui.accounting.check.CheckCategory;
import com.code.aon.ui.accounting.check.CheckParams;
import com.code.aon.ui.accounting.check.ICheckEntry;
import com.code.aon.ui.accounting.check.ICheckModule;

public class EmptyAccountEntryCheck implements ICheckModule{

	private String label = "Chequeo de apuntes sin líneas.";
	private String emptyAccountEntry = "Apunte sin líneas.";
	private boolean enabled;
	private List <ICheckEntry> list;
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute(CheckParams params) throws AonCheckException{
		list = new LinkedList<ICheckEntry>();
		try {
			String sessionFactoryName = HibernateUtil.getSessionFactoryName(AccountEntry.class.getName());
			String select = 
				"SELECT ae.id id,count(DISTINCT aed.id) count"
				+" FROM account_entry ae "
				+" LEFT OUTER JOIN account_entry_detail aed ON aed.account_entry = ae.id" 
				+" WHERE account_period = " + params.getPeriod().getId()
				+" AND " + DomainManager.getSQLWhereClause("ae.domain")
				+" GROUP BY ae.id"
				+" HAVING COUNT(DISTINCT aed.id) = 0";
			SQLQuery query = HibernateUtil.getSession(sessionFactoryName).createSQLQuery(select);
			List<?> queryList = query
					.addScalar("id", Hibernate.INTEGER)
					.addScalar("count", Hibernate.INTEGER)
					.list();
			Iterator<?> iterator = queryList.iterator();
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			while (iterator.hasNext()) {
				Object[] obj = (Object[]) iterator.next();
				Integer id = (Integer) obj[0];
				AccountEntry entry = (AccountEntry) entryBean.get(id);
				EmptyAccountEntryCheckEntry e = new EmptyAccountEntryCheckEntry();
				e.setMessage( emptyAccountEntry );
				e.setTo(entry);
				list.add(e);
			}
		} catch (ManagerBeanException e) {
			throw new AonCheckException(e.getMessage(),e);
		}
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
		return CheckCategory.ACCOUNTING;
	}
	
}