package com.code.aon.ui.finance.event;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.SecurityLevel;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.finance.Finance;
import com.code.aon.finance.enumeration.FinanceStatus;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.Projection;
import com.code.aon.ql.ProjectionList;
import com.code.aon.ql.ast.Expression;
import com.code.aon.ql.util.ExpressionUtilities;
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
			Criteria criteria = new Criteria();
			String idAlias = financeBean.getFieldName(IEntityAlias.FINANCE_ID);
			ProjectionList idPL = new ProjectionList(Projection.property(idAlias));
			Expression exp = ExpressionUtilities.getSubQueryExpression(Finance.class, controller.getCriteria(), idPL);
			criteria.addInExpression(idAlias, exp);
			Projection amountProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_AMOUNT));
			Projection expensesProjection = Projection.sum(financeBean.getFieldName(IEntityAlias.FINANCE_EXPENSES));
			ProjectionList pl = new ProjectionList(amountProjection, expensesProjection);
			Object[] result = (Object[]) financeBean.getUniqueResult(pl, criteria);
			Double amount = CommonUtil.round(result[0]==null?0:(Double)result[0]);
			Double expenses = result[1]==null?0:(Double) result[1];
			controller.setTotalFinanceAmount(amount + expenses);
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
