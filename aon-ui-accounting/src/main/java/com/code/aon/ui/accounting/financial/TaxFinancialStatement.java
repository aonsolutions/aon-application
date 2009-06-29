package com.code.aon.ui.accounting.financial;

import java.util.Calendar;
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
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4750*|477*|472*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				double amount = 0; 
				double subtotal = 0;
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period.getDeadline(), account.getId(), false, false);
					amount = CommonUtil.round(balance.getCredit() - balance.getDebit() ) ;
					if (account.getId().startsWith("472")) {
						amount = CommonUtil.round(amount * -1 ) ;	
					}
					subtotal = CommonUtil.round(subtotal + amount) ; 	  
				}
				FinancialStatement fs = new FinancialStatement();
				fs.setCode( "4750,477,472" );
				fs.setDescription("IVA Pendiente de Pago" );
				fs.setAmount( subtotal );
				setTotal( CommonUtil.round(getTotal() + amount));
				getFinancialStatements().add(fs);
				
				// IRPF PENDIENTE DE PAGO
				criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4751*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				amount = 0;
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), period.getDeadline(), account.getId(), false, false);
					amount = CommonUtil.round(amount + balance.getCredit() - balance.getDebit() ) ;
				}
				fs = new FinancialStatement();
				fs.setCode( "4751" );
				fs.setDescription("IRPF Pendiente de Pago" );
				fs.setAmount( amount );
				setTotal( CommonUtil.round(getTotal() + amount));
				getFinancialStatements().add(fs);
				
				// SS PENDIENTE DE PAGO
				Calendar c = Calendar.getInstance();
				c.setTime(params.getFinancialDate());
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_MONTH, -1);
				criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "4760*");
				criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
						new Boolean(true));
				amount = 0;
				for (ITransferObject to: accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = util.getPeriodBalance(period.getInitiationDate(), c.getTime(), account.getId(), false, false);
					amount = CommonUtil.round(amount + balance.getCredit() - balance.getDebit() ) ;
				}
				fs = new FinancialStatement();
				fs.setCode( "4760" );
				fs.setDescription("SS Pendiente de Pago" );
				fs.setAmount( amount );
				setTotal( CommonUtil.round(getTotal() + amount));
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
