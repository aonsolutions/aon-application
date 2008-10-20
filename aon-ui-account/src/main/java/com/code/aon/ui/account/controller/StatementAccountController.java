package com.code.aon.ui.account.controller;

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
import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.summary.SummaryProviderParameters;
import com.code.aon.account.util.AccountUtils;
import com.code.aon.account.util.Balance;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class StatementAccountController extends BasicController {

	private static final String STATEMENT_ACCOUNT_DETAIL_CONTROLLER_NAME = "statementAccountDetail";

	private AccountUtils utils = new AccountUtils();

	private Balance openingEntry;
	private Balance fromOpeningEntry;
	private Balance periodBalance;

	private List<Balance> detail;
	private DataModel detailModel;

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
		IController c = AonUtil.getController(STATEMENT_ACCOUNT_DETAIL_CONTROLLER_NAME);
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
				double b = round(d.getDebit() - d.getCredit());
				if (b > 0) {
					balance.setUnpaidBalance(b);
				} else {
					balance.setCreditBalance(round(b * (-1)));
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

	private double round(double value) {
		double decimal = Math.pow(10, 2);
		return Math.round(decimal * value) / decimal;
	}
	
	
	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection() {
		return getDetail();
	}
}
