package com.code.aon.ui.accounting.financial;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;

public class FinanceFinancialStatement extends AbstractFinancialStatement {

	@SuppressWarnings("unchecked")
	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		if (getFinancialStatements() == null) {
			setFinancialStatements( new LinkedList<FinancialStatement>() );
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), false );
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS ), FinanceStatus.PENDING );
			criteria.addGreaterThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getExcludeFinanceDate());
			criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getFinancialDate());
			ProjectionList pl = new ProjectionList();
			pl.add(Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT)));
			List charges = financeBean.getList(pl, criteria);
			if (charges.size() > 0) {
				Double amount = charges.get(0) != null ? (Double) charges.get(0) : new Double(0);
				setTotal( amount.doubleValue()); 
				FinancialStatement fs = new FinancialStatement();
				fs.setCode( null );
				fs.setDescription("Cobros Pendientes" );
				fs.setAmount(amount);
				getFinancialStatements().add(fs);
			}

			criteria = new Criteria();
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_PAYMENT), true );
			criteria.addEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_FINANCE_STATUS ), FinanceStatus.PENDING );
			criteria.addGreaterThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getExcludeFinanceDate());
			criteria.addLessThanOrEqualExpression(financeBean.getFieldName(IFinanceAlias.FINANCE_DUE_DATE), params.getFinancialDate());
			pl = new ProjectionList();
			pl.add(Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT)));
			List payments = financeBean.getList(pl, criteria);
			if (payments.size() > 0) {
				Double amount = payments.get(0) != null ? (Double) payments.get(0) : new Double(0);
				setTotal( CommonUtil.round( getTotal() - amount.doubleValue()));
				FinancialStatement fs = new FinancialStatement();
				fs.setCode( null );
				fs.setDescription("Pagos Pendientes" );
				fs.setAmount(amount);
				getFinancialStatements().add(fs);
			}
}
	}

	@Override
	public String getLabel() {
		return "SALDO TESORERÍA";
	}

}
