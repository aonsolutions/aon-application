package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;

public class EmptyAccountEntryCheck implements IAccountCheck{

	private String period;
	private List <AccountEntry> list;
	
	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute() throws AccountingCheckException{
		list = new LinkedList<AccountEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			HibernateUtil.setCloseSession(false);
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period);
			List<ITransferObject> toList = entryBean.getList(criteria);
			for (ITransferObject to: toList) {
				AccountEntry entry = (AccountEntry) to;
				if(entry.getDetail() == null || entry.getDetail().size() == 0){
					list.add(entry);
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(),e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	public List <AccountEntry> getList(){
		return list;
	}

}