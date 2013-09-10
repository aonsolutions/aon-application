package com.code.aon.ui.accounting.controller.report;

import static com.code.aon.ui.accounting.IAccountingConstants.ACCOUNTING_GROSS_MARGIN;
import static com.code.aon.ui.accounting.IAccountingConstants.ACCOUNTING_TOTAL_EXPENSES;
import static com.code.aon.ui.accounting.IAccountingConstants.ACCOUNTING_TOTAL_RESULT;
import static com.code.aon.ui.accounting.IAccountingConstants.ACCOUNTING_TOTAL_RESULT_LOSS;
import static com.code.aon.ui.accounting.IAccountingConstants.ACCOUNTING_TOTAL_RESULT_NULL;
import static com.code.aon.ui.accounting.IAccountingConstants.BUNDLE_KEY;
import static com.code.aon.ui.accounting.IAccountingConstants.PROFIT_AND_LOSS_CONTROLLER_NAME;
import static com.code.aon.ui.common.ICommonMessages.PROFIT;

import java.util.Collection;
import java.util.LinkedList;

import javax.faces.event.AbortProcessingException;

import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryMonthly;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.util.AonUtil;

public class ProfitAndLossMonthlyReportController implements ICollectionProvider {

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
			gmTotal.setDescription(AonUtil.getMessage(BUNDLE_KEY, ACCOUNTING_GROSS_MARGIN));
			gmTotal.setCredit(grossMargin.getCredit());
			gmTotal.setDebit(grossMargin.getDebit());
			Double[] months0 = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
			for (Summary s : grossMargin.getSummaryList()) {
				SummaryMonthly sm = (SummaryMonthly) s;
				for (int i = 0; i < sm.getMonths().length; i++) {
					if (sm.getCode().startsWith("7")) {
						months0[i] = CommonUtil.round(months0[i] + sm.getMonths()[i]);
					} else {
						months0[i] = CommonUtil.round(months0[i] - sm.getMonths()[i]);
					}
				}
			}
			gmTotal.setMonths(months0);
			list.add(gmTotal);

			SummaryCollection totalExpenses = sp.getTotalExpensesSummaryCollection(params);
			list.addAll(totalExpenses.getSummaryList());
			SummaryMonthly teTotal = new SummaryMonthly();
			teTotal.setDescription(AonUtil.getMessage(BUNDLE_KEY, ACCOUNTING_TOTAL_EXPENSES));
			teTotal.setCredit(totalExpenses.getCredit());
			teTotal.setDebit(totalExpenses.getDebit());
			Double[] months1 = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
			for (Summary s : totalExpenses.getSummaryList()) {
				SummaryMonthly sm = (SummaryMonthly) s;
				for (int i = 0; i < sm.getMonths().length; i++) {
					months1[i] = CommonUtil.round(months1[i] + sm.getMonths()[i]);
				}
			}
			teTotal.setMonths(months1);
			list.add(teTotal);

			SummaryMonthly result = new SummaryMonthly();
			Double[] months2 = {0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0,0.0};
			for (int i = 0; i < months2.length; i++) {
				months2[i] = CommonUtil.round(gmTotal.getMonths()[i] - teTotal.getMonths()[i]);
			}
			result.setMonths(months2);

			StringBuilder r = new StringBuilder();
			r.append(AonUtil.getMessage(BUNDLE_KEY, ACCOUNTING_TOTAL_RESULT));
			r.append(" (");
			double total = result.getTotal();
			if (total > 0) {
				r.append(AonUtil.getMessage(PROFIT));
			} else if (total < 0) {
				r.append(AonUtil.getMessage(BUNDLE_KEY, ACCOUNTING_TOTAL_RESULT_LOSS));
			} else {
				r.append(AonUtil.getMessage(BUNDLE_KEY, ACCOUNTING_TOTAL_RESULT_NULL));
			}
			r.append(")");
			result.setDescription(r.toString());
			list.add(result);
			return list;
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} finally {
			params.setMonthlyGrouping(false);
		}

	}

	@Override
	public Collection<?> getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}

}
