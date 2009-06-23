package com.code.aon.ui.accounting.financial;

import java.util.LinkedList;
import java.util.List;

import com.code.aon.accounting.ProfitAndLossComparison;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.controller.ProfitAndLossComparisonController;

public class ExpensesFinancialStatement implements IFinancialStatementManager {

	private List<FinancialStatement> list;
	private double total;

	@Override
	public void initialize() {
		list = null;
		total = 0;
	}

	@Override
	public List<FinancialStatement> getFinancialStatements(){
		return list;
	}

	@Override
	public void search(FinancialStatementParams params) throws ManagerBeanException {
		if (list == null) {
			list = new LinkedList<FinancialStatement>();
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
					total = CommonUtil.round(total + dif);
					list.add(fs);
				}
			}
		}
	}

	@Override
	public String getLabel() {
		return "GASTOS (Presupuesto - Acumulado)";
	}

	@Override
	public double getTotal() {
		return total;
	}

	@Override
	public double getTotalForSummary() {
		return CommonUtil.round(total * -1);
	}
}
