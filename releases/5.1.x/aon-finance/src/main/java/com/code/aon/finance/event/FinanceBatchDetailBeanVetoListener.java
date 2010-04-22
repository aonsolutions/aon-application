package com.code.aon.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.event.ManagerBeanEvent;
import com.code.aon.common.event.ManagerBeanVetoListenerAdapter;
import com.code.aon.common.event.ManagerBeanVetoListenerException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.FinanceBatchDetail;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.finance.enumeration.FinanceTrackingType;
import com.code.aon.finance.invoicing.finance.FinanceTrackingWriter;

public class FinanceBatchDetailBeanVetoListener extends ManagerBeanVetoListenerAdapter {

	@Override
	public void vetoableBeanRemoved(ManagerBeanEvent evt) throws ManagerBeanVetoListenerException {
		FinanceBatchDetail fBatchDetail = (FinanceBatchDetail)evt.getTo();
		Finance finance = fBatchDetail.getFinance();
		try {
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);
			FinanceStatus financeStatus = (FinanceTrackingWriter.wasFinanceReturned(finance))?FinanceStatus.RETURNED:FinanceStatus.PENDING;
			finance.setFinanceStatus(financeStatus);
			financeBean.update(finance);

			FinanceTrackingWriter.removeLastTrackingByType(finance, FinanceTrackingType.BATCHED);
		} catch (ManagerBeanException e) {
			throw new ManagerBeanVetoListenerException(e.getMessage(), e);
		}
	}

}