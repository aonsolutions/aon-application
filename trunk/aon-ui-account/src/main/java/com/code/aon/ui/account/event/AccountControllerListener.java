package com.code.aon.ui.account.event;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.model.DataModel;

import com.code.aon.account.Account;
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class AccountControllerListener extends ControllerAdapter {

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			DataModel model = event.getController().getModel();
			model.setRowIndex((model.getRowCount() > 0?1:-1));
			while (model.isRowAvailable()) {
				if (model.getRowData().equals(event.getController().getTo())) {
					break;
				}
				model.setRowIndex(model.getRowIndex() + 1);

			}
			event.getController().onSelect(null);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		try {
			Account toAccount = (Account)event.getController().getTo();
			toAccount.setEntryEnabled(true);
			String accountId = toAccount.getId().substring(0,toAccount.getId().length() - 1);
			int length = accountId.length();
			IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = null;
			List accountList = new LinkedList();
			for(int i=1; i<=length; i++){
				if(criteria == null){
					criteria = new Criteria();
				}
				criteria.addOrExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), accountId);
				accountId = accountId.substring(0,accountId.length() - 1);
			}
			accountList = (criteria == null?new LinkedList():accountBean.getList(criteria));
			if(accountList.size() > 0){
				checkAccountEntryDetails(accountList);
			}
			Iterator iter = accountList.iterator();
			while(iter.hasNext()){
				Account account = (Account)iter.next();
				account.setEntryEnabled(false);
				accountBean.update(account);
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		} catch (ExpressionException e) {
			throw new ControllerListenerException(e);
		}
	}

	@SuppressWarnings("unchecked")
	private void checkAccountEntryDetails(List accountList) throws ManagerBeanException, ExpressionException, ControllerListenerException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Iterator iter = accountList.iterator();
		Criteria criteria = new Criteria();
		while(iter.hasNext()){
			Account account = (Account)iter.next();
			criteria.addOrExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), account.getId());
			criteria.addOrExpression(accountEntryDetailBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_DETAIL_BALANCING_ACCOUNT_ID), account.getId());
		}
		if(criteria != null && accountEntryDetailBean.getCount(criteria) > 0){
			throw new ControllerListenerException("Unable to create account");
		}
	}
}