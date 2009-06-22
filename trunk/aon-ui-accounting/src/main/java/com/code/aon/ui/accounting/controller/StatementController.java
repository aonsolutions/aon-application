package com.code.aon.ui.accounting.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;

public class StatementController extends BasicController {

	private static final String STATEMENT_DETAIL_CONTROLLER_NAME = "statementDetail";

	private AccountingUtil utils = new AccountingUtil();

	private Balance openingEntry;
	private Balance fromOpeningEntry;
	private Balance periodBalance;

	private List<Balance> detail;
	private DataModel detailModel;
	
	private String backAction;

	private SummaryProviderParameters params;

	public Balance getOpeningEntry() {
		return openingEntry;
	}

	public void setOpeningEntry(Balance openingEntry) {
		this.openingEntry = openingEntry;
	}

	public Balance getFromOpeningEntry() {
		return fromOpeningEntry;
	}

	public void setFromOpeningEntry(Balance fromOpeningEntry) {
		this.fromOpeningEntry = fromOpeningEntry;
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
	public void onSelect(ActionEvent event) {
		try {
			super.onSelect(event);
			initialize();
			initializeAmounts();
			transformDetailModel();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void initialize() {
		setOpeningEntry(null);
		setFromOpeningEntry(null);
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
		return detailModel;
	}

	public void setDetailModel(DataModel detailModel) {
		this.detailModel = detailModel;
	}

	private void transformDetailModel() throws ManagerBeanException {
		IController c = FormUtil.getController(STATEMENT_DETAIL_CONTROLLER_NAME);
		DataModel model = c.getModel();
		Balance previous = null;
		if (isOpeningEntryPresent()) {
			previous = getOpeningEntry();	
		}
		if (isFromOpeningEntryPresent()) {
			previous = getFromOpeningEntry();
		}
		setDetail(new LinkedList<Balance>());
		for (int i = 0; i < model.getRowCount(); i++) {
			model.setRowIndex(i);
			AccountEntryDetail d = (AccountEntryDetail) model.getRowData();
			Balance balance = new Balance();
			balance.setAccountEntry(d.getAccountEntry().getId());
			balance.setAccount(d.getAccount().getId());
			balance.setDescription(d.getAccount().getDescription());
			balance.setFromDate(d.getAccountEntry().getEntryDate());
			balance.setDebit(d.getDebit());
			balance.setCredit(d.getCredit());
			balance.setConcept(d.getConcept());
			balance.setBalancingAccount(d.getBalancingAccount() == null ? null : d
					.getBalancingAccount().getId());
			balance.setBalancingAccountDescription(d.getBalancingAccount() == null ? null : d
					.getBalancingAccount().getDescription());			
			if (previous != null) {
				balance.addBalance(previous);
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
		setDetailModel(new ListDataModel(getDetail()));
	}

	private void initializeAmounts() throws ManagerBeanException {
		Account account = getAccount();
		Date from = null;
		if (params.getFromDate() != null) {
			from = params.getFromDate();
			setOpeningEntry(utils.getOpeningEntryBalance(from, account.getId()));
		}
		
		if (isOpeningEntryPresent()) {
			if (!DateUtils.isSameDay(getOpeningEntry().getFromDate(), params.getFromDate())) {
				Date to = DateUtils.addDays(getParams().getFromDate(), -1);
				setFromOpeningEntry(utils.getPeriodBalance(getOpeningEntry().getFromDate(), to,
						account.getId(),true,true));
				getFromOpeningEntry().addBalance(getOpeningEntry());
			}
		} else {
			if (params.getPeriod() != null && params.getPeriod().getId() != null) {
				if (!DateUtils.isSameDay(from, params.getPeriod().getInitiationDate())) {
					from = params.getPeriod().getInitiationDate();
					Date to = DateUtils.addDays(getParams().getFromDate(), -1);
					setFromOpeningEntry(utils.getPeriodBalance(from, to, account.getId(),true,true));
				}
			} else {
//				setFromOpeningEntry(utils.getPeriodBalance(from, to, account.getId()));
			}
		}
		setPeriodBalance(utils.getPeriodBalance(params.getFromDate(), params.getToDate(), account
				.getId(),true,true));
		if (isFromOpeningEntryPresent()) {
			getPeriodBalance().addBalance(getFromOpeningEntry());
		}
	}

	private Account getAccount() {
		return (Account) getTo();
	}

	public boolean isOpeningEntryPresent() {
		return (getOpeningEntry() != null);
	}

	public boolean isFromOpeningEntryPresent() {
		return (getFromOpeningEntry() != null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		return getDetail();
	}
	
	
	public String getBackAction() {
		return backAction;
	}

	public void setBackAction(String backAction) {
		this.backAction = backAction;
	}

	public String backAction() {
		return getBackAction();
	}
}
