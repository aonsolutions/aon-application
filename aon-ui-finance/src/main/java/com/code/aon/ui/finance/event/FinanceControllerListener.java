package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Projection;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.entity.IEntityAlias;

public class FinanceControllerListener extends ControllerAdapter {

	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		try {	
			IManagerBean financeBean = BeanManager.getManagerBean(Finance.class);	
			Projection amountProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
			Projection expensesProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_EXPENSES));
			Double amount = (Double)financeBean.getUniqueResult(amountProjection, controller.getCriteria());
			Double expenses = (Double)financeBean.getUniqueResult(expensesProjection, controller.getCriteria());
			controller.setTotalFinanceAmount(CommonUtil.round((amount==null?0:amount) + (expenses==null?0:expenses)));
		} catch (ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}		
	}

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		FinanceController controller = (FinanceController)event.getController();
		controller.setPurchase(false);
		Finance finance = (Finance)controller.getTo();
		finance.setPayment(controller.isPayment());
		finance.setFinanceStatus(FinanceStatus.PENDING);
		finance.setSecurityLevel(SecurityLevel.OFFICIAL);
	}

}
