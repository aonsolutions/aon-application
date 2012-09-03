package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.faces.event.AbortProcessingException;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;
import com.esferalia.aon.ui.pms.controller.CashCalculatorController;
import com.esferalia.aon.ui.pms.controller.IPmsConstants;
import com.esferalia.aon.ui.pms.controller.PosShiftController;

public class PosShiftControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		CashCalculatorController cashCalculatorController = (CashCalculatorController) AonUtil.getRegisteredBean(IPmsConstants.CASH_CALCULATOR_CONTROLLER_NAME);
		PosShiftController controller = (PosShiftController) this.getController();
		controller.setCalculator(cashCalculatorController);
		controller.setPosShift((PosShift) controller.getTo());
		controller.setInvoiceFinancesModel(null);
		controller.setComparedCashModel(null);
		controller.initInvoiceData( );
		try {
			if( getPosShiftCount()!=null ){
				controller.getCalculator().setCashAmount(getPosShiftCount().getAmount());
			} else {
				controller.getCalculator().setCashAmount(null);
			}
		} catch (ManagerBeanException e) {
			String msg = ">>>>>>>>>>>> afterBeanSelected: Error al actualizar el importe de la calculadora.";
			AonUtil.addErrorMessage(msg);
			throw new ControllerListenerException(msg);
		}
	}
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		PosShiftController controller = (PosShiftController) this.getController();
		controller.setInvoiceFinancesModel(null);
	}
	
	@Override
	public void afterEditSearch(ControllerEvent event)
			throws ControllerListenerException {
		PosShiftSearchListener searchListener = (PosShiftSearchListener) AonUtil.getRegisteredBean("posShiftSearch");
		if(AonUtil.getRoleManager().isConfig() && !AonUtil.getRoleManager().isAdmin() && !AonUtil.getRoleManager().isSaleOperator()){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			searchListener.setStartTimeTo(DateUtils.addDays(cal.getTime(), -1));
			searchListener.setEndTimeTo(DateUtils.addDays(cal.getTime(), -1));
		}
	}
		
	@Override
	public void beforeBeanReset(ControllerEvent event)
			throws ControllerListenerException {
		((PosShiftController)this.getController()).init();
	}
	
	@Override
	public void beforeBeanCreated(ControllerEvent event)
			throws ControllerListenerException {
		((PosShiftController)this.getController()).init();
		((PosShift) getController().getTo()).setUser(UserUtils.getInstance().getLoggedUser());
	}
	
	private PayMethod getCashPayMethod() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		List<ITransferObject> list = bean.getList(criteria);
		PayMethod pm = list.isEmpty()?null:(PayMethod)list.get(0);
		if ( pm==null ) {
			String msg = "No existe la forma de pago 'EFECTIVO'";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		return pm;
	}
	
	private PosShiftCount getPosShiftCount() throws ManagerBeanException{
		PosShiftController controller = (PosShiftController) this.getController();
		IManagerBean bean = BeanManager.getManagerBean(PosShiftCount.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_POS_SHIFT_ID), ((PosShift)controller.getTo()).getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_COUNT_PAY_METHOD_ID), getCashPayMethod().getId());
		List<ITransferObject> list = bean.getList(criteria);
		return list.isEmpty()?null:(PosShiftCount)list.get(0);
			
	}
	
}
