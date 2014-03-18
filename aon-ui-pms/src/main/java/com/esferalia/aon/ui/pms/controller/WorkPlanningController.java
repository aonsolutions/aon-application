package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.RoomWorkPlanning;


public class WorkPlanningController extends DataScrollerState implements ICollectionProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private Hotel hotel;
	private Date date;
	private List<RoomPlanning> roomPlanningList;	
	
	public List<RoomPlanning> getRoomPlanningList() {
		return roomPlanningList;
	}
	public void setRoomPlanningList(List<RoomPlanning> roomPlanningList) {
		this.roomPlanningList = roomPlanningList;
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
	
	private ProjectReservationRoomDetail obtainActivity(Room room, Date date) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		if(getHotel()!=null && getHotel().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());
		}
		if(getDate()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE), date);
		}
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_ASSET_ID), room.getId());
		List<ITransferObject> list = bean.getList(criteria);
		return (ProjectReservationRoomDetail) (list.isEmpty()?null:list.get(0));
	}
	
	private RoomWorkPlanning obtainRoomOperation(ProjectReservationRoomDetail reservationRoomDetail, Room room) throws ManagerBeanException {
		if(isBlockedRoom(room, getDate())){
			return RoomWorkPlanning.BLOCKED;
		}
		if(reservationRoomDetail!=null){
			if(reservationRoomDetail.isFirstNight()){
				return RoomWorkPlanning.CHECKIN;
			} else {
				try {
					long days = CommonUtil.getDaysBetweenDates(reservationRoomDetail.getProjectReservationRoom().getProjectReservation().getStartDate(), getDate());
					Integer sheetChangingDays = reservationRoomDetail.getRoom().getHotel().getSheetChanging();
					return days%sheetChangingDays==0?RoomWorkPlanning.SHEET_CHANGE:RoomWorkPlanning.CLEANING;
				} catch (IllegalArgumentException e){
					AonUtil.addErrorMessage("Fechas incongruentes en la reserva " + reservationRoomDetail.getProjectReservationRoom().getProjectReservation().getProject().getId());
				}
			}
		} else {
			ProjectReservationRoomDetail previousReservationRoomDetail = obtainActivity(room, DateUtils.addDays(getDate(), -1));
			boolean lastNight = previousReservationRoomDetail!=null?previousReservationRoomDetail.isLastNight():false;
			if(lastNight){	
				return RoomWorkPlanning.CHECKOUT;
			}
		}
		return RoomWorkPlanning.FREE;
	}
	
	private boolean isBlockedRoom(Room room, Date date) throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), room.getAsset().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), date);
		criteria.addNotEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_STATUS), ActivityStatus.BUSY);
		return bean.getCount(criteria)>0;
	}
	
	public void onSearch(ActionEvent event) throws ManagerBeanException{
		IManagerBean bean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		if(getHotel()!=null && getHotel().getId()!=null){
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
			criteria.addOrder(bean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
		}
		List<ITransferObject> list = bean.getList(criteria);
		if( !list.isEmpty() ){
			setRoomPlanningList(new LinkedList<WorkPlanningController.RoomPlanning>());
			for(ITransferObject to: list){
				RoomPlanning rp = new RoomPlanning();
				rp.setRoom((Room) to);
				rp.setDate(getDate());
				rp.setReservationRoomDetail(obtainActivity((Room) to, getDate()));
				rp.setAction(obtainRoomOperation(rp.getReservationRoomDetail(), (Room) to));
				getRoomPlanningList().add(rp);
			}
			setModel(new SerializableListDataModel(getRoomPlanningList()));
		}
	}
	
	
	/**************************************************/
	/**************************************************/
	
	public static class RoomPlanning implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Room room;
		private Date date;
		private ProjectReservationRoomDetail reservationRoomDetail;
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
		public ProjectReservationRoomDetail getReservationRoomDetail() {
			return reservationRoomDetail;
		}
		public void setReservationRoomDetail(
				ProjectReservationRoomDetail reservationRoomDetail) {
			this.reservationRoomDetail = reservationRoomDetail;
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
