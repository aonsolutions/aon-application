package com.code.aon.account.bridge.event;

import com.code.aon.account.bridge.AccountEntryBankStatement;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.BankStatementLink;
import com.code.aon.finance.enumeration.StatementStatus;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;

public class AccountEntryBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		AccountEntry entry = (AccountEntry) evt.getTo();
		try {
			IManagerBean statementBean = BeanManager.getManagerBean(BankStatement.class);
			IManagerBean accEntryStatementBean = BeanManager.getManagerBean(AccountEntryBankStatement.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryStatementBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_BANK_STATEMENT_ACCOUNT_ENTRY_ID), entry.getId());
			for (ITransferObject ito : accEntryStatementBean.getList(criteria)) {
				AccountEntryBankStatement accEntryStatement = (AccountEntryBankStatement)ito;
				accEntryStatementBean.remove(ito);

				BankStatement statement = accEntryStatement.getBankStatement();
				IManagerBean statementLinkBean = BeanManager.getManagerBean(BankStatementLink.class);
				criteria = new Criteria();
				criteria.addEqualExpression(statementLinkBean.getFieldName(IEntityAlias.BANK_STATEMENT_LINK_BANK_STATEMENT_ID), statement.getId());
				statement.setStatus((statementLinkBean.getCount(criteria) > 0) ? StatementStatus.CHECKED : StatementStatus.PENDING);
				statementBean.update(statement);
			}
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(),e);
			
		}
	}
}