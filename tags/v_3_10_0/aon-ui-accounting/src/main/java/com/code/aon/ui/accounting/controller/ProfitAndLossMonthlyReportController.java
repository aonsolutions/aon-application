package com.code.aon.ui.accounting.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryMonthly;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class ProfitAndLossMonthlyReportController implements ICollectionProvider {

	private static final String PROFIT_AND_LOSS_CONTROLLER_NAME = "profitAndLossReport";
	private static final String ACCOUNTING_BUNDLE = "accountingBundle";
	@Override
	public Collection<?> getCollection() {
		ProfitAndLossReportController c = (ProfitAndLossReportController) AonUtil
				.getRegisteredBean(PROFIT_AND_LOSS_CONTROLLER_NAME);
		SummaryProviderParameters params = c.getParameters();
		try {
			params.setMonthlyGrouping(true);
			Collection<Summary> list = new LinkedList<Summary>();

			SummaryProvider sp = new SummaryProvider();
			SummaryCollection grossMargin = sp.getGrossMarginSummaryCollection(params);
			list.addAll(grossMargin.getSummaryList());
			SummaryMonthly gmTotal = new SummaryMonthly();
			gmTotal.setDescription(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_gross_margin"));
			gmTotal.setCredit(grossMargin.getCredit());
			gmTotal.setDebit(grossMargin.getDebit());
			List<Double> months = new ArrayList<Double>(12);
			for (int i = 0; i < 12; i++) {
				months.add(new Double(0));
			}
			for (Summary s : grossMargin.getSummaryList()) {
				SummaryMonthly sm = (SummaryMonthly) s;
				for (int i = 0; i < sm.getMonths().size(); i++) {
					months.set(i, round(months.get(i) + sm.getMonths().get(i)));
				}
			}
			gmTotal.setMonths(months);
			list.add(gmTotal);

			SummaryCollection totalExpenses = sp.getTotalExpensesSummaryCollection(params);
			list.addAll(totalExpenses.getSummaryList());
			SummaryMonthly teTotal = new SummaryMonthly();
			teTotal.setDescription(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_expenses"));
			teTotal.setCredit(totalExpenses.getCredit());
			teTotal.setDebit(totalExpenses.getDebit());
			months = new ArrayList<Double>(12);
			for (int i = 0; i < 12; i++) {
				months.add(new Double(0));
			}
			for (Summary s : totalExpenses.getSummaryList()) {
				SummaryMonthly sm = (SummaryMonthly) s;
				for (int i = 0; i < sm.getMonths().size(); i++) {
					months.set(i, round(months.get(i) + sm.getMonths().get(i)));
				}
			}
			teTotal.setMonths(months);
			list.add(teTotal);

			SummaryMonthly result = new SummaryMonthly();

			months = new ArrayList<Double>(12);
			for (int i = 0; i < 12; i++) {
				months.add(new Double(0));
			}
			for (int i = 0; i < months.size(); i++) {
				months.set(i, round(gmTotal.getMonths().get(i) - teTotal.getMonths().get(i)));
			}
			result.setMonths(months);

			StringBuilder r = new StringBuilder();
			r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result"));
			r.append(" (");
			double total = result.getTotal();
			if (total > 0) {
				r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_profit"));
			} else if (total < 0) {
				r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_loss"));
			} else {
				r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_null"));
			}
			r.append(")");
			result.setDescription(r.toString());
			list.add(result);
			return list;
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			params.setMonthlyGrouping(true);
		}

	}

	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

}
