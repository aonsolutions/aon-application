package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosClosingController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCalculatorController calculator;
	private boolean isNew;
	private List<SelectItem> openedPosList;
	
	public boolean isNew() {
		return isNew;
	}
	public void setNew(boolean isNew) {
		this.isNew = isNew;
	}
	public CashCalculatorController getCalculator() {
		return calculator;
	}
	public void setCalculator(CashCalculatorController calculator) {
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
	
	public List<SelectItem> getOpenedPosList() throws ManagerBeanException {
		if(openedPosList==null){
			openedPosList = new LinkedList<SelectItem>();
		}
		return openedPosList;
	}
	public void setOpenedPosList(List<SelectItem> openedPosList) {
		this.openedPosList = openedPosList;
	}
	
	private void buildOpenedPosList() throws ManagerBeanException {
		if(getHotel()!=null && getHotel().getId()!=null){
			Locale locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			Criteria criteria = new Criteria();
			criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME));
			criteria.addNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORK_PLACE_ID), getHotel().getWorkPlace().getId());
			for(ITransferObject to: bean.getList(criteria)){
				PosShift ps = (PosShift) to;
				SelectItem item = new SelectItem(ps, ps.getPos().getName()+", Apertura:"+ps.getStartTime()+", Turno:"+ps.getShift().getName(locale));
				getOpenedPosList().add(item);
			}
		}
	}
	
	
	public void onInit( ActionEvent event ){
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean("cashCalculator");
		controller.init();
		setCalculator(controller);
		setHotel(null);
		setPosShift(null);
		setOpenedPosList(null);
		setNew(true);
	}	
	
	public void onReset( ActionEvent event ){
		onInit(event);
	}
	
	public void onHotelChange( ActionEvent event ){
		try {
			setOpenedPosList(null);
			buildOpenedPosList();
		} catch (ManagerBeanException e) {
			// TODO: handle exception
		}
	}
	
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setEndTime(new Date());
			setPosShift((PosShift) bean.update(getPosShift()));
			PosShiftController controller = (PosShiftController) FormUtil.getController("posShift");
			controller.select(event, getPosShift());
			acceptCashAmount();
			setNew(false);
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
		getCalculator().setInitialAmount(false);
		getCalculator().setPosShift(getPosShift());
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
