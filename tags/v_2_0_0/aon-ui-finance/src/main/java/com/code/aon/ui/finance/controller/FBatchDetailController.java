package com.code.aon.ui.finance.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.event.ActionEvent;

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
	
	@SuppressWarnings("unused")
	public void onRemoveSelected(ActionEvent event) {
        FinanceBatchDetail fBatchDetail = null;
        try {
            fBatchDetail = (FinanceBatchDetail)this.getModel().getRowData();
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining FinanceBatch details", e);
        }

        if (fBatchDetail != null) {
            try {
                getManagerBean().remove(fBatchDetail);
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error removing detail from the FinanceBatch with id =" + fBatchDetail.getFinanceBatch().getId(), e);
            }

            try {
                IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
                FinanceStatus financeStatus = (wasFinanceReturned(fBatchDetail.getFinance())) ? FinanceStatus.RETURNED: FinanceStatus.PENDING;
                fBatchDetail.getFinance().setFinanceStatus(financeStatus);
                financeBean.update(fBatchDetail.getFinance());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error updating finances in FinanceBatch with id=" + fBatchDetail.getFinanceBatch().getId(), e);
            }
            FinanceTrackingWriter.removeLastTrackingByType(fBatchDetail.getFinance(), FinanceTrackingType.BATCHED);
        }

        FBatchController fBatchController = (FBatchController)AonUtil.getController(FINANCE_BATCH_CONTROLLER_NAME);
        if (!FinanceBatchStatus.TODO.equals(fBatchDetail.getFinanceBatch().getFinanceBatchStatus())) {
            try {
                ((FinanceBatch)fBatchController.getTo()).setFinanceBatchStatus(FinanceBatchStatus.TODO);
                fBatchController.getManagerBean().update(fBatchController.getTo());
            } catch (ManagerBeanException e) {
                LOGGER.log(Level.SEVERE, "Error updating FinanceBatch with id=" + fBatchDetail.getFinanceBatch().getId(), e);
            }
        }
        fBatchController.loadDetails(fBatchDetail.getFinanceBatch());
        fBatchController.loadAvailableFinances(fBatchDetail.getFinanceBatch().isPayment());
    }

    public boolean wasFinanceReturned(Finance finance) {
        try {
            IManagerBean trackingBean = BeanManager.getManagerBean(FinanceTracking.class);
            Criteria criteria = new Criteria();
            criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_FINANCE_ID), finance.getId());
            criteria.addEqualExpression(trackingBean.getFieldName(IFinanceAlias.FINANCE_TRACKING_TYPE), FinanceTrackingType.RETURNED);
            return (trackingBean.getList(criteria).size() > 0);
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining FinanceTracking of Finance with id=" + finance.getId(), e);
        }
        return false;
    }

}
