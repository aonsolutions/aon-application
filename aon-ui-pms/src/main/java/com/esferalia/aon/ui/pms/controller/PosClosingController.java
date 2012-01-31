package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosClosingController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCountCalculator calculator;
	
	public CashCountCalculator getCalculator() {
		if(calculator == null){
			calculator = new CashCountCalculator();
		}
		return calculator;
	}
	public void setCalculator(CashCountCalculator calculator) {
		this.calculator = calculator;
	}
	public PosShift getPosShift() {
		return posShift;
	}
	public void setPosShift(PosShift posShift) {
		this.posShift = posShift;
	}
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	public List<SelectItem> getOpenedPos() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME));
		criteria.addNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
		if(getHotel()!=null && getHotel().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());
		}
		for(ITransferObject to: bean.getList(criteria)){
			PosShift ps = (PosShift) to;
			SelectItem item = new SelectItem(ps, ps.getPos().getName()+", Apertura:"+ps.getStartTime()+", Turno:"+ps.getShift());
			list.add(item);
		}
		return list;
	}
	
	public void onInit( ActionEvent event ){
		setPosShift(null);
	}	
	
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setEndTime(new Date());
			setPosShift((PosShift) bean.update(getPosShift()));
			PosShiftController controller = (PosShiftController) FormUtil.getController("posShift");
			controller.select(event, getPosShift());
			acceptCashAmount();
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el cierre de caja";
			throw new AbortProcessingException(msg, e);
		}
	}	
	
	private void acceptCashAmount() throws ManagerBeanException {
		if (getCashPayMethod()==null) {
			String msg = "No existe la forma de pago 'EFECTIVO'";
			throw new AbortProcessingException(msg);
		}
		IManagerBean bean = BeanManager.getManagerBean(PosShiftCount.class);
		PosShiftCount psc = new PosShiftCount();
		psc.setDomain(getPosShift().getDomain());
		psc.setPosShift(getPosShift());
		psc.setPayMethod(getCashPayMethod());
		psc.setAmount(getCalculator().getCashAmount());
		bean.insert(psc);
	}
	private PayMethod getCashPayMethod() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		return bean.getList(criteria).isEmpty()?null:(PayMethod)bean.getList(criteria).get(0);
	}
	public void onShowCalculatorWindow( ActionEvent event ){
		getCalculator().setAmounts( new int[15] );
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		IController controller = FormUtil.getController("posShiftCount");
		PosShiftCount c = (PosShiftCount) controller.getTo();
		if(c!=null){
			c.setAmount(getCalculator().getCalcTotal());
		}
		getCalculator().setCashAmount(getCalculator().getCalcTotal());
	}
	
			
}
