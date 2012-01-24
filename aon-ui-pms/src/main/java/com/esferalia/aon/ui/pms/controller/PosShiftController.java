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
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosShiftController extends BasicController {
	
	private PosShift posShift;
	private Hotel hotel;
	private boolean showCalculatorWindow;
	private int[] amounts = new int[15];
	private Double cashAmount;
	private boolean cashCalculator;
	
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
	public boolean isCashCalculator() {
		return cashCalculator;
	}
	public void setCashCalculator(boolean cashCalculator) {
		this.cashCalculator = cashCalculator;
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
	public int[] getAmounts() {
		return amounts;
	}
	public void setAmounts(int[] amounts) {
		this.amounts = amounts;
	}
	
	public List<SelectItem> getPosList() throws ManagerBeanException {
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ (getHotel() == null ? "" : " WHERE Pos.workplace = " + getHotel().getWorkPlace().getId()) 
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
		setPosShift(new PosShift());
		getPosShift().setStartTime(new Date());
	}
	
	public void onShowCalculatorWindow( ActionEvent event ){
		setCashCalculator(false);
		amounts = new int[15];
	}
	
	public void onShowCashCalculatorWindow( ActionEvent event ){
		onShowCalculatorWindow( event );
		setCashCalculator(true);
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		IController controller = FormUtil.getController("posShiftCount");
		if( isCashCalculator() ){
			setCashAmount(getCalcTotal());
		} else if( controller.getTo() != null ){
			PosShiftCount c = (PosShiftCount) controller.getTo();
			c.setAmount(getCalcTotal());
		} else {
			controller = FormUtil.getController("posShift");
			PosShift c = (PosShift) controller.getTo();
			c.setInitialAmount(getCalcTotal());
		}
	}
	
	
}
