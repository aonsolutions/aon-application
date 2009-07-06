package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.finance.Finance;
import com.code.aon.finance.dao.IFinanceAlias;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ui.finance.controller.FinanceController;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;


public class FinanceControllerListener extends ControllerAdapter {

	
	@Override
	public void afterModelInitialized(ControllerEvent event)throws ControllerListenerException {
										
			try {	
				IManagerBean financeBean;	
				Criteria criteria =((FinanceController)getController()).getCriteria();
				financeBean = BeanManager.getManagerBean(Finance.class);
				Projection amountProjection = Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_AMOUNT));
				Projection expensesProjection = Projection.sum(financeBean.getFieldName(IFinanceAlias.FINANCE_EXPENSES));
				Object amount = financeBean.getUniqueResult(amountProjection,criteria);
				Object expenses = financeBean.getUniqueResult(expensesProjection,criteria);
				Double value=(Double)amount;
				Double value2=(Double)expenses;
				((FinanceController)getController()).setTotalFinanceAmount(value+value2);
				} catch (ManagerBeanException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				}		
			}
	}

