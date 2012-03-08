package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.common.domain.DomainManager;
import com.code.aon.config.PayMethod;
import com.code.aon.config.enumeration.PayMethodType;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Pos;
import com.esferalia.aon.pms.PosShift;
import com.esferalia.aon.pms.PosShiftCount;


public class PosShiftController extends BasicController {
	
	private final String CASH_CALCULATOR_CONTROLLER_NAME = "cashCalculator";
	private final String POS_SHIFT_COUNT_CONTROLLER_NAME = "posShiftCount";
	private final String POS_SHIFT_CONTROLLER_NAME = "posShift";
	
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
	
	public Double getTotalCashAmount() {
		String sqlSelect;
		try {
			sqlSelect = "SELECT sum(amount)"
					+ " FROM pos_shift_count"
					+ " WHERE pos_shift = " + ((PosShift)getTo()).getId()
					+ getCashPayMethodClause()
					+ " GROUP BY pos_shift";
		} catch (ManagerBeanException e) {
			return null;
		}
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());
		Query sqlQuery = session.createSQLQuery(sqlSelect);
		return (Double) sqlQuery.uniqueResult();
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
	
	public List<SelectItem> getPosList() throws ManagerBeanException {
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ " WHERE " + DomainManager.getSQLWhereClause("Pos.domain") 
			+ " AND " + (getHotel() == null ? " Pos.id is null " : " Pos.workplace = " + getHotel().getWorkPlace().getId()) 
			+ " GROUP BY Pos.name"
			+ " ORDER BY Pos.name";
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
		CashCalculatorController controller = (CashCalculatorController) AonUtil.getRegisteredBean(CASH_CALCULATOR_CONTROLLER_NAME);
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
		if( isCashCalculator() ){
			IController controller = FormUtil.getController(POS_SHIFT_COUNT_CONTROLLER_NAME);
			getCalculator().setCashAmount(getCalculator().getCalcTotal());
			((PosShiftCount)controller.getTo()).setAmount(getCalculator().getCalcTotal());
		} else {
			IController controller = FormUtil.getController(POS_SHIFT_CONTROLLER_NAME);
			PosShift c = (PosShift) controller.getTo();
			c.setInitialAmount(getCalculator().getCalcTotal());
		} 
	}
	
}
