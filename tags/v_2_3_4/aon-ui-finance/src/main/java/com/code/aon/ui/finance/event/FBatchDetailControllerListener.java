package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.FBatchDetailController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

public class FBatchDetailControllerListener extends ControllerAdapter {
	
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		FBatchDetailController controller = (FBatchDetailController)event.getController();
		try {
			Criteria criteria = controller.getCriteria();
	        criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_DUE_DATE));
	        criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_INVOICE_SERIES));
	        criteria.addOrder(controller.getFieldName(IFinanceAlias.FINANCE_BATCH_DETAIL_FINANCE_INVOICE_NUMBER));
	        controller.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e);
		}
	}

}