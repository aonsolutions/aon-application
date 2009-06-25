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

public class TaxFinancialStatement extends AbstractFinancialStatement {

	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		try {
			if (getFinancialStatements() == null) {
				setFinancialStatements( new LinkedList<FinancialStatement>());
				AccountingUtil util = new AccountingUtil();
				Period period = AccountingPeriodUtil.getPeriod(params.getFinancialDate());
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				
				// IVA Pendiente de Pago
				Criteria criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4750*");
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "477*");
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "472*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period.getDeadline(), account.getId(), false, false);
					double amount = CommonUtil.round(balance.getCredit() - balance.getDebit() ) ;
					if (account.getId().startsWith("472")) {
						amount = CommonUtil.round(amount * -1 ) ;	
					}
					setTotal( CommonUtil.round(getTotal() + amount));  
				}
				FinancialStatement fs = new FinancialStatement();
				fs.setCode( "4750,477,472" );
				fs.setDescription("IVA Pendiente de Pago" );
				fs.setAmount( getTotal() );
				getFinancialStatements().add(fs);
				
				// IRPF PENDIENTE DE PAGO
				criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4751*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period.getDeadline(), account.getId(), false, false);
					double amount = CommonUtil.round(balance.getCredit() - balance.getDebit() ) ;
					setTotal( CommonUtil.round(getTotal() + amount));  
				}
				fs = new FinancialStatement();
				fs.setCode( "4751" );
				fs.setDescription("IRPF Pendiente de Pago" );
				fs.setAmount( getTotal() );
				getFinancialStatements().add(fs);
				
				// SS PENDIENTE DE PAGO
				criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4760*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period.getDeadline(), account.getId(), false, false);
					double amount = CommonUtil.round(balance.getCredit() - balance.getDebit() ) ;
					setTotal( CommonUtil.round(getTotal() + amount));  
				}
				fs = new FinancialStatement();
				fs.setCode( "4760" );
				fs.setDescription("SS Pendiente de Pago" );
				fs.setAmount( getTotal() );
				getFinancialStatements().add(fs);
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	@Override
	public String getLabel() {
		return "IMPUESTOS";
	}

	@Override
	public double getTotalForSummary() {
		return CommonUtil.round(getTotal() * -1);
	}

}
