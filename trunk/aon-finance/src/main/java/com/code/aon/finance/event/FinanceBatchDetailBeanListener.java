package com.code.aon.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanListenerAdapter;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;

public class FinanceBatchDetailBeanListener extends ManagerBeanListenerAdapter {
	
	@Override
	public void beanInserted(ManagerBeanEvent evt) throws ManagerBeanException {
		FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)evt.getTo();
		Finance finance = fBatchDetail.getFinance();
        finance.setFinanceStatus(FinanceStatus.BATCHED);
		BeanManager.getManagerBean(Finance.class).update(finance);

		String message = "Remesa: " + fBatchDetail.getFinanceBatch().getId() + " - " + fBatchDetail.getFinanceBatch().getDescription();
		FinanceTrackingWriter.addFinanceTracking(finance, fBatchDetail.getFinanceBatch().getIssueDate(), FinanceTrackingType.BATCHED, message);
	}

	@Override
	public void beanRemoved(ManagerBeanEvent evt) throws ManagerBeanException {
		FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)evt.getTo();
		Finance finance = (Finance)BeanManager.getManagerBean(Finance.class).get(fBatchDetail.getFinance().getId());
		finance.setFinanceStatus((FinanceTrackingWriter.wasFinanceReturned(finance)) ? FinanceStatus.RETURNED : FinanceStatus.PENDING);
		finance = (Finance)BeanManager.getManagerBean(Finance.class).update(finance);
		
		FinanceTrackingWriter.removeLastTrackingByType(finance, FinanceTrackingType.BATCHED);
	}

}
