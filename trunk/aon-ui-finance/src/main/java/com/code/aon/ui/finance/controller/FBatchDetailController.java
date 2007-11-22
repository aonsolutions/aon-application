package com.code.aon.ui.finance.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.FinanceTracking;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;

public class FBatchDetailController extends LinesController {

	private static final Logger LOGGER = Logger.getLogger(FBatchDetailController.class.getName());

	private static final String FINANCE_BATCH_CONTROLLER_NAME = "fbatch";

	private ArrayList<FinanceBatchDetail> checks = new ArrayList<FinanceBatchDetail>();

	public boolean getRowChecked() {
		FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
		return checks.contains(to);
	}

	public void setRowChecked(boolean rowChecked) {
		if (rowChecked) {
			FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
			if (!checks.contains(to)) {
				checks.add(to);
			}
		} else {
			FinanceBatchDetail to = (FinanceBatchDetail) model.getRowData();
			if (checks.contains(to)) {
				checks.remove(to);
			}
		}
	}

	public ArrayList<FinanceBatchDetail> getCheckedFinanceBatchDetails() {
		return checks;
	}

	public void clearCheckedFinanceBatchDetails() {
		checks = new ArrayList<FinanceBatchDetail>();
	}

	public void rowSelected(ValueChangeEvent event) {
		if (event.getNewValue() != null) {
			setRowChecked(((Boolean) event.getNewValue()).booleanValue());
		}
	}

	@SuppressWarnings({"unused","unchecked"})
	public void checkAll(ActionEvent event) throws ManagerBeanException{
		Iterator iter = this.getManagerBean().getList(this.getCriteria()).iterator();
		while(iter.hasNext()){
			FinanceBatchDetail detail = (FinanceBatchDetail)iter.next();
			if (!checks.contains( detail )) {
				checks.add( detail );
			}
		}
	}

	@SuppressWarnings("unused")
	public void checkNone(ActionEvent event) {
		clearCheckedFinanceBatchDetails();
	}

	@SuppressWarnings({"unused","unchecked"})
	public void onRemoveSelected(ActionEvent event) {
        Iterator iter = checks.iterator();
        while(iter.hasNext()){
        	removeFinanceBatchDetail((FinanceBatchDetail) iter.next());
        }
        clearCheckedFinanceBatchDetails();
        FBatchController fBatchController = (FBatchController)AonUtil.getController(FINANCE_BATCH_CONTROLLER_NAME); 
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		if (!FinanceBatchStatus.TODO.equals(fBatch.getFinanceBatchStatus())) {
			try {
				fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
				fBatchController.getManagerBean().update(fBatch);
			} catch (ManagerBeanException e) {
				LOGGER.log(Level.SEVERE, "Error updating FinanceBatch with id="+ fBatch.getId(), e);
			}
		}
        fBatchController.loadDetails(((FinanceBatch)fBatchController.getTo()));
		fBatchController.loadAvailableFinances(((FinanceBatch)fBatchController.getTo()).isPayment());
    }

	private void removeFinanceBatchDetail(FinanceBatchDetail fBatchDetail) {
		try {
			getManagerBean().remove(fBatchDetail);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error removing detail from the FinanceBatch with id ="+ fBatchDetail.getFinanceBatch().getId(), e);
		}
		updateRelatedInfo(fBatchDetail);
	}

	public void updateRelatedInfo(FinanceBatchDetail fBatchDetail) {
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			FinanceStatus financeStatus = (wasFinanceReturned(fBatchDetail.getFinance()))?FinanceStatus.RETURNED:FinanceStatus.PENDING;
			fBatchDetail.getFinance().setFinanceStatus(financeStatus);
			financeBean.update(fBatchDetail.getFinance());
			FinanceTrackingWriter.removeLastTrackingByType(fBatchDetail.getFinance(), FinanceTrackingType.BATCHED);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error updating finances in FinanceBatch with id="+ fBatchDetail.getFinanceBatch().getId(), e);
		}
	}

	public boolean wasFinanceReturned(Finance finance) {
		try {
			IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID),finance.getId());
			criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE),FinanceTrackingType.RETURNED);
			return (trackingBean.getCount(criteria) > 0);
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE,"Error obtaining FinanceTracking of Finance with id="+ finance.getId(), e);
		}
		return false;
	}
}