package com.code.aon.ui.accounting.controller;

import java.util.Collection;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.accounting.summary.Summary;
import com.code.aon.accounting.summary.SummaryCollection;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.accounting.summary.SummaryProviderParameters;
import com.code.aon.accounting.util.Balance;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.util.AccountingPeriodUtil;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;

public class TrialBalanceController implements ICollectionProvider {

	private static final String STATEMENT_CONTROLLER_NAME = "statement";
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	private SummaryProviderParameters parameters;
	private SummaryCollection summaryCollection;
	private DataModel model;

	public SummaryProviderParameters getParameters() {
		if (parameters == null) {
			SummaryProviderParameters p = new SummaryProviderParameters();
			try {
				p.setPeriod(AccountingPeriodUtil.getDefaultPeriod());
			} catch (ManagerBeanException e) {
				p.setPeriod(null);
			}
			p.setFromDate(null);
			p.setToDate(null);
			p.setDate(new Date());
			p.setAccountExpression(null);
			p.setLowerLevelVisible(false);
			p.setNoTouchedAccountVisible(false);
			p.setRowsPerPage(20);
			p.setAccountLevel(5);
			setParameters(p);
		}
		return parameters;
	}

	public void setParameters(SummaryProviderParameters parameters) {
		this.parameters = parameters;
	}


	public void onReset(ActionEvent event) {
		setParameters(null);
		setSummaryCollection(null);
	}
	public void onResetStatement(ActionEvent event) {
		onReset(event);
		getParameters().setAccountLevel(5);
	}
	
	public void onSearch(ActionEvent event) {
		try {
			if (getParameters().getPeriod() == null) {
				getParameters().setPeriod(new Period());
			}
			if (getParameters().getFromDate() == null && getParameters().getPeriod().getInitiationDate() != null) {
				getParameters().setFromDate(getParameters().getPeriod().getInitiationDate());
			}
			if (getParameters().getToDate() == null && getParameters().getPeriod().getDeadline() != null) {
				getParameters().setToDate(getParameters().getPeriod().getDeadline());
			}
			setSummaryCollection(null);
			setModel(new ListDataModel(getSummaryCollection().getSummaryList()));
			if (getParameters().getAccountLevel() == 5) {
				getParameters().setRowsPerPage(20);
			}
			if (getSummaryCollection().getSummaryList().size() == 1) {
				getModel().setRowIndex(0);
				onStatement(event);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public SummaryCollection getSummaryCollection() throws ManagerBeanException {
		if (summaryCollection == null) {
			SummaryProvider sp = new SummaryProvider();
			setSummaryCollection(sp.getSummaryCollection(getParameters()));
		}
		return summaryCollection;
	}

	public void setSummaryCollection(SummaryCollection summaryCollection) {
		this.summaryCollection = summaryCollection;
	}

	public DataModel getModel() {
		if (model == null) {
			model = new ListDataModel();
		}
		return model;
	}

	public void setModel(DataModel model) {
		this.model = model;
	}

	public void onStatement(ActionEvent event) {
		try {
			Summary summary = (Summary) getModel().getRowData();
			StatementController c = (StatementController) FormUtil
					.getController(STATEMENT_CONTROLLER_NAME);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			criteria.addExpression(alias, summary.getId() + "*");
			alias = c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			c.setParams(getParameters());
			c.onSearch(event);
			if (c.getModel().getRowCount() > 0) {
				c.getModel().setRowIndex(0);
				c.onSelect(event);
			} else {
				String msg = "No existen cuentas contables con el identiicador " + summary.getId()
						+ "*.";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		} catch (ExpressionException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void showAccountEntry(Balance balance) {
		try {
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IAccountingAlias.ACCOUNT_ENTRY_ID), balance.getAccountEntry());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onOpeningEntry(ActionEvent event) {
		StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
		Balance balance = c.getOpeningEntry();
		showAccountEntry(balance);
	}
	public void onAccountEntry(ActionEvent event) {
		StatementController c = (StatementController) AonUtil.getRegisteredBean(STATEMENT_CONTROLLER_NAME);
		Balance balance = (Balance) c.getDetailModel().getRowData();
		showAccountEntry(balance);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection() {
		try {
			return getSummaryCollection().getSummaryList();
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public Collection getCollection(boolean forceRefresh) throws ManagerBeanException {
		return getCollection();
	}
}
