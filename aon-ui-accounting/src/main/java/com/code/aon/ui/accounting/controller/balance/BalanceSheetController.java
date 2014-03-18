package com.code.aon.ui.accounting.controller.balance;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.accounting.Balance;
import com.code.aon.accounting.BalanceDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.balance.BalanceItem;
import com.code.aon.accounting.balance.BalanceManager;
import com.code.aon.accounting.enumeration.BalanceType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.AccountingUtil;
import com.code.aon.AonVersion;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.AccountingCollectionsController;
import com.code.aon.ui.accounting.controller.report.TrialBalanceController;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.util.AonUtil;

@Deprecated
public class BalanceSheetController implements ICollectionProvider, Serializable {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

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

	public void onBack(ActionEvent event) {
		list = null;
		balanceModel = null;
		previousPeriod = null;
	}

	public void onReset(ActionEvent event) {
		onBack(event);
		parameters = new SummaryProviderParameters(AonUtil.getDomainName());
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
		if (balanceType == BalanceType.CLOSING) {
			parameters.setExcludeClosingEntry(true);	
		}
		if (balanceType == BalanceType.OPERATING) {
			parameters.setExcludeOperatingEntry(true);
			parameters.setExcludeClosingEntry(true);
		}
		parameters.setRowsPerPage(20);
		parameters.setAccountLevel(5);
		parameters.setPreviousPeriodVisible(true);
		parameters.setCounterVisible(false);
		parameters.setCoverVisible(false);
		if (!AonUtil.getRoleManager().isConfidentiality()) {
			parameters.setSecurityLevel(SecurityLevel.OFFICIAL);	
		}
	}

	public void onBalance(ActionEvent event) {
		try {
			setPreviousPeriod(null);
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
			balanceModel = new SerializableListDataModel(list);
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
	public String getPreviousPeriodName() {
		return getPreviousPeriod()==null?"":getPreviousPeriod().getName();
	}

	public void setPreviousPeriod(Period previousPeriod) {
		this.previousPeriod = previousPeriod;
	}

	@Override
	public List<BalanceItem> getCollection() {
		return list;
	}

	@Override
	public Collection<?> getCollection(boolean forceRefresh) throws ManagerBeanException {
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
				.getRegisteredBean( IAccountingConstants.ACCOUNTING_COLLECTIONS_CONTROLLER_NAME);
		return c.getBalances(balanceType);
	}

	public void onAccountStatement(ActionEvent event) {
		try {
			BalanceItem item = (BalanceItem) getBalanceModel().getRowData();
			TrialBalanceController c = (TrialBalanceController) AonUtil
					.getRegisteredBean(IAccountingConstants.TRIAL_BALANCE_CONTROLLER_NAME);
			c.onReset(event);
			c.onResetStatement(event);
			SummaryProviderParameters spp = getStatementParameters(item);
			c.setParameters(spp);
			c.onSearch(event);
			c.setBackAction(IAccountingConstants.BALANCE_SHEET_LIST_NAVKEY);
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
	public void onAccountPreviousStatement(ActionEvent event) {
		try {
			BalanceItem item = (BalanceItem) getBalanceModel().getRowData();
			TrialBalanceController c = (TrialBalanceController) AonUtil
					.getRegisteredBean(IAccountingConstants.TRIAL_BALANCE_CONTROLLER_NAME);
			c.onReset(event);
			c.onResetStatement(event);
			SummaryProviderParameters spp = getStatementParameters(item);
			spp.setPeriod(getPreviousPeriod());
			Date previousFrom;
			if (getParameters().getFromDate() != null) {
				previousFrom = DateUtils.addYears(getParameters().getFromDate(),-1); 
			} else {
				previousFrom =  getPreviousPeriod().getInitiationDate();	
			}
			spp.setFromDate(previousFrom);
			Date previousTo;
			if (getParameters().getToDate() != null) {
				previousTo = DateUtils.addYears(getParameters().getToDate(),-1); 
			} else {
				previousTo = getPreviousPeriod().getDeadline();
			}
			spp.setToDate(previousTo);
			c.setParameters(spp);
			c.onSearch(event);
			c.setBackAction(IAccountingConstants.BALANCE_SHEET_LIST_NAVKEY);
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

	private SummaryProviderParameters getStatementParameters(BalanceItem item) throws CloneNotSupportedException {
		SummaryProviderParameters spp = getParameters().clone();
		BalanceDetail bd = item.getDetail();
		String[] tokens = StringUtils.split(bd.getAccounts(), IAccountingConstants.COMMA);
		StringBuilder exp = new StringBuilder();
		for (String token : tokens) {
			token = token.trim(); 
			if (StringUtils.isNotBlank(token)) {
				exp.append(exp.length() > 0 ? IAccountingConstants.PIPE : IAccountingConstants.EMPTY);
				if (token.startsWith(IAccountingConstants.OPEN_BRACKET) && token.endsWith(IAccountingConstants.CLOSE_BRACKET)) {
					token = token.replace(IAccountingConstants.OPEN_BRACKET, IAccountingConstants.EMPTY).replace(IAccountingConstants.CLOSE_BRACKET, IAccountingConstants.EMPTY);
				}
				if (token.startsWith(IAccountingConstants.QUESTION_MARK)) {
					token = token.replace(IAccountingConstants.QUESTION_MARK, IAccountingConstants.EMPTY);
				}
				exp.append(token);
				exp.append(IAccountingConstants.ASTERISK);
			}
		}
		spp.setAccountExpression(exp.toString());
		if (spp.getFromDate() == null) {
			spp.setFromDate(spp.getPeriod().getInitiationDate());
		}
		if (spp.getToDate() == null) {
			spp.setToDate(spp.getPeriod().getDeadline());
		}
		spp.setExcludeBalancedAccounts(true);
		spp.setExcludeClosingEntry((balanceType == BalanceType.CLOSING));	
		spp.setExcludeOperatingEntry((balanceType == BalanceType.OPERATING));
		return spp;
	}

}
