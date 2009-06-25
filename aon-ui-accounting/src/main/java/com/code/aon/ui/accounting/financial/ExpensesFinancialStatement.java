package com.code.aon.ui.accounting.financial;

import java.util.LinkedList;

import com.code.aon.accounting.ProfitAndLossComparison;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.controller.ProfitAndLossComparisonController;

public class ExpensesFinancialStatement extends AbstractFinancialStatement {

	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		if (getFinancialStatements() == null) {
			setFinancialStatements(new LinkedList<FinancialStatement>());
			ProfitAndLossComparisonController pc = new ProfitAndLossComparisonController();
			SummaryProviderParameters spp = new SummaryProviderParameters();
			spp.setFromDate(params.getIncludeExpensesDate());
			spp.setToDate(params.getFinancialDate());
			pc.setParameters(spp);
			pc.loadCollection();
			for (ProfitAndLossComparison to : pc.getList()) {
				double dif = to.getDifference() * -1;
				if (to.getId() != null && to.getId().startsWith("6") && !to.getId().startsWith("60") && dif > 0) {
					FinancialStatement fs = new FinancialStatement();
					fs.setCode(to.getId());
					fs.setDescription(to.getDescription());
					fs.setAmount(dif);
					setTotal(CommonUtil.round(getTotal() + dif));
					getFinancialStatements().add(fs);
				}
			}
		}
	}

	@Override
	public String getLabel() {
		return "GASTOS (Presupuesto - Acumulado)";
	}

	@Override
	public double getTotalForSummary() {
		return CommonUtil.round(getTotal() * -1);
	}
}
