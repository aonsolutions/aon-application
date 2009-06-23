package com.code.aon.ui.accounting.controller;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.util.AonUtil;

public class ProfitAndLossReportController implements ICollectionProvider {

	private static final String TRIAL_BALANCE_CONTROLLER_NAME = "trialBalance";
	private static final String ACCOUNTING_BUNDLE = "accountingBundle";
	private String accountStatement;

	private SummaryCollection grossMargin;
	private SummaryCollection totalExpenses;
	private SummaryProviderParameters parameters;
	private List<ProfitAndLossReportController> collections;

	private boolean budgeted;

	public boolean isBudgeted() {
		return budgeted;
	}

	public void setBudgeted(boolean budgeted) {
		this.budgeted = budgeted;
	}

	public SummaryCollection getGrossMargin() {
		return grossMargin;
	}

	public void setGrossMargin(SummaryCollection grossMargin) {
		this.grossMargin = grossMargin;
	}

	public SummaryCollection getTotalExpenses() {
		return totalExpenses;
	}

	public void setTotalExpenses(SummaryCollection totalExpenses) {
		this.totalExpenses = totalExpenses;
	}

	public double getTotalResult() {
		double gm = round(grossMargin.getCredit() - grossMargin.getDebit());
		double te = round(totalExpenses.getDebit() - totalExpenses.getCredit());
		return round(gm - te);
	}

	public String getAccountStatement() {
		return accountStatement;
	}

	public void setAccountStatement(String accountStatement) {
		this.accountStatement = accountStatement;
	}

	public SummaryProviderParameters getParameters() {
		if (parameters == null) {
			SummaryProviderParameters p = new SummaryProviderParameters();
			p.setBudgeted(isBudgeted());
			p.setLowerLevelVisible(false);
			setParameters(p);
		}
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	public void onStatement(ActionEvent event) {
		TrialBalanceController c = (TrialBalanceController) AonUtil
				.getRegisteredBean(TRIAL_BALANCE_CONTROLLER_NAME);
		c.onReset(event);
		c.setParameters(getParameters());
		c.onSearch(event);
	}

	public void onEditSearch(ActionEvent event) {
		setParameters(null);
		this.collections = null;
	}

	public void calculateSummaryCollections(ActionEvent event) {
		this.calculateGrossMarginSummaryCollection();
		this.calculateTotalExpensesSummaryCollection();
	}

	private void calculateTotalExpensesSummaryCollection() {
		try {
			SummaryProvider sp = new SummaryProvider();
			this.setTotalExpenses(sp.getTotalExpensesSummaryCollection(getParameters()));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void calculateGrossMarginSummaryCollection() {
		try {
			SummaryProvider sp = new SummaryProvider();
			this.setGrossMargin(sp.getGrossMarginSummaryCollection(getParameters()));
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public Collection<Summary> getCollection() {
		Collection<Summary> list = new LinkedList<Summary>();
		list.addAll(getGrossMargin().getSummaryList());
		Summary gmTotal = new Summary();
		gmTotal.setDescription(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_gross_margin"));
		gmTotal.setCredit(getGrossMargin().getCredit());
		gmTotal.setDebit(getGrossMargin().getDebit());
		list.add(gmTotal);

		list.addAll(getTotalExpenses().getSummaryList());
		Summary teTotal = new Summary();
		teTotal.setDescription(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_expenses"));
		teTotal.setCredit(getTotalExpenses().getCredit());
		teTotal.setDebit(getTotalExpenses().getDebit());
		list.add(teTotal);

		Summary result = new Summary();
		StringBuilder r = new StringBuilder();
		r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result"));
		r.append(" (");
		if (getTotalResult() > 0) {
			r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_profit"));
		} else if (getTotalResult() < 0) {
			r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_loss"));
		} else {
			r.append(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_total_result_null"));
		}
		r.append(")");
		result.setDescription(r.toString());
		if (getTotalResult() < 0) {
			result.setDebit(round(getTotalResult() * (-1)));
		} else {
			result.setCredit(getTotalResult());
		}
		list.add(result);
		return list;
	}

	@Override
	public Collection<Summary> getCollection(boolean arg0) throws ManagerBeanException {
		return getCollection();
	}

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}

	public List<ProfitAndLossReportController> getController() {
		if (collections == null) {
			collections = new LinkedList<ProfitAndLossReportController>();
			// Se añade una WeakReference para evitar que el objeto "this" cruce
			// referencia directa con la lista.
			WeakReference<ProfitAndLossReportController> weakThis = new WeakReference<ProfitAndLossReportController>(
					this);
			collections.add(weakThis.get());
		}
		return collections;
	}

	public String getReportTitle() {
		return budgeted ? 
				AonUtil.getMessage(ACCOUNTING_BUNDLE,"accounting_budgeted_balance_sheet_module") : 
				AonUtil.getMessage(ACCOUNTING_BUNDLE,"accounting_profit_and_loss_module");
	}
}
