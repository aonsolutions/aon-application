package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.account.bridge.writer.AccountEntryFinanceWriter;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class FinanceTrackingController extends LinesController implements IFinanceConstants {

	private AccountEntryFinanceWriter writer;

	public AccountEntryFinanceWriter getWriter() {
		if(writer == null){
			writer = new AccountEntryFinanceWriter();
		}
		return writer;
	}

	public boolean isUnrecordable() throws ManagerBeanException{
		if (getModel().isRowAvailable()) {
			FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
			if (FinanceTrackingWriter.isLastTracking(tracking)) {
				if (tracking.getType().equals(FinanceTrackingType.SETTLED)) {
					return true;
				}
				IManagerBean entryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
				Criteria criteria = new Criteria();
				criteria.addEqualExpression(entryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), tracking.getFinance().getId());
				criteria.addOrder(entryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ID), false);
				Iterator<?> iterator = entryFinanceTrackingBean.getList(criteria).iterator();
				if (iterator.hasNext()) {
					AccountEntryFinanceTracking entryFinanceTracking = (AccountEntryFinanceTracking)iterator.next();
					if (!entryFinanceTracking.getFinanceTracking().getId().equals(tracking.getId())) {
						return false;
					}

					IManagerBean entryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
					AccountEntry entry = entryFinanceTracking.getAccountEntry();
					criteria = new Criteria();
					criteria.addEqualExpression(entryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), entry.getId());
					return (entryDetailBean.getCount(criteria) <= 2);
				}
			}
		}
		return false;
	}

	public void undoTracking(ActionEvent event) throws ManagerBeanException {
		FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
		getWriter().removeAccountEntryFinanceTracking(tracking);
		updateFinanceStatus(tracking);

		getManagerBean().remove(tracking);
		this.onSearch(null);
	}

	private void updateFinanceStatus(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		if (tracking.getType().equals(FinanceTrackingType.RETURNED)) {
			updateFinanceBatchDetailStatus(tracking.getFinance());
			tracking.getFinance().setFinanceStatus(FinanceStatus.PAID);
		} else {
			tracking.getFinance().setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(tracking.getFinance())?FinanceStatus.RETURNED:FinanceStatus.PENDING));
		}
		Finance finance = (Finance)financeBean.update(tracking.getFinance());

		FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
		((Finance)financeController.getTo()).setFinanceStatus(finance.getFinanceStatus());
	}

	private void updateFinanceBatchDetailStatus(Finance finance) throws ManagerBeanException {
		if (FinanceTrackingWriter.getReturnedTimes(finance) == getBatchedTimes(finance)) {
			IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
			criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
			criteria.addOrder(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_BATCH_ISSUE_DATE), false);
			Iterator<?> iterator = fBatchDetailBean.getList(criteria).iterator();
			if (iterator.hasNext()) {
				FinanceBatchDetail detail = (FinanceBatchDetail)iterator.next();
				detail.setStatus(FinanceStatus.PAID);
				fBatchDetailBean.update(detail);
			}
		}
	}

	private int getBatchedTimes(Finance finance) throws ManagerBeanException {
		IManagerBean fBatchDetailBean = BeanManager.getManagerBean(FinanceBatchDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_ID), finance.getId());
		criteria.addEqualExpression(fBatchDetailBean.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_STATUS), FinanceStatus.RETURNED);
		return (fBatchDetailBean.getCount(criteria));
	}

}