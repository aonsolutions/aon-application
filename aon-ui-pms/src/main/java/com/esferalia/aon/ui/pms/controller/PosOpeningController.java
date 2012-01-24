package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.ui.config.util.UserUtils;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;


public class PosOpeningController {
	
	private PosShift posShift;
	private Hotel hotel;
	private boolean showCalculatorWindow;
	private int[] amounts = new int[15];
	
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
	
	public List<SelectItem> getClosedPos() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ " WHERE  (PosShift.end_time is not null"
			+ " OR PosShift.start_time is null)"
			+ (getHotel() == null ? "" : " AND Pos.workPlace = " + getHotel().getWorkPlace().getId()) 
			+ " GROUP BY Pos.name"
			+ " ORDER BY Pos.name"
			;
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
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
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setStartTime(new Date(getPosShift().getStartTime().getTime()));
			getPosShift().setUser(UserUtils.getInstance().getLoggedUser());
			bean.insertOrUpdate(getPosShift());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la apertura de caja";
			throw new AbortProcessingException(msg, e);
		}
	}
	public void onReset( ActionEvent event ){
		onInit(event);
	}
	
	public void onShowCalculatorWindow( ActionEvent event ){
		amounts = new int[15];
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		getPosShift().setInitialAmount(getCalcTotal());
	}
	
	
}
