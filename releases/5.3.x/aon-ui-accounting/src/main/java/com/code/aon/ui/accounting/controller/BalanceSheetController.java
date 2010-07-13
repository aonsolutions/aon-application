package com.code.aon.ui.accounting.controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.balance.BalanceItem;
import com.code.aon.accounting.balance.BalanceManager;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.util.AonUtil;

public class BalanceSheetController implements ICollectionProvider {

	private static final String TRIAL_BALANCE_CONTROLLER_NAME = "trialBalance";
	private static final String EMPTY = "";
	private static final String OPEN_BRACKET = "(";
	private static final String CLOSE_BRACKET = ")";
	private static final String PIPE = "|";
	private static final String ASTERISK = "*";
	private static final String COMMA = ",";

	private DataModel balanceModel;
	private SummaryProviderParameters parameters;
	private BalanceType balanceType;
	private Balance balance;
	private Boolean flagAccounts;
	private Period previousPeriod;

	private List<BalanceItem> list;

	public void onClosingBalance(ActionEvent event) {
		setBalanceType(BalanceType.CLOSING);
		onReset(event);
	}

	public void onOperatingBalance(ActionEvent event) {
		setBalanceType(BalanceType.OPERATING);
		onReset(event);
	}

	public void onPatrimonyBalance(ActionEvent event) {
		setBalanceType(BalanceType.PATRIMONY);
		onReset(event);
	}

	public void onCustomBalance(ActionEvent event) {
		setBalanceType(BalanceType.CUSTOM);
		onReset(event);
	}

	public void onReset(ActionEvent event) {
		list = null;
		balanceModel = null;
		previousPeriod = null;
		parameters = new SummaryProviderParameters();
		try {
			parameters.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
		} catch (ManagerBeanException e) {
			parameters.setPeriod(null);
		}
		parameters.setFromDate(null);
		parameters.setToDate(null);
		parameters.setDate(new Date());
		parameters.setAccountExpression(null);
		parameters.setLowerLevelVisible(false);
		parameters.setNoTouchedAccountVisible(false);
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(5);
		parameters.setBudgeted(false);
	}

	public void onBalance(ActionEvent event) {
		try {
			BalanceManager balanceManager = new BalanceManager();
			list = balanceManager.getBalanceCollection(getParameters(), getBalance());
		} catch (Throwable e) {
			String msg = "No se pudo realizar el Balance. " + e.getMessage();
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

	public DataModel getBalanceModel() {
		if (balanceModel == null) {
			balanceModel = new ListDataModel(list);
		}
		return balanceModel;
	}

	public void setBalanceModel(DataModel balanceModel) {
		this.balanceModel = balanceModel;
	}

	public Balance getBalance() {
		return balance;
	}

	public void setBalance(Balance balance) {
		this.balance = balance;
	}

	public SummaryProviderParameters getParameters() {
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}


	public Period getPreviousPeriod() {
		if (previousPeriod == null) {
			if (getParameters().getPeriod() != null && getParameters().getPeriod().getId() != null) {
				AccountingUtil au = new AccountingUtil();
				try {
					previousPeriod = au.getPreviousPeriod(getParameters().getPeriod());
				} catch (ManagerBeanException e) {
					previousPeriod = null;
				} 
			}
		}
		return previousPeriod;
	}
	public String getPreviousPeriodID() {
		return getPreviousPeriod()==null?"":getPreviousPeriod().getId();
	}

	public void setPreviousPeriod(Period previousPeriod) {
		this.previousPeriod = previousPeriod;
	}

	@Override
	public List<BalanceItem> getCollection() {
		return list;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}

	public BalanceType getBalanceType() {
		return balanceType;
	}

	public void setBalanceType(BalanceType balanceType) {
		this.balanceType = balanceType;
	}

	public Boolean getFlagAccounts() {
		return flagAccounts;
	}

	public void setFlagAccounts(Boolean flagAccounts) {
		this.flagAccounts = flagAccounts;
	}

	public List<SelectItem> getBalances() throws ManagerBeanException {
		AccountingCollectionsController c = (AccountingCollectionsController) AonUtil
				.getRegisteredBean("accountingCollections");
		return c.getBalances(balanceType);
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			BalanceItem item = (BalanceItem) getBalanceModel().getRowData();
			TrialBalanceController c = (TrialBalanceController) AonUtil
					.getRegisteredBean(TRIAL_BALANCE_CONTROLLER_NAME);
			c.onReset(event);
			SummaryProviderParameters spp = getParameters().clone();

			BalanceDetail bd = item.getDetail();
			String[] tokens = StringUtils.split(bd.getAccounts(), COMMA);
			StringBuilder exp = new StringBuilder();
			for (String token : tokens) {
				token = token.trim();
				if (StringUtils.isNotBlank(token)) {
					exp.append(exp.length() > 0 ? PIPE : EMPTY);
					if (token.startsWith(OPEN_BRACKET) && token.endsWith(CLOSE_BRACKET)) {
						token = token.replace(OPEN_BRACKET, EMPTY).replace(CLOSE_BRACKET, EMPTY);
					}
					exp.append(token);
					exp.append(ASTERISK);
				}
			}
			spp.setAccountExpression(exp.toString());
			if (spp.getFromDate() == null) {
				spp.setFromDate(spp.getPeriod().getInitiationDate());
			}
			if (spp.getToDate() == null) {
				spp.setToDate(spp.getPeriod().getDeadline());
			}
			c.onResetStatement(event);
			c.setParameters(spp);
			c.setBackAction("balance_sheet_list");
			c.onSearch(event);
			if (c.getModel().getRowCount() == 0) {
				String msg = "No existen cuentas contables para la cuenta.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (CloneNotSupportedException e) {
			String msg = "No se pudo realizar el acceso al extracto.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg, e);
		}
	}

}
