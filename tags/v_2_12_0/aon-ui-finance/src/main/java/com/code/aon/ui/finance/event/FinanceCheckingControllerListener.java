package com.code.aon.ui.finance.event;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.finance.controller.FinanceCheckingController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;

/**
 * FinanceCheckingController listener. 
 * 
 * @author Consulting & Development.
 * @since 1.0
 *
 */
public class FinanceCheckingControllerListener extends ControllerAdapter{
	
	/**
	 * Adds the criteria the payment of the controller
	 * 
	 * @see com.code.aon.ui.form.event.ControllerAdapter#beforeModelInitialized(com.code.aon.ui.form.event.ControllerEvent)
	 */
	@Override
	public void beforeModelInitialized(ControllerEvent event) throws ControllerListenerException {
		FinanceCheckingController financeCheckingController = (FinanceCheckingController)event.getController();
		Criteria criteria;
		try {
			criteria = financeCheckingController.getCriteria();
			criteria.addEqualExpression(financeCheckingController.getManagerBean().getFieldName(IFinanceAlias.FINANCE_PAYMENT), financeCheckingController.getPayment());
			financeCheckingController.setCriteria(criteria);
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}
}
