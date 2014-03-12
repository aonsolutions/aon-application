package com.code.aon.ui.accounting.event;

import com.code.aon.account.Account;
import com.code.aon.account.bridge.util.AccountConstants;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountControllerCascadeListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
		try {
			Account account = (Account)event.getController().getTo();
			if(!account.isEntryEnabled() && isExtended(account)){
				throw new ControllerListenerException("Imposible borrar cuenta. La cuenta está extendida.");
			}else if (isSystemAccount(account)){
				throw new ControllerListenerException("Imposible borrar cuenta. Cuenta necesaria para el sistema.");
			}else if (obtainAccountEntryDetailCount(account) != 0){
				throw new ControllerListenerException("Imposible borrar cuenta. Hay apuntes contables asociados.");
			}
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage());
		}
	}

	private boolean isExtended(Account account) throws ManagerBeanException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addNotEqualExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_ID), account.getId());
		criteria.addExpression(ExpressionUtilities.getLikeExpression(accountBean.getFieldName(IEntityAlias.ACCOUNT_CODE), account.getCode()+"%"));
		return accountBean.getCount(criteria) > 0;
	}

	private boolean isSystemAccount(Account account) {
		for(String systemAccount : AccountConstants.getSystemAccounts()){
			if(systemAccount.equals(account.getId())){
				return true;
			}
		}
		return false;
	}

	private int obtainAccountEntryDetailCount(Account account) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ID), account.getId());
		return accountEntryDetailBean.getCount(criteria); 
	}
	
}