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
		String sqlSelect = "SELECT Pos.*"
			+ " FROM pos as Pos"
			+ " LEFT JOIN pos_shift as PosShift on PosShift.pos = Pos.id"
			+ " WHERE  (PosShift.end_time is not null"
			+ " OR PosShift.start_time is null)"
			+ (getHotel() == null ? "" : " AND Pos.workplace = " + getHotel().getWorkPlace().getId()) 
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
	public void onAccept( ActionEvent event ){
		try {
			IManagerBean bean = BeanManager.getManagerBean(PosShift.class);
			getPosShift().setStartTime(new Date(getPosShift().getStartTime().getTime()));
			getPosShift().setUser(UserUtils.getInstance().getLoggedUser());
			bean.insert(getPosShift());
		} catch (ManagerBeanException e) {
			String msg = "Error al grabar la apertura de caja";
			throw new AbortProcessingException(msg, e);
		}
	}
	
	
}
