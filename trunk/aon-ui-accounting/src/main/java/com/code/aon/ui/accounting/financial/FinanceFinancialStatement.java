package com.code.aon.ui.accounting.financial;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.account.Account;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.util.ExpressionException;

public class FinanceFinancialStatement extends AbstractFinancialStatement {

	@SuppressWarnings("unchecked")
	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		try {
			if (getFinancialStatements() == null) {
				setFinancialStatements(new LinkedList<FinancialStatement>());
				IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
				
				// Vencimientos Pendientes de Cobro
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(
						financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), false);
				criteria.addEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
				criteria.addGreaterThanOrEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params
						.getExcludeFinanceDate());
				criteria.addLessThanOrEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getFinancialDate());
				ProjectionList pl = new ProjectionList();
				pl.add(Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT)));
				List charges = financeBean.getList(pl, criteria);
				if (charges.size() > 0) {
					Double amount = charges.get(0) != null ? (Double) charges.get(0)
							: new Double(0);
					setTotal(amount.doubleValue());
					FinancialStatement fs = new FinancialStatement();
					fs.setCode(null);
					fs.setDescription("Vencimientos Pendientes de Cobro");
					fs.setAmount(amount);
					fs.setAddition(true);
					getFinancialStatements().add(fs);
				}
				
				// Vencimientos Remesados Pendientes de Cobro
				IManagerBean fbatchBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
				String aliasStatus = fbatchBean.getFieldName( IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS );
				String aliasDate = fbatchBean.getFieldName( IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ISSUE_DATE);
				String aliasAmount = fbatchBean.getFieldName( IFinanceAlias.FINANCE_BATCH_DETAIL_AMOUNT);
				criteria = new Criteria();
				criteria.addEqualExpression(aliasStatus, FinanceStatus.BATCHED);
				criteria.addGreaterThanOrEqualExpression(aliasDate, params.getExcludeFinanceDate());
				criteria.addLessThanOrEqualExpression(aliasDate, params.getFinancialDate());
				pl = new ProjectionList();
				pl.add(Projection.sum(aliasAmount));
				List payments = fbatchBean.getList(pl, criteria);
				if (payments.size() > 0) {
					Double amount = payments.get(0) != null ? (Double) payments.get(0) : new Double(0);
					setTotal(CommonUtil.round(getTotal() + amount.doubleValue()));
					FinancialStatement fs = new FinancialStatement();
					fs.setCode(null);
					fs.setDescription("Vencimientos Remesados Pendientes de Cobro");
					fs.setAmount(amount);
					fs.setAddition(true);
					getFinancialStatements().add(fs);
				}
				
				
				// Vencimientos Pendientes de Pago
				criteria = new Criteria();
				criteria.addEqualExpression(
						financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), true);
				criteria.addEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS), FinanceStatus.PENDING);
				criteria.addGreaterThanOrEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params
						.getExcludeFinanceDate());
				criteria.addLessThanOrEqualExpression(financeBean
						.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getFinancialDate());
				pl = new ProjectionList();
				pl.add(Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT)));
				List batched = financeBean.getList(pl, criteria);
				if (batched.size() > 0) {
					Double amount = batched.get(0) != null ? (Double) batched.get(0)
							: new Double(0);
					setTotal(CommonUtil.round(getTotal() - amount.doubleValue()));
					FinancialStatement fs = new FinancialStatement();
					fs.setCode(null);
					fs.setDescription("Vencimientos Pendientes de Pago");
					fs.setAmount(amount);
					fs.setAddition(false);
					getFinancialStatements().add(fs);
				}

				// Remuneraciones Pendientes de Pago
				IManagerBean accountBean = BeanManager.getManagerBean(Account.class);
				Period period = getAccountingUtil().getPeriod(params.getFinancialDate());
				criteria = new Criteria();
				criteria.addExpression(accountBean.getFieldName(IAccountAlias.ACCOUNT_ID), "465*");
				criteria.addEqualExpression(accountBean
						.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED), new Boolean(true));
				double amount = 0;
				for (ITransferObject to : accountBean.getList(criteria)) {
					Account account = (Account) to;
					Balance balance = getAccountingUtil().getPeriodBalance(period.getInitiationDate(), period
							.getDeadline(), account.getId(), false, false);
					amount = CommonUtil.round(amount + balance.getCredit() - balance.getDebit());
				}
				if (amount != 0) {
					FinancialStatement fs = new FinancialStatement();
					fs.setCode("465");
					fs.setDescription("Remuneraciones Pendientes de Pago");
					fs.setAmount(amount);
					fs.setAddition(false);
					setTotal(CommonUtil.round(getTotal() - amount));
					getFinancialStatements().add(fs);
				}
			}
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
	}

	@Override
	public String getLabel() {
		return "PAGOS Y COBROS";
	}

}
