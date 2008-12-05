package com.code.aon.ui.finance.controller;

import java.util.Iterator;

import javax.faces.event.ActionEvent;

import com.code.aon.account.bridge.AccountEntryFinanceTracking;
import com.code.aon.account.bridge.dao.IAccountBridgeAlias;
import com.code.aon.accounting.AccountEntry;
import com.code.aon.accounting.AccountEntryDetail;
import com.code.aon.accounting.dao.IAccountingAlias;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.LinesController;

public class FinanceTrackingController extends LinesController {

	private static final String FINANCE_CONTROLLER_NAME = "finance";
	
	public boolean isUnrecordable() throws ManagerBeanException{
		if ( getModel().isRowAvailable() ) {
			FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
			IManagerBean accEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(accEntryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_FINANCE_ID), tracking.getFinance().getId());
			criteria.addOrder(accEntryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_ID),false);
			Iterator<?> iter = accEntryFinanceTrackingBean.getList(criteria).iterator();
			if(iter.hasNext()){
				AccountEntryFinanceTracking accEntryFinanceTracking = (AccountEntryFinanceTracking)iter.next();
				return accEntryFinanceTracking.getFinanceTracking().getId().equals(tracking.getId());
			}
		}
		return false;
	}
	
	public void undoTracking(ActionEvent event) throws ManagerBeanException{
		FinanceTracking tracking = (FinanceTracking)this.getModel().getRowData();
		AccountEntryFinanceTracking accFinanceTracking = deleteAccEntryFinanceTracking(tracking);
		deleteAccountEntry(accFinanceTracking.getAccountEntry());
		updateFinanceStatus(tracking);
		IManagerBean financeTrackingBean = BeanManager.getManagerBean(FinanceTracking.class);
		financeTrackingBean.remove(tracking);
		this.onSearch(null);
	}

	private AccountEntryFinanceTracking deleteAccEntryFinanceTracking(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean accEntryFinanceTrackingBean = BeanManager.getManagerBean(AccountEntryFinanceTracking.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accEntryFinanceTrackingBean.getFieldName(IAccountBridgeAlias.ACCOUNT_ENTRY_FINANCE_TRACKING_FINANCE_TRACKING_ID), tracking.getId());
		Iterator<?> iter = accEntryFinanceTrackingBean.getList(criteria).iterator();
		if(iter.hasNext()){
			AccountEntryFinanceTracking accEntryFinanceTracking = (AccountEntryFinanceTracking)iter.next();
			accEntryFinanceTrackingBean.remove(accEntryFinanceTracking);
			return accEntryFinanceTracking;
		}
		return null;
	}
	
	private void deleteAccountEntry(AccountEntry accountEntry) throws ManagerBeanException {
		IManagerBean accountEntryDetailBean = BeanManager.getManagerBean(AccountEntryDetail.class);
		IManagerBean accountEntryBean = BeanManager.getManagerBean(AccountEntry.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(accountEntryDetailBean.getFieldName(IAccountingAlias.ACCOUNT_ENTRY_DETAIL_ACCOUNT_ENTRY_ID), accountEntry.getId());
		Iterator<?> iter = accountEntryDetailBean.getList(criteria).iterator();
		while(iter.hasNext()){
			AccountEntryDetail detail = (AccountEntryDetail)iter.next();
			accountEntryDetailBean.remove(detail);
		}
		accountEntryBean.remove(accountEntry);
	}

	private void updateFinanceStatus(FinanceTracking tracking) throws ManagerBeanException {
		IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
		if(tracking.getType().equals(FinanceTrackingType.RETURNED)){
			tracking.getFinance().setFinanceStatus(FinanceStatus.PAID);
		}else {
			tracking.getFinance().setFinanceStatus((wasFinanceReturned(tracking.getFinance())?FinanceStatus.RETURNED:FinanceStatus.PENDING));
		}
		FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
		Finance finance = (Finance)financeBean.update(tracking.getFinance());
		((Finance)financeController.getTo()).setFinanceStatus(finance.getFinanceStatus());
	}
	
	private boolean wasFinanceReturned(Finance finance) throws ManagerBeanException {
        IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
        Criteria criteria = new Criteria();
        criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
        criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
        return (trackingBean.getList(criteria).size() > 0);
    }
}