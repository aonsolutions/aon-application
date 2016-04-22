package com.code.aon.ui.accounting.controller.report;

import java.util.Collection;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.enumeration.AccountEntryType;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.occam.api.ACCOUNTING;
import com.esferalia.aon.occam.api.model.AccountStatement;
import com.esferalia.aon.occam.api.model.AccountStatementParams;
import com.esferalia.aon.occam.api.model.AccountStatementReport;

public class StatementController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Balance previousBalance;
	private Balance periodBalance;

	private List<AccountStatement> detail;
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
			initialize();
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

	public List<AccountStatement> getDetail() {
		return detail;
	}

	public void setDetail(List<AccountStatement> details) {
		this.detail = details;
	}

	public DataModel getDetailModel() {
		return getDetailState().getDirectModel();
	}

	public void setDetailModel(DataModel detailModel) {
		setDetailState(new DataScrollerState(detailModel, "statement"));
	}
	
	public DataScrollerState getDetailState() {
		return detailState;
	}

	public void setDetailState(DataScrollerState detailState) {
		this.detailState = detailState;
	}

	private void transformDetailModel() throws ManagerBeanException {
		String domainName = AonUtil.getDomainName();
		Integer domainId = DomainManager.getCurrentDomain();
		String user = AonUtil.getAuthPrincipal().getShortName();
		AccountStatementParams params = new AccountStatementParams();
		SecurityLevel se = getParams().getSecurityLevel();
		if (se != null) {
			com.esferalia.aon.occam.api.model.type.SecurityLevel securityLevel =
					com.esferalia.aon.occam.api.model.type.SecurityLevel.values()[se.ordinal()];
			params.setSecurityLevel( securityLevel );
		}
		params.setAccount(getAccount().getId());
		params.setFromDate(getParams().getFromDate());
		params.setToDate(getParams().getToDate());
		if (getParams().getPeriod() != null && getParams().getPeriod().getId() != null) {
			//params.setOpeningEntriesExcluded(getParams().isExcludeOpeningEntry() );
			params.setOperatingEntriesExcluded(getParams().isExcludeOperatingEntry() );
			params.setClosingEntriesExcluded(getParams().isExcludeClosingEntry() );
		}
		if (getParams().isNotEmptyDocumentNumber()) {
			params.setDocumentNumber(getParams().getDocumentNumber() );
		}
		AccountStatementReport asr = ACCOUNTING.getAccountStatement(domainName, domainId, user, params);
		for (AccountStatement as : asr.getSummary()) {
			if (as.getType() == 0) {
				Balance balance = new Balance();
				balance.setFromDate(null);
				balance.setToDate(asr.getFrom());
				balance.setUnpaidBalance(as.getDebitBalance());
				balance.setCreditBalance(as.getUnpaidBalance());
				setPreviousBalance(balance);
			} else if (as.getType() == 1) {
				Balance balance = new Balance();
				balance.setFromDate(asr.getFrom());
				balance.setToDate(asr.getTo());
				balance.setUnpaidBalance(as.getDebitBalance());
				balance.setCreditBalance(as.getUnpaidBalance());
				setPeriodBalance(balance);
			}
		}
		setDetail(asr.getDetails());
		setDetailModel(new SerializableListDataModel(getDetail()));
	}

	public Account getAccount() {
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
	
	public void onAccountEntry(ActionEvent event) {
		StatementController c = (StatementController) AonUtil.getRegisteredBean(IAccountingConstants.STATEMENT_CONTROLLER_NAME);
		AccountStatement balance = (AccountStatement) c.getDetailModel().getRowData();
		showAccountEntry(balance,null);
	}
	
	private void showAccountEntry(AccountStatement balance,AccountEntryType type) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			if (balance.getAccountEntry() != null) {
				criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ID), balance.getAccountEntry());
			} else {
				criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_ENTRY_DATE), balance.getEntryDate());
			}
			if (type != null) {
				criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_TYPE), type);
			}
			if (getParams().getSecurityLevel() != null) {
				criteria.addEqualExpression(entryController.getManagerBean().getFieldName(IEntityAlias.ACCOUNT_ENTRY_SECURITY_LEVEL), getParams().getSecurityLevel());
			}
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(IAccountingConstants.ACCOUNT_STMT_LIST_NAVKEY);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	
}
