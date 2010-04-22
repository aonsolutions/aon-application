package com.code.aon.ui.accounting.controller;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.util.AonUtil;

public class ProfitAndLossReportController implements ICollectionProvider {

	
	private final static Logger LOGGER = LoggerFactory
			.getLogger(ProfitAndLossReportController.class);
	
	private static final String TRIAL_BALANCE_CONTROLLER_NAME = "trialBalance";
	private static final String ACCOUNTING_BUNDLE = "accountingBundle";
	private String accountStatement;	
	private SummaryCollection grossMargin;
	private SummaryCollection totalExpenses;
	private SummaryProviderParameters parameters;
	private List<ProfitAndLossReportController> collections;
	private String graphName;
	private List<Summary> netExpenses;
	private List<Summary> salesList;
	private List<Summary> purchaseList;
	private List<Summary> grossMarginList= new LinkedList<Summary>();
	private Double totalSales;
	private Double totalPurchases;
	private boolean budgeted;
	
	private AccountingUtil accountingUtil;
	
	private AccountingUtil getAccountingUtil() {
		if (accountingUtil == null) {
			accountingUtil = new AccountingUtil();
		}
		return accountingUtil;
	}

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
		double gm = CommonUtil.round(grossMargin.getCredit() - grossMargin.getDebit());
		double te = CommonUtil.round(totalExpenses.getDebit() - totalExpenses.getCredit());
		return CommonUtil.round(gm - te);
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
			try {
				p.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
			} catch (ManagerBeanException e) {
				p.setPeriod(null);
			}
			p.setBudgeted(isBudgeted());
			p.setLowerLevelVisible(false);
			boolean excludeOperating = false;
			if (p.getPeriod() != null) {
				try {
					excludeOperating = getAccountingUtil().existsEntry(p.getPeriod(), AccountEntryType.OPERATING, p.getSecurityLevel());
				} catch (ManagerBeanException e) {
					LOGGER.warn("No se pudo saber si existe asiento de explotacion",e);					
				}	
			}
			p.setExcludeOperatingEntry(excludeOperating);
			setParameters(p);
		}
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}

	public void onStatement(ActionEvent event) {
		TrialBalanceController c = (TrialBalanceController) AonUtil.getRegisteredBean(TRIAL_BALANCE_CONTROLLER_NAME);
		c.onReset(event);
		SummaryProviderParameters spp = new SummaryProviderParameters();
		spp.setAccountExpression(getAccountStatement());
		spp.setDate(getParameters().getDate());
		spp.setFromDate(getParameters().getFromDate());
		spp.setToDate(getParameters().getToDate());
		spp.setAccountLevel(getParameters().getAccountLevel());
		spp.setBudgeted(getParameters().isBudgeted());
		spp.setLowerLevelVisible(getParameters().isLowerLevelVisible());
		spp.setNoTouchedAccountVisible(getParameters().isNoTouchedAccountVisible());
		spp.setPeriod(getParameters().getPeriod());
		spp.setSecurityLevel(getParameters().getSecurityLevel());
		c.setParameters(spp);		
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
	
	public void onNetExpenses(ActionEvent event) {
		List<Summary> summaryList;
		netExpenses = new LinkedList<Summary>();
		summaryList = getTotalExpenses().getSummaryList();
		for (Summary summary: summaryList) {
			if ((summary.getId().substring(0, 3).equals("640"))
					|| (summary.getId().substring(0, 3).equals("642"))) {

			} else {
				netExpenses.add(summary);
			}
		}
	}

	public void onGenerateLists(ActionEvent event) {
		generateSalesList();
		generatePurchasesList();
		generateGrossMarginsList();
	}

	public void generateSalesList() {

		Double amount = 0.0;
		List<Summary> summaryList;
		salesList = new LinkedList<Summary>();
		summaryList = getGrossMargin().getSummaryList();
		for (Summary summary: summaryList) {
			if (summary.getId().substring(0, 1).equals("7")) {
				salesList.add(summary);
				amount += summary.getCreditBalance();
			}
		}
		setTotalSales(amount);
		Summary s = new Summary();
		s.setId("Total Ventas");
		s.setDescription("Total Ventas");
		s.setCredit(amount);
		salesList.add(0, s);
		grossMarginList.add(s);
	}

	public void generatePurchasesList() {
		Double amount = 0.0;
		List<Summary> summaryList;
		purchaseList = new LinkedList<Summary>();
		summaryList = getGrossMargin().getSummaryList();
		for (Summary summary: summaryList) {
			if (summary.getId().substring(0, 2).equals("60")) {
				purchaseList.add(summary);
				amount += summary.getUnpaidBalance();
			}
		}
		setTotalPurchases(amount);
		Summary s = new Summary();
		s.setId("Total Compras");
		s.setDescription("Total Compras");
		s.setDebit(amount);
		purchaseList.add(0, s);
		grossMarginList.add(s);
	}

	public void generateGrossMarginsList() {

		Summary s = new Summary();
		s.setId("Margen Bruto");
		s.setDescription("Margen Bruto");
		s.setDebit(totalSales - totalPurchases);
		grossMarginList.add(s);
	}
	
	


	public Collection<Summary> getCollection() {
		Collection<Summary> list = new LinkedList<Summary>();
		list.addAll(getGrossMargin().getSortedSummaryList());
		Summary gmTotal = new Summary();
		gmTotal.setDescription(AonUtil.getMessage(ACCOUNTING_BUNDLE, "accounting_gross_margin"));
		gmTotal.setCredit(getGrossMargin().getCredit());
		gmTotal.setDebit(getGrossMargin().getDebit());
		list.add(gmTotal);

		list.addAll(getTotalExpenses().getSortedSummaryList());
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
			result.setDebit(CommonUtil.round(getTotalResult() * (-1)));
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

	public List<ProfitAndLossReportController> getController() {
		if (collections == null) {
			collections = new LinkedList<ProfitAndLossReportController>();
			// Se añade una WeakReference para evitar que el objeto "this" cruce
			// referencia directa con la lista.
			WeakReference<ProfitAndLossReportController> weakThis = new WeakReference<ProfitAndLossReportController>(this);
			collections.add(weakThis.get());
		}
		return collections;
	}

	public String getReportTitle() {
		return budgeted ? 
				AonUtil.getMessage(ACCOUNTING_BUNDLE,"accounting_budgeted_balance_sheet_module") : 
				AonUtil.getMessage(ACCOUNTING_BUNDLE,"accounting_profit_and_loss_module");
	}
	
	public String getGraphName() {
		return graphName;
	}

	public void setGraphName(String graphName) {
		this.graphName = graphName;
	}
	

	public List<Summary> getNetExpenses() {
		return netExpenses;
	}

	public void setNetExpenses(List<Summary> netExpenses) {
		this.netExpenses = netExpenses;
	}
	
	public List<Summary> getSalesList() {
		return salesList;
	}

	public void setSalesList(List<Summary> salesList) {
		this.salesList = salesList;
	}

	public List<Summary> getPurchaseList() {
		return purchaseList;
	}

	public void setPurchaseList(List<Summary> purchaseList) {
		this.purchaseList = purchaseList;
	}

	public Double getTotalSales() {
		return totalSales;
	}

	public void setTotalSales(Double totalSales) {
		this.totalSales = totalSales;
	}

	public Double getTotalPurchases() {
		return totalPurchases;
	}

	public void setTotalPurchases(Double totalPurchases) {
		this.totalPurchases = totalPurchases;
	}

	public List<Summary> getGrossMarginList() {
		return grossMarginList;
	}

	public void setGrossMarginList(List<Summary> grossMarginList) {
		this.grossMarginList = grossMarginList;
	}
	
	public boolean isDateValid() {
		if (getParameters().getPeriod() != null) {
			return true;
		}
		Date from = getParameters().getFromDate();
		Date to = getParameters().getToDate();
		if ( from == null || to == null) {
			return false;
		}
		if (to.compareTo(from) < 0 ) {
			return false;
		}
		return true;
	}
}
