package com.esferalia.aon.ui.pms.event;

import java.util.List;

import javax.faces.event.AbortProcessingException;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;
import com.esferalia.aon.ui.pms.controller.PosShiftController;

public class PosShiftControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanSelected(ControllerEvent event)
			throws ControllerListenerException {
		PosShiftController controller = (PosShiftController) this.getController();
		controller.setPosShift((PosShift) this.getController().getTo());
		try {
			if( getPosShiftCount()!=null ){
				controller.getCalculator().setCashAmount(getPosShiftCount().getAmount());
			} else {
				controller.getCalculator().setCashAmount(null);
			}
		} catch (ManagerBeanException e) {
			// TODO: handle exception
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
