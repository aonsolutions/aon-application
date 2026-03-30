package com.code.aon.ui.accounting.controller.amortization;

import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
import com.code.aon.account.Account;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.Amortization;
import com.code.aon.accounting.AmortizationDetail;
import com.code.aon.accounting.Period;
import com.code.aon.accounting.amortization.AmortizationManager;
import com.code.aon.accounting.enumeration.AmortizationDetailStatus;
import com.code.aon.accounting.summary.SummaryProvider;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.accounting.IAccountingConstants;
import com.code.aon.ui.accounting.controller.entry.AccountEntryController;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class PeriodAmortizationController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Period period;
	private Date fromDate;
	private Date toDate;
	private boolean pendingStatus;
	private boolean scoredStatus;
	private boolean blockedStatus;
	private String description;
	private Account accumulatedAccount;
	private Account allocationAccount;
	private Account fixedAssetAccount;
	
	private double accumulated;
	private double pending;
	private double totalAmount;
	private double totalAccumulated;
	private double totalAllocation;
	private double totalPending;
	private SummaryProvider summaryProvider;
	
	private boolean initialized = false;
	
	@SuppressWarnings("unchecked")
	public List<AmortizationDetail> getAmortizationList() throws ManagerBeanException {
		return (List<AmortizationDetail>) getCalculatedModel().getWrappedData();
	}
	
	public SummaryProvider getSummaryProvider() {
		if (summaryProvider == null) {
			summaryProvider = new SummaryProvider();
		}
		return summaryProvider;
	}

	public Period getPeriod() {
		return period;
	}
	public void setPeriod(Period period) {
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

	public boolean isPendingStatus() {
		return pendingStatus;
	}

	public void setPendingStatus(boolean pendingStatus) {
		this.pendingStatus = pendingStatus;
	}

	public boolean isScoredStatus() {
		return scoredStatus;
	}

	public void setScoredStatus(boolean scoredStatus) {
		this.scoredStatus = scoredStatus;
	}

	public boolean isBlockedStatus() {
		return blockedStatus;
	}

	public void setBlockedStatus(boolean blockedStatus) {
		this.blockedStatus = blockedStatus;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	public Account getAccumulatedAccount() {
		return accumulatedAccount;
	}

	public void setAccumulatedAccount(Account accumulatedAccount) {
		this.accumulatedAccount = accumulatedAccount;
	}

	public Account getAllocationAccount() {
		return allocationAccount;
	}

	public void setAllocationAccount(Account allocationAccount) {
		this.allocationAccount = allocationAccount;
	}

	public Account getFixedAssetAccount() {
		if (fixedAssetAccount== null) fixedAssetAccount = new Account(); 
		return fixedAssetAccount;
	}

	public void setFixedAssetAccount(Account fixedAssetAccount) {
		this.fixedAssetAccount = fixedAssetAccount;
	}
	
	@Override
	public void onEditSearch(ActionEvent event) {
		super.onEditSearch(event);
		initialized = false;
	}	
	
	@Override
	public void onSearch(ActionEvent event) {
		try {
			clearCriteria();
			// ---------------------------------- FECHA
			String dateAlias = getFieldName(IEntityAlias.AMORTIZATION_DETAIL_TO_DATE);
			Date fromDate = getPeriod().getInitiationDate();
			if (getFromDate() != null ) {
				fromDate = getFromDate();
			}
			Date toDate = getPeriod().getDeadline();
			if (getToDate() != null ) {
				toDate = getToDate();
			}
			getCriteria().addGreaterThanOrEqualExpression(dateAlias, fromDate);
			getCriteria().addLessThanOrEqualExpression(dateAlias, toDate);
			
			// ---------------------------------- ESTADO
			String statusAlias = getFieldName(IEntityAlias.AMORTIZATION_DETAIL_STATUS);
			Expression expr1 = pendingStatus
					?ExpressionUtilities.getEqualExpression(statusAlias, AmortizationDetailStatus.PENDING)
					:null;
			Expression expr2 = scoredStatus
					?ExpressionUtilities.getEqualExpression(statusAlias, AmortizationDetailStatus.SCORED)
					:null;
			Expression expr3 = blockedStatus
					?ExpressionUtilities.getEqualExpression(statusAlias, AmortizationDetailStatus.BLOCKED)
					:null;
			Expression exp = null;
			if (expr1 != null) exp = expr1;
			if (expr2 != null) {
				exp = exp==null?expr2 : ExpressionUtilities.getOrExpression(exp, expr2);   
			}
			if (expr3 != null) {
				exp = exp==null?expr3 : ExpressionUtilities.getOrExpression(exp, expr3);   
			}
			if (exp != null) {
				getCriteria().addExpression(exp);
			}
			
			// ---------------------------------- DESCRIPTION
			if (StringUtils.isNotBlank(description)) {
				String desc =  (!StringUtils.contains(description,"*")) 
					?"*" +  description + "*"
					:description;
				getCriteria().addExpression(getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_DESCRIPTION), desc);
			}
			// ---------------------------------- Fixed Asset Account
			if (fixedAssetAccount != null && fixedAssetAccount.getId() != null) {
				getCriteria().addEqualExpression(getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_FIXED_ASSET_ACCOUNT_ID), fixedAssetAccount.getId());
			}
			
			super.onSearch(event);
		} catch (ManagerBeanException | ExpressionException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public DataModel getCalculatedModel() throws ManagerBeanException {
		if (!initialized) {
			DataModel model = super.getModel();
			
			setTotalAmount(0);
			setTotalAllocation(0);
			setTotalAccumulated(0);
			setTotalPending(0);
			
			for (int i = 0; i < model.getRowCount(); i++) {
				model.setRowIndex(i);
				AmortizationDetail detail = (AmortizationDetail) model.getRowData();
				Amortization a = detail.getAmortization();
				
				double accumulated = 0.0;
				Criteria c = new Criteria();
				c.addEqualExpression(getManagerBean().getFieldName(IEntityAlias.AMORTIZATION_DETAIL_AMORTIZATION_ID), a.getId());
				c.addLessThanExpression(getManagerBean().getFieldName(IEntityAlias.AMORTIZATION_DETAIL_FROM_DATE), period.getDeadline());
				ProjectionList pl = new ProjectionList();
				pl.add(Projection.sum(getManagerBean().getFieldName(IEntityAlias.AMORTIZATION_DETAIL_ALLOCATION)));
				List<?> list = getManagerBean().getList(pl, c);
				if (list != null && list.size() > 0 && list.get(0) != null) {
					accumulated = (Double) list.get(0);	
				}
				double pending = CommonUtil.round(a.getAmount() - accumulated);
				
				detail.setAccumulated(accumulated);
				detail.setPending(pending);
				
				setTotalAmount(CommonUtil.round(getTotalAmount()) + a.getAmount());
				setTotalAllocation(CommonUtil.round(getTotalAllocation()) + detail.getAllocation());
				setTotalAccumulated(CommonUtil.round(getTotalAccumulated()) + accumulated);
				setTotalPending(CommonUtil.round(getTotalPending()) + pending);
			}
			initialized = true;
		}
		return model;
	}

		
	public double getAccumulated() {
		return accumulated;
	}

	public void setAccumulated(double accumulated) {
		this.accumulated = accumulated;
	}

	public double getPending() {
		return pending;
	}

	public void setPending(double pending) {
		this.pending = pending;
	}

	public double getTotalAmount() {
		return totalAmount;
	}

	public void setTotalAmount(double totalAmount) {
		this.totalAmount = totalAmount;
	}

	public double getTotalAllocation() {
		return totalAllocation;
	}

	public void setTotalAllocation(double totalAllocation) {
		this.totalAllocation = totalAllocation;
	}

	public double getTotalAccumulated() {
		return totalAccumulated;
	}

	public void setTotalAccumulated(double totalAccumulated) {
		this.totalAccumulated = totalAccumulated;
	}

	public double getTotalPending() {
		return totalPending;
	}

	public void setTotalPending(double totalPending) {
		this.totalPending = totalPending;
	}

	public void onRecord(ActionEvent event) {
		try {
			checkList(AmortizationDetailStatus.SCORED);
			AmortizationManager am = new AmortizationManager();
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
				if (detail.isChecked()) {
					AccountEntry entry = am.recordAllocation(detail);
					detail.setAccountEntry(entry);
					detail.setStatus(AmortizationDetailStatus.SCORED);
					getManagerBean().update(detail);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	public void onUnrecord(ActionEvent event) {
		try {
			checkList(AmortizationDetailStatus.PENDING);
			AmortizationManager am = new AmortizationManager();
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
				if (detail.isChecked()) {
					Integer accountEntryId = detail.getAccountEntry().getId(); 
					detail.setAccountEntry(null);
					detail.setStatus(AmortizationDetailStatus.PENDING);
					getManagerBean().update(detail);
					am.unrecordAllocation(accountEntryId);
				}
			}
		} catch (ManagerBeanException e) {
			AonUtil.addErrorMessage(e.getMessage());
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private void checkList(AmortizationDetailStatus status) throws ManagerBeanException {
		boolean nocheck = true;
		for (int i = 0; i < getModel().getRowCount(); i++) {
			getModel().setRowIndex(i);
			AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
			if (detail.isChecked()) {
				nocheck = false;
				if (status == AmortizationDetailStatus.SCORED) {
					if (detail.getStatus() == AmortizationDetailStatus.SCORED) {
						String msg = "Existen cuotas seleccionadas ya contabilizadas";
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
				}
				if (status == AmortizationDetailStatus.PENDING) {
					if (detail.getStatus() == AmortizationDetailStatus.PENDING
							|| detail.getStatus() == AmortizationDetailStatus.BLOCKED) {
						String msg = "Existen cuotas seleccionadas sin contabilizar";
						AonUtil.addErrorMessage(msg);
						throw new AbortProcessingException(msg);
					}
				}
			}
		}
		if (nocheck) {
			String msg = "No se ha realizado ninguna selección.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onAccountEntry(ActionEvent event) {
		try {
			AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
			AccountEntryController entryController = (AccountEntryController) FormUtil
					.getController(IAccountingConstants.ACCOUNT_ENTRY_CONTROLLER_NAME);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(entryController.getManagerBean().getFieldName(
					IEntityAlias.ACCOUNT_ENTRY_ID), detail.getAccountEntry().getId());
			entryController.setCriteria(criteria);
			entryController.onSearch(null);
			entryController.getModel().setRowIndex(0);
			entryController.onSelect(null);
			entryController.setBackAction(IAccountingConstants.PERIOD_AMORTIZATION_LIST_NAVKEY);
		} catch (ManagerBeanException e) {
			String msg = "Error al cargar el apunte.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onCheckAll(ActionEvent event) {
		try {
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
				detail.setChecked(true);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al marcar la lista.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	public void onUncheckAll(ActionEvent event) {
		try {
			for (int i = 0; i < getModel().getRowCount(); i++) {
				getModel().setRowIndex(i);
				AmortizationDetail detail = (AmortizationDetail) getModel().getRowData();
				detail.setChecked(false);
			}
		} catch (ManagerBeanException e) {
			String msg = "Error al desmarcar la lista.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public PeriodAmortizationController getController() {
		return this;
	}
	
	public String getAccountDescription( String code ) {
		try {
			IManagerBean bean = BeanManager.getManagerBean(Account.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ACCOUNT_CODE), code );
			List<ITransferObject> list = bean.getList(criteria);
			if ( list != null && list.size() > 0 ) {
				Account account = (Account) list.get(0);
				return account.getDescription();
			}
		} catch (ManagerBeanException e) {
		}
		return "???????";
	}
	
	public void onPeriodChanged(ActionEvent event) {
		setFromDate(null);
		setToDate(null);
		if (getPeriod() != null) {
			setFromDate(getPeriod().getInitiationDate());	
			setToDate(getPeriod().getDeadline());
		}
	}
}
