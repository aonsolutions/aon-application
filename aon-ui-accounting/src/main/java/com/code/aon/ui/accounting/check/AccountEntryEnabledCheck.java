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

	private List <Account> list;
	
	/**
	 * Comprueba el derecho de apunte de las cuentas segun su nivel
	 */
	@Override
	public void onExecute() throws AccountingCheckException {
		list = new LinkedList<Account>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			List<ITransferObject> toList = accountBean.getList(criteria);
			for (ITransferObject to: toList) {
				Account account = (Account) to;
				if(account.getLevel()<5 && account.isEntryEnabled()){
					list.add(account);
				}else if(account.getLevel()==5 && !account.isEntryEnabled()){
					list.add(account);
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(), e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	public List <Account> getList(){
		return list;
	}

}