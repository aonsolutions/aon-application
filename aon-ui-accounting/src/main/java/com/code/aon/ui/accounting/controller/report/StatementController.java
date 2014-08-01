package com.code.aon.ui.accounting.controller.report;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class StatementController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final String STATEMENT_DETAIL_CONTROLLER_NAME = "statementDetail";

	private SummaryProvider sp = new SummaryProvider();

	private Balance previousBalance;
	private Balance periodBalance;

	private List<Balance> detail;
	private DataScrollerState detailState;
	
	private SummaryProviderParameters params;


	public Balance getPreviousBalance() {
		return previousBalance;
	}

	public void setPreviousBalance(Balance previousBalance) {
		this.previousBalance = previousBalance;
	}

	public Balance getPeriodBalance() {
		return periodBalance;
	}

	public void setPeriodBalance(Balance periodBalance) {
		this.periodBalance = periodBalance;
	}

	public SummaryProviderParameters getParams() {
		return params;
	}

	public void setParams(SummaryProviderParameters params) {
		this.params = params;
	}

	@Override
	public void select(ActionEvent event) {
		super.select(event);
		refresh();
	}

	public void onRefresh(ActionEvent event) {
		refresh();
	}

	private void refresh() {
		try {
			IController c = FormUtil.getController(STATEMENT_DETAIL_CONTROLLER_NAME);
			c.onSearch(null);
			initialize();
			initializeAmounts();
			transformDetailModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException("No se pudo actualizar la página."+e.getMessage(), e);
		}
	}


	private void initialize() {
		setPreviousBalance(null);
		setPeriodBalance(null);
		setDetail(null);
	}

	public List<Balance> getDetail() {
		return detail;
	}

	public void setDetail(List<Balance> detail) {
		this.detail = detail;
	}

	public DataModel getDetailModel() {
		return getDetailState().getDirectModel();
	}

	public void setDetailModel(DataModel detailModel) {
		setDetailState(new DataScrollerState(detailModel, "statementDetail"));
	}
	
	public DataScrollerState getDetailState() {
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	private void transformDetailModel() throws ManagerBeanException {
		IController c = FormUtil.getController(STATEMENT_DETAIL_CONTROLLER_NAME);
		DataModel model = c.getModel();
		Balance previous = getPreviousBalance();
		setDetail(new LinkedList<Balance>());
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setRowIndex(i);
			AccountEntryDetail d = (AccountEntryDetail) model.getRowData();
			Balance balance = new Balance();
			balance.setAccountEntry(d.getAccountEntry().getId());
			balance.setAccount(d.getAccount().getCode());
			balance.setDescription(d.getAccount().getDescription());
			balance.setFromDate(d.getAccountEntry().getEntryDate());
			balance.setDebit(d.getDebit());
			balance.setCredit(d.getCredit());
			balance.setConcept(d.getConcept());
			balance.setDocumentNumber(d.getDocumentNumber());
			balance.setBalancingAccount(d.getBalancingAccount() == null ? null : d.getBalancingAccount().getCode());
			balance.setBalancingAccountDescription(d.getBalancingAccount() == null ? null : d.getBalancingAccount().getDescription());
			if (previous != null) {
				balance.dragBalance(previous);
			} else {
				double b = CommonUtil.round(d.getDebit() - d.getCredit());
				if (b > 0) {
					balance.setUnpaidBalance(b);
				} else {
					balance.setCreditBalance(CommonUtil.round(b * (-1)));
				}
			}
			detail.add(balance);
			previous = balance;
		}
		setDetailModel(new SerializableListDataModel(getDetail()));
	}

	private void initializeAmounts() throws ManagerBeanException {
		try {
			Account account = getAccount();
			AccountingUtil accountingUtil = new AccountingUtil();
			Date from = params.getFromDate()==null?params.isPeriodNull()?accountingUtil.getFirstPeriodInitialDate():params.getPeriod().getInitiationDate():params.getFromDate();
			SummaryProviderParameters clonedParams = params.clone();
			clonedParams.setFromDate(from);
			clonedParams.setAccountExpression(account.getCode());
			
			Summary summary = sp.getUniqueSummary(clonedParams);
			double initialDebit = summary.getInitialDebit();
			double initialCredit = summary.getInitialCredit();
			if ( CommonUtil.round(initialDebit - initialCredit, 2) != 0.0) {
				setPreviousBalance( new Balance() );
				getPreviousBalance().setAccount(summary.getCode());
				getPreviousBalance().setDescription(summary.getDescription());
				getPreviousBalance().setToDate(DateUtils.addDays(from, -1));
				getPreviousBalance().set(initialDebit,initialCredit);	
			}
			
			
			setPeriodBalance( new Balance() );
			getPeriodBalance().setAccount(summary.getCode());
			getPeriodBalance().setDescription(summary.getDescription());
			getPeriodBalance().setFromDate(from);
			getPeriodBalance().setToDate(params.getToDate());
			getPeriodBalance().set(summary.getDebit(),summary.getCredit());
			if (isPreviousBalancePresent()) {
				getPeriodBalance().dragBalance(getPreviousBalance());
			}
			
		} catch (CloneNotSupportedException e) {
			throw new ManagerBeanException(e.getMessage(),e);
		}
	}

	private Account getAccount() {
		return (Account) getTo();
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Collection getCollection() {
		return getDetail();
	}

	public boolean isPreviousBalancePresent() {
		return getPreviousBalance() != null;
	}
}
