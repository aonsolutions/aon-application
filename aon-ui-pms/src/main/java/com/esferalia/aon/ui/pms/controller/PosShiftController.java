package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosShiftController extends BasicController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCalculatorController calculator;
	private boolean cashCalculator;
	
	
	public CashCalculatorController getCalculator() {
		return calculator;
	}
	public void setCalculator(CashCalculatorController calculator) {
		this.calculator = calculator;
	}
	public boolean isCashCalculator() {
		return cashCalculator;
	}
	public void setCashCalculator(boolean cashCalculator) {
		this.cashCalculator = cashCalculator;
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
	
	public List<SelectItem> getPosList() throws ManagerBeanException {
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ (getHotel() == null ? " WHERE Pos.id is null " : " WHERE Pos.workplace = " + getHotel().getWorkPlace().getId()) 
			+ " GROUP BY Pos.name"
			+ " ORDER BY Pos.name"
			;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		List<SelectItem> list = new LinkedList<SelectItem>();
		for(Object to: sqlQuery.list()){
			Pos pos = (Pos) BeanManager.getManagerBean(Pos.class).get((Integer)(((Object[])to)[0]));
			SelectItem item = new SelectItem(pos, pos.getName());
			list.add(item);
		}
		return list;
	}
	
	public void onInit( ActionEvent event ){
		init();
	}
	
	public void init( ){
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean("cashCalculator");
		controller.init();
		setCalculator(controller);
		setHotel(null);
		setPosShift(new PosShift());
		getPosShift().setStartTime(new Date());
	}
	
	public void onShowCalculatorWindow( ActionEvent event ){
		setCashCalculator(false);
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(true);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onShowCashCalculatorWindow( ActionEvent event ){
		setCashCalculator(true);
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(false);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		IController controller = FormUtil.getController("posShiftCount");
		if( isCashCalculator() ){
			getCalculator().setCashAmount(getCalculator().getCalcTotal());
		} else if( controller.getTo() != null ){
			PosShiftCount c = (PosShiftCount) controller.getTo();
			c.setAmount(getCalculator().getCalcTotal());
		} else {
			controller = FormUtil.getController("posShift");
			PosShift c = (PosShift) controller.getTo();
			c.setInitialAmount(getCalculator().getCalcTotal());
		}
	}
	
	
}
