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
	private boolean showCalculatorWindow;
	private int[] amounts = new int[15];
	private Double cashAmount;
	
	public Double getCalcTotal() {
		Double calcTotal = 0.0;
		calcTotal += amounts[0]*0.01;
		calcTotal += amounts[1]*0.02;
		calcTotal += amounts[2]*0.05;
		calcTotal += amounts[3]*0.1;
		calcTotal += amounts[4]*0.2;
		calcTotal += amounts[5]*0.5;
		calcTotal += amounts[6]*1;
		calcTotal += amounts[7]*2;
		calcTotal += amounts[8]*5;
		calcTotal += amounts[9]*10;
		calcTotal += amounts[10]*20;
		calcTotal += amounts[11]*50;
		calcTotal += amounts[12]*100;
		calcTotal += amounts[13]*200;
		calcTotal += amounts[14]*500;
		return calcTotal;
	}
	public Double getCashAmount() {
		return cashAmount;
	}
	public void setCashAmount(Double cashAmount) {
		this.cashAmount = cashAmount;
	}
	public boolean isShowCalculatorWindow() {
		return showCalculatorWindow;
	}
	public void setShowCalculatorWindow(boolean showCalculatorWindow) {
		this.showCalculatorWindow = showCalculatorWindow;
	}
	public int[] getAmounts() {
		return amounts;
	}
	public void setAmounts(int[] amounts) {
		this.amounts = amounts;
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
		psc.setAmount(getCashAmount());
		bean.insert(psc);
	}
	private PayMethod getCashPayMethod() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		return bean.getList(criteria).isEmpty()?null:(PayMethod)bean.getList(criteria).get(0);
	}
	public void onShowCalculatorWindow( ActionEvent event ){
		amounts = new int[15];
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		IController controller = FormUtil.getController("posShiftCount");
		PosShiftCount c = (PosShiftCount) controller.getTo();
		if(c!=null){
			c.setAmount(getCalcTotal());
		}
		setCashAmount(getCalcTotal());
	}
	
			
}
