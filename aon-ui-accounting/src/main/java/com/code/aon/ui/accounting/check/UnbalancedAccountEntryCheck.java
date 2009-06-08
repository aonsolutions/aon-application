package com.code.aon.ui.accounting.check;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.controller.AccountEntryController;
import com.code.aon.ui.form.FormUtil;

public class UnbalancedAccountEntryCheck implements IAccountCheck {

	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";
	private String period;
	private List <AccountEntry> list;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	/**
	 * Comprueba que los apuntes no esten descuadrados
	 */
	@Override
	public void onExecute() throws AccountingCheckException {
		list = new LinkedList<AccountEntry>();
		boolean prev = HibernateUtil.mustCloseSession();
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			IManagerBean entryBean = BeanManager.getManagerBean(AccountEntry.class);
			entryController.getCriteria().addEqualExpression(entryBean
					.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_ACCOUNT_PERIOD), period);
			entryController.onSelectFirst(null);
			entryController.onSelectNext(null);
			if(CommonUtil.round(entryController.getTotalDebit()) != CommonUtil.round(entryController.getTotalCredit())){
				list.add((AccountEntry)entryController.getTo());
			}
			while(!entryController.isInLast()){
				entryController.onSelectNext(null);
				if(CommonUtil.round(entryController.getTotalDebit()) != CommonUtil.round(entryController.getTotalCredit())){
					list.add((AccountEntry)entryController.getTo());
				}
			}
		} catch (ManagerBeanException e) {
			throw new AccountingCheckException(e.getMessage(), e);
		} finally {
			HibernateUtil.setCloseSession(prev);
		}
	}
	
	public List <AccountEntry> getList(){
		return list;
	}

}