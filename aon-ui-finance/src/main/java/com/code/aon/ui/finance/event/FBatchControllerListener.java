package com.code.aon.ui.finance.event;

import java.util.Date;
import java.util.Iterator;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.faces.context.FacesContext;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatch;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.finance.enumeration.FinanceBatchStatus;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.FinanceTrackingWriter;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.FBatchController;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;

public class FBatchControllerListener extends ControllerAdapter {
	
	private static final Logger LOGGER = Logger.getLogger(FBatchControllerListener.class.getName());
	
	private static final String FINANCE_BATCH_DETAIL_CONTROLLER = "fBatchDetail"; 
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FinanceBatch fBatch = (FinanceBatch)event.getController().getTo();
		fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
	}
	
	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.loadAvailableFinances(fBatch.isPayment());
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

                String bundleName = AonUtil.getConfigurationController().getApplicationBundles().get("financeBundle");
                ResourceBundle bundle = ResourceBundle.getBundle(bundleName, FacesContext.getCurrentInstance().getViewRoot().getLocale());
                throw new ControllerListenerException(bundle.getString("aon_finance_batch_date_error"));
            }
        } catch (ManagerBeanException e) {
            LOGGER.log(Level.SEVERE, "Error obtaining FinanceBatch with id=" + fBatch.getId(), e);
        }

        fBatch.setFinanceBatchStatus(FinanceBatchStatus.TODO);
    }

    @Override
    public void afterBeanUpdated(ControllerEvent event) throws ControllerListenerException {
        FBatchController fBatchController = (FBatchController)event.getController();
        FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
        fBatchController.loadAvailableFinances(fBatch.isPayment());
    }

    @Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		FBatchController fBatchController = (FBatchController)event.getController();
		FinanceBatch fBatch = (FinanceBatch)fBatchController.getTo();
		fBatchController.loadAvailableFinances(fBatch.isPayment());
	}

    @Override
	public void beforeBeanRemoved(ControllerEvent event) throws ControllerListenerException {
        FBatchDetailController fBatchDetailController = (FBatchDetailController)AonUtil.getController(FINANCE_BATCH_DETAIL_CONTROLLER);
        Iterator iter = fBatchDetailController.getWrappedList().iterator();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			while (iter.hasNext()) {
				FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)iter.next();
                FinanceStatus financeStatus = (fBatchDetailController.wasFinanceReturned(fBatchDetail.getFinance())) ? FinanceStatus.RETURNED: FinanceStatus.PENDING;
                fBatchDetail.getFinance().setFinanceStatus(financeStatus);
				financeBean.update(fBatchDetail.getFinance());

                FinanceTrackingWriter.removeLastTrackingByType(fBatchDetail.getFinance(), FinanceTrackingType.BATCHED);

            }
		} catch (ManagerBeanException e) {
			LOGGER.log(Level.SEVERE, "Error updating finances in FinanceBatch with id=" + ((FinanceBatch)event.getController().getTo()).getId(), e);
		}
	}

}