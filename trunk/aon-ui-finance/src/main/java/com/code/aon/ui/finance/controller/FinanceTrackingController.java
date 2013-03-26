package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.BankStatement;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceTrackingController extends LinesController implements IFinanceConstants {

	private AccountEntryFinanceWriter writer;
	private FinanceTracking currentTracking;

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public String getTrackingDescription() throws ManagerBeanException {
		String description = null;
		if (getModel().isRowAvailable()) {
			FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
			if (tracking.getType() == FinanceTrackingType.PAID || tracking.getType() == FinanceTrackingType.RETURNED) {
				description = obtainPaymentDescription(tracking);
			} else {
				description = tracking.getDescription();
			}
		}
		return description;
	}

	private String obtainPaymentDescription(FinanceTracking tracking) {
		if (tracking.getRegistryBank() != null) {
			return tracking.getRegistryBank().getFullName();
		} else if (tracking.getPayMethodTypeDetail() != null) {
			return tracking.getPayMethodTypeDetail().getDescription();
		} else {
			return AonUtil.getMessage(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_CASH);
		}
	}

	public void recordTracking(ActionEvent event) throws ManagerBeanException {
		FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
		getWriter().recordFinanceTracking(tracking);
	}

	public boolean isUndoable() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
			if (tracking.getBankStatementLink() != null && tracking.getBankStatementLink().getId() != null) {
				return false;
			}
			if (tracking.getType() == FinanceTrackingType.BATCHED || tracking.getType() == FinanceTrackingType.FRACTIONED) {
				return false;
			}
			if (tracking.getFinance().getFinanceGroup() != null && tracking.getFinance().getFinanceGroup().getId() != null) {
				return false;
			}

			if (FinanceTrackingWriter.isLastTracking(tracking)) {
				if (tracking.getType() == FinanceTrackingType.SETTLED || !tracking.isRecorded()) {
					return true;
				} else if (tracking.isRecorded() && AonUtil.getRoleManager().isAccountingOperator()) {
					IManagerBean entryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
					Criteria criteria = new Criteria();
					criteria.addEqualExpression(entryFinanceTrackingBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_ID), tracking.getId());
					Iterator<?> iterator = entryFinanceTrackingBean.getList(criteria).iterator();
					if (iterator.hasNext()) {
						AccountEntryFinanceTracking entryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
	
						IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
						AccountEntry entry = entryFinanceTracking.getAccountEntry();
						criteria = new Criteria();
						criteria.addEqualExpression(entryDetailBean.getFieldName(IEntityAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
						return (entryDetailBean.getCount(criteria) <= 2);
					}
				}
			}
		}
		return false;
	}

	public void undoTracking(ActionEvent event) throws ManagerBeanException {
		FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
		undoTracking(tracking);

		this.onSearch(null);
	}
	
	public void undoTracking(FinanceTracking tracking) throws ManagerBeanException {
		getWriter().removeAccountEntryFinanceTracking(tracking);
		updateFinanceStatus(tracking);
		getManagerBean().remove(tracking);
	}

	private void updateFinanceStatus(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		if (tracking.getType().equals(FinanceTrackingType.RETURNED)) {
			updateFinanceBatchDetailStatus(tracking);
			tracking.getFinance().setFinanceStatus(FinanceStatus.PAID);
		} else {
			tracking.getFinance().setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(tracking.getFinance())?FinanceStatus.RETURNED:FinanceStatus.PENDING));
		}
		Finance finance = (Finance)financeBean.update(tracking.getFinance());

		FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
		((Finance)financeController.getTo()).setFinanceStatus(finance.getFinanceStatus());
	}

	private void updateFinanceBatchDetailStatus(FinanceTracking tracking) throws ManagerBeanException {
		Finance finance = tracking.getFinance();
		IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
		criteria.addLessThanExpression(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_ID), tracking.getId());
		criteria.addOrder(trackingBean.getFieldName(IEntityAlias.FINANCE_TRACKING_ID), false);
		Iterator<?> iterator = trackingBean.getList(criteria).iterator();
		if (iterator.hasNext()) {
			iterator.next();
			if (iterator.hasNext()) {
				FinanceTracking batchedTracking = (FinanceTracking)iterator.next();
				if (batchedTracking.isBatched()) {
					IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
					criteria = new Criteria();
					criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
					criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
					criteria.addOrder(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_ID), false);
					for (ITransferObject ito : fBatchDetailBean.getList(criteria)) {
						FinanceBatchDetail detail = (FinanceBatchDetail)ito;
						detail.setStatus(FinanceStatus.PAID);
						fBatchDetailBean.update(detail);
						return;
					}
				}
			}
		}
	}

	public void onLoadFinanceBatch(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
			FinanceBatch fBatch = obtainFinanceBatch(tracking);
			if (fBatch != null) {
				FBatchController fBatchController = (FBatchController) AonUtil.getRegisteredBean(FINANCE_BATCH_CONTROLLER_NAME);
				fBatchController.setPayment(fBatch.isPayment());
				fBatchController.onLoad(event, fBatch.getId(), FINANCE_FORM_NAME, FINANCE_TRACKING_CONTROLLER_NAME + ".onBackTracking");
			} else {
				String msg =  "Error al seleccionar la remesa. ";
				AonUtil.addErrorMessage(msg);
				throw new AbortProcessingException(msg);	
			}
		}
	}

	private FinanceBatch obtainFinanceBatch(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IEntityAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), tracking.getFinance().getId());
		for (ITransferObject ito : fBatchDetailBean.getList(criteria)) {
			FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)ito;
			if (tracking.getDescription().indexOf(": " + fBatchDetail.getFinanceBatch().getId() + " - ") >= 0) {
				return fBatchDetail.getFinanceBatch();
			}
		}
		return null;
	}

	public void onLoadBankStatement(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			BankStatement statement = ((FinanceTracking)this.getModel().getRowData()).getBankStatementLink().getBankStatement();
			BankStatementController statementController = (BankStatementController) AonUtil.getRegisteredBean(BANK_STATEMENT_CONTROLLER_NAME);
			statementController.onLoad(event, statement, FINANCE_FORM_NAME, FINANCE_TRACKING_CONTROLLER_NAME + ".onBackTracking");
		}
	}
	
	public void onLoadFinanceGroup(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			currentTracking = (FinanceTracking)this.getModel().getRowData();
			FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
			financeController.onLoad(event, currentTracking.getFinance().getFinanceGroup().getId(), FINANCE_FORM_NAME, FINANCE_TRACKING_CONTROLLER_NAME + ".onBackGroupTracking");
		}
	}

	public void onBackTracking(ActionEvent event) throws ManagerBeanException {
		FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		financeController.refresh(event);

		onSearch(event);
	}
	
	public void onBackGroupTracking(ActionEvent event) throws ManagerBeanException {
		FinanceController financeController = (FinanceController) AonUtil.getRegisteredBean(FINANCE_CONTROLLER_NAME);
		financeController.select(event, currentTracking.getFinance());
		financeController.refresh(event);

		onSearch(event);
		currentTracking = null;
	}

}