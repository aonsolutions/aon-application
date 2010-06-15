package com.code.aon.ui.finance.event;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.IFinanceMessages;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class FBatchControllerListener extends ControllerAdapter implements IFinanceConstants {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FinanceBatch fBatch = (FinanceBatch)event.getController().getTo();
		fBatch.setPayment(false);
		fBatch.setIssueDate(new Date());
		fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.loadAvailableFinances(fBatch.isPayment());
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setCsbOutput(null);
	}

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        FBatchController fBatchController = (FBatchController)event.getController();
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
        try {
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(fBatchController.getFieldName(IFinanceAlias.FINANCE_BATCH_ID), fBatch.getId());
            Date oldDate = ((FinanceBatch)fBatchController.getManagerBean().getList(criteria).get(0)).getIssueDate();
            if (oldDate.after(fBatch.getIssueDate())) {
                fBatch.setIssueDate(oldDate);
                AonUtil.addErrorMessageFromBundle(IFinanceMessages.BUNDLE_KEY, IFinanceMessages.FINANCE_BATCH_DATE_ERROR);
            }
        } catch (ManagerBeanException e) {
            LOGGER.error("Error obtaining FinanceBatch with id=" + fBatch.getId(), e);
        }

        fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
    }

    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        FBatchController fBatchController = (FBatchController)event.getController();
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
        fBatchController.loadAvailableFinances(fBatch.isPayment());
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setCsbOutput(null);
    }

    @Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.loadAvailableFinances(fBatch.isPayment());
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setCsbOutput(null);

        FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.clearCheckedFinanceBatchDetails();
        FinanceController financeController = (FinanceController)FormUtil.getController(FINANCE_CONTROLLER_NAME);
        financeController.clearCheckedFinances();
    }

}