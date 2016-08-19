package com.code.aon.ui.finance.event;

import static com.code.aon.ui.common.ICommonMessages.FINANCE_BATCH_DATE_ERROR;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.AonVersion;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.finance.controller.FinanceListController;
import com.code.aon.ui.finance.controller.IFinanceConstants;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;

public class FBatchControllerListener extends ControllerAdapter implements IFinanceConstants {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(FBatchControllerListener.class.getName());
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatch.setPayment(fBatchController.isPayment());
		fBatch.setIssueDate(new Date());
		fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
		fBatch.setSecurityLevel(SecurityLevel.OFFICIAL);
	}
	
    @Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.setPayment(fBatch.isPayment());
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setAebOutput(null);
		try {
			fBatchController.loadAvailableFinances();
			fBatchController.loadAebFile();
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

        FBatchDetailController fBatchDetailController = (FBatchDetailController)FormUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER_NAME);
        fBatchDetailController.clearCheckedFinanceBatchDetails();
        FinanceListController financeController = (FinanceListController)FormUtil.getController(FINANCE_LIST_CONTROLLER_NAME);
        financeController.clearCheckedFinances();
    }

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setAebOutput(null);
		try {
			fBatchController.loadAvailableFinances();
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

    @Override
    public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        FBatchController fBatchController = (FBatchController)event.getController();
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
        if (fBatch.getFinanceBatchType().is19()) {
            try {
                Criteria criteria = new Criteria();
                criteria.addEqualExpression(fBatchController.getFieldName(IEntityAlias.FINANCE_BATCH_ID), fBatch.getId());
                Date oldDate = ((FinanceBatch)fBatchController.getManagerBean().getList(criteria).get(0)).getIssueDate();
                if (oldDate.after(fBatch.getIssueDate())) {
                    fBatch.setIssueDate(oldDate);
                    AonUtil.addErrorMessageFromBundle(FINANCE_BATCH_DATE_ERROR);
                }
            } catch (ManagerBeanException e) {
                LOGGER.error("Error obtaining FinanceBatch with id=" + fBatch.getId(), e);
            }
        }
        fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
        try {
        	fBatchController.cleanAebFile();
        } catch(ManagerBeanException e) {
        	throw new ControllerListenerException(e.getMessage(), e);
        }
    }

    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        FBatchController fBatchController = (FBatchController)event.getController();
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.setRecordDate(fBatch.getIssueDate());
		fBatchController.setAebOutput(null);
		try {
			fBatchController.loadAvailableFinances();
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
    }

}