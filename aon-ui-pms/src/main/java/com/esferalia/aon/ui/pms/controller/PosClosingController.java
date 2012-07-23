package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
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
import com.esferalia.aon.pms.invoicing.PosInvoicing;


public class PosClosingController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCalculatorController calculator;
	private List<SelectItem> openedPosList;

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
	
	public Double getTotalCashAmount() {
		if(getPosShift()!=null && getPosShift().getId()!=null){
			String sqlSelect;
			try {
				sqlSelect = "SELECT sum(amount)"
						+ " FROM pos_shift_count"
						+ " WHERE pos_shift = " + getPosShift().getId()
						+ getCashPayMethodClause()
						+ " GROUP BY pos_shift";
			} catch (ManagerBeanException e) {
				return null;
			}
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			Query sqlQuery = session.createSQLQuery(sqlSelect);
			return (Double) sqlQuery.uniqueResult();
		}
		return null;
	}
	
	private String getCashPayMethodClause() throws ManagerBeanException {
		String clause = "";
		IManagerBean bean = BeanManager.getManagerBean(PayMethod.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PAY_METHOD_TYPE), PayMethodType.CASH_BASIS);
		for(ITransferObject to: bean.getList(criteria)){
			PayMethod pm = (PayMethod) to;
			clause += (clause.isEmpty()?" AND (":" OR ") + " pay_method = " + pm.getId();
		}
		clause += " )";
		return clause;
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
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean(IPmsConstants.CASH_CALCULATOR_CONTROLLER_NAME);
		controller.init();
		setCalculator(controller);
		setHotel(null);
		setPosShift(null);
		setOpenedPosList(null);
	}	
	
	public void onReset( ActionEvent event ){
		onInit(event);
	}
	
	public void onHotelChange( ActionEvent event ){
		try {
			setPosShift(null);
			setOpenedPosList(null);
			buildOpenedPosList();
		} catch (ManagerBeanException e) {
			String msg = "Error al buscar los POS";
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onPosChange( ActionEvent event ){
		selectPosShift( event );
	}
	
	public void selectPosShift( ActionEvent event ){
		try {
			PosShiftController controller = (PosShiftController) FormUtil.getController(IPmsConstants.POS_SHIFT_CONTROLLER_NAME);
			controller.load(event, getPosShift().getId());
		} catch (ManagerBeanException e) {
			String msg = "Error al seleccionar el turno";
			throw new AbortProcessingException(msg, e);
		}
	}
	
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setEndTime(new Date());
			PosShift ps = (PosShift) bean.update(getPosShift());
			setPosShift((PosShift) bean.get(ps.getId()));
			if(ps.getPos().isInvoiceable()){
				PosInvoicing posInvoicing = new PosInvoicing();
				try {
					posInvoicing.completeInvoice(ps);
				} catch (Exception e) {
					String msg = "Error al completar la factura de caja";
					AonUtil.addErrorMessage(msg +"("+ e.getMessage()+")");
					getPosShift().setEndTime(null);
					bean.update(getPosShift());
					throw new AbortProcessingException(msg, e);
				}
			}
			selectPosShift( event );
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el cierre de caja";
			AonUtil.addErrorMessage(msg +"("+ e.getMessage()+")");
			throw new AbortProcessingException(msg, e);
		}
	}	
	
	public void onCancel( ActionEvent event ){
		setPosShift(null);
		setHotel(null);
	}	
	
	public void onShowCalculatorWindow( ActionEvent event ){
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(false);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		IController controller = FormUtil.getController(IPmsConstants.POS_SHIFT_COUNT_CONTROLLER_NAME);
		PosShiftCount c = (PosShiftCount) controller.getTo();
		if(c!=null){
			c.setAmount(getCalculator().getCalcTotal());
		}
		getCalculator().setCashAmount(getCalculator().getCalcTotal());
	}
	
			
}
