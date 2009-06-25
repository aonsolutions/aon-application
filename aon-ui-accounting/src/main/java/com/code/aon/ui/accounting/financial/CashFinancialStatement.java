package com.code.aon.ui.accounting.financial;

import java.util.LinkedList;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;

public class CashFinancialStatement extends  AbstractFinancialStatement {

	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		try {
			if (getFinancialStatements() == null) {
				setFinancialStatements(new LinkedList<FinancialStatement>());
				AccountingUtil util = new AccountingUtil();
				Period period = AccountingPeriodUtil.getPeriod(params.getFinancialDate());
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Criteria criteria = new Criteria();

				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "570*");
				criteria.addEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
				for (ITransferObject to : accountBean.getList(criteria)) {
					Account account = (Account) to;
					FinancialStatement fs = new FinancialStatement();
					fs.setCode(account.getId());
					fs.setDescription(account.getDescription());
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period
							.getDeadline(), account.getId(), false, false);
					double amount = CommonUtil.round(balance.getDebit() - balance.getCredit());
					fs.setAmount(amount);
					setTotal( CommonUtil.round( getTotal() + amount));
					getFinancialStatements().add(fs);
				}
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	@Override
	public String getLabel() {
		return "CAJA";
	}
}
