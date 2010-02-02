package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ql.Criteria;

public class AccountEntryEnabledCheck implements IAccountCheck {

	private String label = "Chequeo de integridad de cuentas contables.";
	private String accountEntryEnabled = "Cuenta de nivel inferior, permite apuntes.";
	private String accountNotEntryEnabled = "Cuenta de nivel superior, no permite apuntes.";
	
	private List<ICheckEntry> list;
	private boolean enabled;
	
	/**
	 * Comprueba el derecho de apunte de las cuentas segun su nivel
	 */
	@Override
	public void onExecute(AccountingCheckParams params) throws AccountingCheckException {
		list = new LinkedList<ICheckEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			List<ITransferObject> toList = accountBean.getList(criteria);
			for (ITransferObject to: toList) {
				Account account = (Account) to;
				if(account.getLevel()<5 && account.isEntryEnabled()){
					AccountEntryEnabledCheckEntry e = new AccountEntryEnabledCheckEntry();
					e.setMessage( accountEntryEnabled );
					e.setTo(account);
					list.add(e);
				} else if(account.getLevel()==5 && !account.isEntryEnabled()){
					AccountNotEntryEnabledCheckEntry e = new AccountNotEntryEnabledCheckEntry();
					e.setMessage( accountNotEntryEnabled );
					e.setTo(account);
					list.add(e);
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(), e);
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