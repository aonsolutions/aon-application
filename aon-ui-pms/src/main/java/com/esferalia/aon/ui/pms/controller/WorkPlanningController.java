package com.esferalia.aon.ui.pms.controller;

import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.RoomWorkPlanning;


public class WorkPlanningController implements ICollectionProvider {
	
	private Hotel hotel;
	private Date date;
	private List<RoomPlanning> roomPlanningList;	
	private DataModel model;
	
	public List<RoomPlanning> getRoomPlanningList() {
		return roomPlanningList;
	}
	public void setRoomPlanningList(List<RoomPlanning> roomPlanningList) {
		this.roomPlanningList = roomPlanningList;
	}
	public DataModel getModel() {
		return model;
	}
	public void setModel(DataModel model) {
		this.model = model;
	}
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
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		setDate(new Date());
	}
	
	private ProjectReservationRoomDetail obtainActivity(Room room) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		if(getHotel()!=null && getHotel().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());
		}
		if(getDate()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), getDate());
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_ID), room.getId());
		List<ITransferObject> list = bean.getList(criteria);
		return (ProjectReservationRoomDetail) (list.isEmpty()?null:list.get(0));
	}
	private RoomWorkPlanning obtainAction(ProjectReservationRoomDetail reservation) throws ManagerBeanException {
		
		if(reservation!=null){
			if(reservation.isLastNight()){
				return RoomWorkPlanning.CHECKOUT;
			} else if(reservation.isFirstNight()){
				return RoomWorkPlanning.CHECKIN;
			} else {
				long days = CommonUtil.getDaysBetweenDates(reservation.getProjectReservationRoom().getProjectReservation().getStartDate(), getDate());
				return days%3==0?RoomWorkPlanning.SHEET_CHANGE:RoomWorkPlanning.CLEANING;
			}
		}
		return RoomWorkPlanning.FREE;
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		if(getHotel()!=null && getHotel().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			criteria.addOrder(bean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
		}
		List<ITransferObject> list = bean.getList(criteria);
		if( !list.isEmpty() ){
			setRoomPlanningList(new LinkedList<WorkPlanningController.RoomPlanning>());
			
			for(ITransferObject to: list){
				RoomPlanning rp = new RoomPlanning();
				rp.setRoom((Room) to);
				rp.setDate(getDate());
				rp.setReservation(obtainActivity((Room) to));
				rp.setAction(obtainAction(rp.getReservation()));
				getRoomPlanningList().add(rp);
			}
			setModel(new ListDataModel(getRoomPlanningList()));
		}
	}
	
	
	/**************************************************/
	/**************************************************/
	
	public class RoomPlanning {
		private Room room;
		private Date date;
		private ProjectReservationRoomDetail reservation;
		private RoomWorkPlanning action;
		
		public Room getRoom() {
			return room;
		}
		public void setRoom(Room room) {
			this.room = room;
		}
		public Date getDate() {
			return date;
		}
		public void setDate(Date date) {
			this.date = date;
		}
		public ProjectReservationRoomDetail getReservation() throws ManagerBeanException {
			return reservation;
		}
		public void setReservation(ProjectReservationRoomDetail reservation) {
			this.reservation = reservation;
		}
		
		public RoomWorkPlanning getAction() throws ManagerBeanException{
			return action;
		}
		public void setAction(RoomWorkPlanning action) {
			this.action = action;
		}
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection() {
		return getRoomPlanningList();
	}
	@SuppressWarnings("rawtypes")
	@Override
	public Collection getCollection(boolean forceRefresh)
			throws ManagerBeanException {
		return getCollection();
	}
	
}
