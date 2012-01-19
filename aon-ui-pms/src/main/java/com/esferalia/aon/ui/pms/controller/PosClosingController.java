package com.esferalia.aon.ui.pms.controller;

import java.util.ArrayList;
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
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.PosShift;


public class PosClosingController {
	
	private PosShift posShift;
	private Hotel hotel;
	private boolean showCalculatorWindow;
	private List<Integer> amounts = new ArrayList<Integer>();
	private int[] amounts2 = new int[15];
	
	
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
	public List<Integer> getAmounts() {
		return amounts;
	}
	public void setAmounts(List<Integer> amounts) {
		this.amounts = amounts;
	}
	public int[] getAmounts2() {
		return amounts2;
	}
	public void setAmounts2(int[] amounts2) {
		this.amounts2 = amounts2;
	}
	
	public List<SelectItem> getOpenedPos() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>();
		IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
		Criteria criteria = new Criteria();
		criteria.addNotNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_START_TIME));
		criteria.addNullExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_END_TIME));
		// TODO generar alias POS_SHIFT_POS_WORKPLACE_ID
//		if(getHotel()!=null && getHotel().getId()!=null){
//			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.POS_SHIFT_POS_WORKPLACE_ID), getHotel().getWorkPlace().getId());
//		}
		for(ITransferObject to: bean.getList(criteria)){
			PosShift ps = (PosShift) to;
			SelectItem item = new SelectItem(ps, ps.getPos().getName());
			list.add(item);
		}
		return list;
	}
	
	public void onInit( ActionEvent event ){
//		setPosShift(new PosShift());
//		BasicController controller = (BasicController) FormUtil.getController("posShiftCount");
//		controller.onEditSearch(event);
//		controller.onSearch(event);
	}	
	
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setEndTime(new Date());
			bean.update(getPosShift());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar el cierre de caja";
			throw new AbortProcessingException(msg, e);
		}
	}	
	
	public void onShowCalculatorWindow( ActionEvent event ){
		
	}
	
			
}
