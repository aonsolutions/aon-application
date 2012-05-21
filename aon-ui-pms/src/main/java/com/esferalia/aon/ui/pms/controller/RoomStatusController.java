package com.esferalia.aon.ui.pms.controller;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.hibernate.Query;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.dao.hibernate.HibernateUtil;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.ui.pms.util.PmsReportManager;

public class RoomStatusController implements ICollectionProvider {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoomStatusController.class.getName());
	
	private Hotel hotel;
	private Date date;
	
	private List<RoomStatus> roomStatusList;
	private DataModel model;
	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public List<RoomStatus> getRoomStatusList() {
		return roomStatusList;
	}
	public void setRoomStatusList(List<RoomStatus> roomStatusList) {
		this.roomStatusList = roomStatusList;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
	
	@SuppressWarnings("rawtypes")
	private void buildRoomStatusList() throws ManagerBeanException {
		PmsReportManager reportManager = PmsReportManager.getInstance();
		
		setRoomStatusList(new LinkedList<RoomStatusController.RoomStatus>());
		
		String roomSelect = reportManager.getRoomStatusSQL(getHotel());
		
		Session session = HibernateUtil.getSession(HibernateUtil.getSessionFactoryName());

		Query roomQuery = session.createSQLQuery(roomSelect);
		roomQuery.setDate("date", new java.sql.Date(getDate().getTime()));
		
		for(Object o :roomQuery.list()){
			RoomStatus rs = new RoomStatus();
			rs.setRoom((String) (((Object[])o)[0]));
			rs.setStatus(Integer.parseInt((((Object[])o)[1]).toString()));
			getRoomStatusList().add(rs);
		}
	}
	
	public void onInit(ActionEvent event) {
		setDate(new Date());
	}
	
	public void onSearch(ActionEvent event) {
		try {
			buildRoomStatusList();
		} catch (ManagerBeanException e) {
			String msg = "Error al construir el listado de habitaciones";
			LOGGER.error(msg);
			throw new AbortProcessingException(msg, e);
		}
		setModel(new ListDataModel(getRoomStatusList()));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getRoomStatusList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
	/**************************************************/
	/**************************************************/
	
	public class RoomStatus {
		private String room;
		private ActivityStatus status;
		public String getRoom() {
			return room;
		}
		public void setRoom(String room) {
			this.room = room;
		}
		public ActivityStatus getStatus() {
			return status;
		}
		public void setStatus(ActivityStatus status) {
			this.status = status;
		}
		public void setStatus(Integer status) {
			for(ActivityStatus as: ActivityStatus.values()){
				if(status.equals(as.ordinal())){
					this.status = as;
					break;
				}
			}
		}
		
	}
	
}
