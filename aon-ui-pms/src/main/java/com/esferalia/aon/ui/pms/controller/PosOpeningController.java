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
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.invoicing.PosInvoicing;


public class PosOpeningController {
	
	private PosShift posShift;
	private Hotel hotel;
	private CashCalculatorController calculator;
	private List<SelectItem> closedPosList;
	
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
	
	public List<SelectItem> getClosedPosList() throws ManagerBeanException {
		if(closedPosList==null){
			closedPosList = new LinkedList<SelectItem>();
		}
		return closedPosList;
	}
	public void setClosedPosList(List<SelectItem> closedPosList) {
		this.closedPosList = closedPosList;
	}
	
	private void buildClosedPosList() throws ManagerBeanException {
		if(getHotel()!=null && getHotel().getId()!=null){
			closedPosList = new LinkedList<SelectItem>();
			String sqlSelect = "SELECT *"
			+ " FROM pos"  
			+ " WHERE workplace = "+getHotel().getWorkPlace().getId() 
			+ " AND id NOT IN ( " 
			+ " SELECT Pos.id"
			+ " FROM pos as Pos LEFT JOIN pos_shift as PosShift on Pos.id = PosShift.pos" 
			+ " WHERE Pos.workPlace = "+getHotel().getWorkPlace().getId() 
			+ " AND PosShift.end_time is null"
			+ " AND PosShift.shift = "+getPosShift().getShift().ordinal()
			+ " ) " 
			+ " GROUP BY id" 
			+ " ORDER BY name"
			;
			Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
			Query sqlQuery = session.createSQLQuery(sqlSelect);
			for(Object to: sqlQuery.list()){
				Pos pos = (Pos) BeanManager.getManagerBean(Pos.class).get((Integer)(((Object[])to)[0]));
				SelectItem item = new SelectItem(pos, pos.getName());
				closedPosList.add(item);
			}
		}
	}

	public void onInit( ActionEvent event ){
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean("cashCalculator");
		controller.init();
		setCalculator(controller);
		setClosedPosList(null);
		setHotel(null);
		setPosShift(new PosShift());
		getPosShift().setStartTime(new Date());
	}

	public void onSearchPos( ActionEvent event ){
		try {
			buildClosedPosList();
		} catch (ManagerBeanException e) {
			closedPosList = new LinkedList<SelectItem>();
		}
	}
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setStartTime(new Date());
			getPosShift().setUser(UserUtils.getInstance().getLoggedUser());
			setPosShift((PosShift) bean.insertOrUpdate(getPosShift()));
			PosInvoicing posInvoicing = new PosInvoicing();
			posInvoicing.createInvoice(getPosShift());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la apertura de caja";
			throw new AbortProcessingException(msg, e);
		}
	}
	public void onReset( ActionEvent event ){
		onInit(event);
	}
	
	public void onShowCalculatorWindow( ActionEvent event ){
		getCalculator().setAmounts( new int[15] );
		getCalculator().setInitialAmount(true);
		getCalculator().setPosShift(getPosShift());
	}
	
	public void onAcceptCalculatorAmount( ActionEvent event ){
		getPosShift().setInitialAmount(getCalculator().getCalcTotal());
	}
	
	
}
