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

	private String label = "Chequeo de apuntes sin líneas.";
	private String emptyAccountEntry = "Apunte sin líneas.";
	private boolean enabled;
	private List <ICheckEntry> list;
	
	/**
	 * Comprueba que todos los apuntes tengan lineas
	 */
	@Override
	public void onExecute(AccountingCheckParams params) throws AccountingCheckException{
		list = new LinkedList<ICheckEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			HibernateUtil.setCloseSession(false);
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), params.getPeriod().getId());
			for (ITransferObject to: entryBean.getList(criteria)) {
				AccountEntry entry = (AccountEntry) to;
				if(entry.getDetail() == null || entry.getDetail().size() == 0){
					EmptyAccountCheckEntry e = new EmptyAccountCheckEntry();
					e.setMessage( emptyAccountEntry );
					e.setTo(entry);
					list.add(e);
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(),e);
		} finally {
			HibernateUtil.setCloseSession(prev);
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

}