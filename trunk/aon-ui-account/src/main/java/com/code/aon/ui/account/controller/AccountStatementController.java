package com.code.aon.ui.account.controller;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.account.AccountEntryDetail;
import com.code.aon.account.Period;
import com.code.aon.account.dao.IAccountAlias;
import com.code.aon.account.summary.Summary;
import com.code.aon.account.summary.SummaryCollection;
import com.code.aon.account.summary.SummaryProvider;
import com.code.aon.account.summary.SummaryProviderParameters;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;

public class AccountStatementController implements ICollectionProvider {

	private String period;
	private Date fromDate;
	private Date toDate;
	private Date date;
	private String account;
	private int level;
	private boolean lowerLevelVisible;
	private boolean zeroSumVisible;
	private int rowsPerPage;

	private static final String STATEMENT_ACCOUNT_CONTROLLER_NAME = "statementAccount";
	private static final String STATEMENT_ACCOUNT_DETAIL_CONTROLLER_NAME = "statementAccountDetail";
	private static final String ACCOUNT_ENTRY_CONTROLLER_NAME = "accountEntry";

	private SummaryProviderParameters summaryProviderParameters;
	private SummaryCollection summaryCollection;
	private DataModel model;

	public String getPeriod() {
		return period;
	}

	public void setPeriod(String period) {
		this.period = period;
	}

	public Date getFromDate() {
		return fromDate;
	}

	public void setFromDate(Date fromDate) {
		this.fromDate = fromDate;
	}

	public Date getToDate() {
		return toDate;
	}

	public void setToDate(Date toDate) {
		this.toDate = toDate;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public boolean isLowerLevelVisible() {
		return lowerLevelVisible;
	}

	public void setLowerLevelVisible(boolean lowerLevelVisible) {
		this.lowerLevelVisible = lowerLevelVisible;
	}

	public boolean isZeroSumVisible() {
		return zeroSumVisible;
	}

	public void setZeroSumVisible(boolean zeroSumVisible) {
		this.zeroSumVisible = zeroSumVisible;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void onReset(ActionEvent event) {
		setPeriod(null);
		setFromDate(null);
		setToDate(null);
		setDate(new Date());
		setAccount(null);
		setLowerLevelVisible(false);
		setZeroSumVisible(false);
		setRowsPerPage(20);
		setLevel(5);
	}

	public void onSearch(ActionEvent event) {
		try {
			setSummaryProviderParameters(new SummaryProviderParameters());
			summaryProviderParameters.setAccountExpression(getAccount());
			summaryProviderParameters.setAccountLevel(getLevel());
			summaryProviderParameters.setDate(getDate());
			Period period = null;
			if (getPeriod() != null) {
				IManagerBean periodBean = BeanManager.getManagerBean(Period.class);
				period = (Period) periodBean.get(getPeriod());
			} 
			if (period == null) {
				period = new Period();	
				period.setId(getPeriod());
			}
			if (getFromDate() == null && period.getInitiationDate() != null) {
				setFromDate(period.getInitiationDate());
			}
			if (getToDate() == null && period.getDeadline() != null) {
				setToDate(period.getDeadline());
			}
			summaryProviderParameters.setFromDate(getFromDate());
			summaryProviderParameters.setToDate(getToDate());
			summaryProviderParameters.setPeriod(period);
			summaryProviderParameters.setLowerLevelVisible(isLowerLevelVisible());
			summaryProviderParameters.setZeroSumVisible(isZeroSumVisible());
			setSummaryCollection(null);
			setModel(new ListDataModel(getSummaryCollection().getSummaryList()));
			if (getLevel() == 5) {
				setRowsPerPage(20);
			}
			if (getSummaryCollection().getSummaryList().size() == 1) {
				getModel().setRowIndex(0);
				onStatement(event);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public String getNextView() {
		try {
			if (getSummaryCollection().getSummaryList().size() == 1) {
				return "accStatement_list";
			} else {
				return "accStatement_account_list";
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public SummaryProviderParameters getSummaryProviderParameters() {
		return summaryProviderParameters;
	}

	public void setSummaryProviderParameters(SummaryProviderParameters summaryProviderParameters) {
		this.summaryProviderParameters = summaryProviderParameters;
	}

	public SummaryCollection getSummaryCollection() throws ManagerBeanException {
		try {
			if (summaryCollection == null) {
				SummaryProvider sp = new SummaryProvider();
				setSummaryCollection(sp.getSummaryCollection(getSummaryProviderParameters()));
			}
			return summaryCollection;
		} catch (ExpressionException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		} catch (SQLException e) {
			throw new ManagerBeanException(e.getMessage(), e);
		}
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

	public int getRowsPerPage() {
		return rowsPerPage;
	}

	public void setRowsPerPage(int rowsPerPage) {
		this.rowsPerPage = rowsPerPage;
	}

	public void onStatement(ActionEvent event) {
		try {
			Summary summary = (Summary) getModel().getRowData();
			StatementAccountController c = (StatementAccountController) AonUtil.getController(STATEMENT_ACCOUNT_CONTROLLER_NAME);
			c.onEditSearch(event);
			Criteria criteria = c.getCriteria();
			String alias = c.getFieldName(IAccountAlias.ACCOUNT_ID);
			criteria.addExpression(alias, summary.getId() + "*");
			alias = c.getFieldName(IAccountAlias.ACCOUNT_ENTRY_ENABLED);
			criteria.addExpression(ExpressionUtilities.getEqualExpression(alias, true));
			c.setParams(getSummaryProviderParameters());
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

	public void onAccountEntry(ActionEvent event) {
		try {
			IController c = AonUtil.getController(STATEMENT_ACCOUNT_DETAIL_CONTROLLER_NAME);
			AccountEntryDetail aed = (AccountEntryDetail) c.getModel().getRowData();
			AccountEntryController entryController = (AccountEntryController) AonUtil
					.getController(ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IAccountAlias.ACCOUNT_ENTRY_ID), aed.getAccountEntry().getId());
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
		return getCollection(false);
	}
}
