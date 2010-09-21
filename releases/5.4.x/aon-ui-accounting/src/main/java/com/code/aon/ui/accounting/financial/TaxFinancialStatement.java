package com.code.aon.ui.accounting.financial;

import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.Month;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.util.AonUtil;

public class TaxFinancialStatement extends AbstractFinancialStatement {

	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		try {
			if (getFinancialStatements() == null) {
				setFinancialStatements( new LinkedList<FinancialStatement>());
				Period period = getAccountingUtil().getPeriod(params.getFinancialDate());

				//470*  -> HP, deudora por diversos conceptos
				addStatement(period.getInitiationDate(), period.getDeadline(), "470", false);
				//471*  -> SS Deudora
				addStatement(period.getInitiationDate(), period.getDeadline(), "471", false);
				//472*  -> HP, IVA Soportado
				addStatement(period.getInitiationDate(), period.getDeadline(), "472", false);
				//4750* -> HP, Acreedor por IVA
				addStatement(period.getInitiationDate(), period.getDeadline(), "4750", true);
				//4751* -> HP, Acreedor por Retenciones
				addStatement(period.getInitiationDate(), period.getDeadline(), "4751", true);
				//4752* -> HP, Acreedor por Imp. Sociedades
				addStatement(period.getInitiationDate(), period.getDeadline(), "4752", true);

				//476*  -> SS Acreedora (-1 mes)
				Calendar c = Calendar.getInstance();
				c.setTime(params.getFinancialDate());
				c.set(Calendar.DAY_OF_MONTH, 1);
				c.add(Calendar.DAY_OF_MONTH, -1);
				FinancialStatement fs = addStatement(period.getInitiationDate(), c.getTime(), "476", true);
				Month m = Month.getMonthByValue( c.get(Calendar.MONTH));
				fs.setDescription(fs.getDescription() + " (hasta "  + m.getName(AonUtil.getCurrentLocale())+ ")");

				//477*  -> HP, IVA Repercutido
				addStatement(period.getInitiationDate(), period.getDeadline(), "477", true);
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	@Override
	public String getLabel() {
		return "ADMINISTRACIONES PÚBLICAS";
	}

	@Override
	public double getTotalForSummary() {
		return CommonUtil.round(getTotal() * -1);
	}

	private FinancialStatement addStatement(Date fromDate, Date toDate, String prefix, boolean addition) throws ManagerBeanException, ExpressionException {
		IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
		Criteria criteria = new Criteria();
		criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), prefix + "*");
		criteria.addEqualExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED),
				new Boolean(true));
		double amount = 0; 
		double subtotal = 0;
		for (ITransferObject to: accountBean.getList(criteria)) {
			Account account = (Account) to;
			Balance balance = getAccountingUtil().getPeriodBalance(fromDate, toDate, account.getId(), false, false);
			amount = CommonUtil.round(balance.getCredit() - balance.getDebit());
			subtotal = CommonUtil.round(subtotal + amount) ; 	  
		}
		if (subtotal != 0) {
			FinancialStatement fs = new FinancialStatement();
			Account account = (Account) accountBean.get(prefix);
			fs.setCode( prefix );
			fs.setDescription(account.getDescription());
			setTotal( CommonUtil.round(getTotal() + subtotal));
			subtotal = CommonUtil.round(subtotal * (addition?1:-1)) ;
			fs.setAmount( subtotal );
			fs.setAddition(addition);
			getFinancialStatements().add(fs);
			return fs;
		}
		return null;
	}
}
