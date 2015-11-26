package com.esferalia.aon.ui.pms.controller;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.RoomStatus;

public class CleanStatusController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String ROOM_IDX = "selectedRoomId";
	
	private final String FLOOR_IDX = "selectedFloorId";
	
	private Date searchDate;

	private Hotel hotel;
	
	private String selectedFloor;
	
	private List<Room> roomList;
	
	private List<String> floorList;

	private Map<Integer, ActivityStatus> blockedRooms;
	
	private Map<Integer, ActivityStatus> busyRooms;
	
	private Map<Integer, ActivityStatus> checkoutRooms;

	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
	}
	
	public String getSelectedFloor() {
		return selectedFloor;
	}
	public void setSelectedFloor(String selectedFloor) {
		this.selectedFloor = selectedFloor;
	}
	public List<Room> getRoomList() {
		return roomList;
	}
	public void setRoomList(List<Room> roomList) {
		this.roomList = roomList;
	}
	
	public List<String> getFloorList() {
		return floorList;
	}
	public void setFloorList(List<String> floorList) {
		this.floorList = floorList;
	}
	
	public Map<Integer, ActivityStatus> getBlockedRooms() {
		return blockedRooms;
	}
	public void setBlockedRooms(Map<Integer, ActivityStatus> blockedRooms) {
		this.blockedRooms = blockedRooms;
	}
	
	public Map<Integer, ActivityStatus> getBusyRooms() {
		return busyRooms;
	}
	public void setBusyRooms(Map<Integer, ActivityStatus> busyRooms) {
		this.busyRooms = busyRooms;
	}
	
	public Map<Integer, ActivityStatus> getCheckoutRooms() {
		return checkoutRooms;
	}
	public void setCheckoutRooms(Map<Integer, ActivityStatus> checkoutRooms) {
		this.checkoutRooms = checkoutRooms;
	}
	
	
	
	public void onInit(ActionEvent event) throws ManagerBeanException{
		searchDate = new Date();
		setHotel(null);
		clearData();
		
		PmsCollectionsController collections = (PmsCollectionsController) AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		if(collections.getCurrentUserHotelList()!=null && collections.getCurrentUserHotelList().size()==1){
			setHotel((Hotel) collections.getCurrentUserHotelList().get(0));
			onSearch(event);
		}
	}
	
	private void clearData(){
		setRoomList(new LinkedList<Room>());
		setFloorList(new LinkedList<String>());
		setSelectedFloor(null);
		blockedRooms = new HashMap<>();
		busyRooms = new HashMap<>();
		checkoutRooms = new HashMap<>();
	}
	
	public void onSearch(ActionEvent event) {
		clearData();
		List<ITransferObject> roomList = getActiveRoomList(getHotel(), null);
		for (ITransferObject ito : roomList) {
			Room room = (Room)ito;
			getRoomList().add(room);
			String floor = null;
			if(room.getAsset().getName().matches("[^0-9]*[0-9]+")){
				floor = room.getAsset().getName().replaceAll("([^0-9]*[0-9]).*", "$1");
			} else if(room.getAsset().getName().matches("[0-9]*[^0-9]*")){
				floor = room.getAsset().getName().replaceAll("([0-9]).*([^0-9]*)", "$1$2");
			}
			if(!getFloorList().contains(floor)){
				getFloorList().add(floor);
			}
		}
		
		List<ITransferObject> roomActivityList = getRoomActivityList(roomList);
		for (ITransferObject ito : roomActivityList) {
			AssetActivity activity = (AssetActivity) ito;
			if(DateUtils.isSameDay(searchDate, activity.getDate())){
				if(activity.getStatus() != ActivityStatus.BUSY){
					blockedRooms.put(activity.getAsset().getId(), activity.getStatus());
				} else {
					busyRooms.put(activity.getAsset().getId(), activity.getStatus());
				}
			}
		}
		for (ITransferObject ito : getCheckoutActivityList()) {
			ProjectReservationRoomDetail projectReservationRoomDetail = (ProjectReservationRoomDetail) ito;
			AssetActivity activity = projectReservationRoomDetail.getAssetActivity();
			checkoutRooms.put(activity.getAsset().getId(), activity.getStatus());
		}
		
		setModel(new SerializableListDataModel(getRoomList()));
	}
	
	public void onSelectFloor(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String newFloor = String.valueOf(new Integer(params.get(FLOOR_IDX)));
		selectedFloor = newFloor.equals(selectedFloor)?null:newFloor;
	}
	
	private List<ITransferObject> getActiveRoomList(Hotel hotel, String floor) {
		try {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), hotel!=null?hotel.getId():-1);
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
			if (StringUtils.isNotBlank(floor)) {
				criteria.addExpression(ExpressionUtilities.getLikeExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), floor+"%"));
			}
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
			return roomBean.getList(criteria);
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
	}

	private List<ITransferObject> getRoomActivityList(List<ITransferObject> roomList) {
		List<Integer> roomIds = new LinkedList<Integer>();
		if(roomList!=null && roomList.size()>0) {
			for(ITransferObject to: roomList) {
				Room room = (Room) to;
				roomIds.add(room.getAsset().getId());
			}
			try {
				IManagerBean aaBean = BeanManager.getManagerBean(AssetActivity.class);
				Criteria criteria = new Criteria();
				criteria.addInExpression(aaBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), roomIds);
				criteria.addEqualExpression(aaBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), searchDate);
				return aaBean.getList(criteria);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}
	
	private List<ITransferObject> getCheckoutActivityList() {
		if(getHotel()!=null && getHotel().getId()!=null) {
			try {
				IManagerBean prrdBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
				Criteria criteria = new Criteria();
				String alias = prrdBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_HOTEL_ID);
				criteria.addEqualExpression(alias, getHotel().getId());
				alias = prrdBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_END_DATE);
				criteria.addEqualExpression(alias, searchDate);
				alias = prrdBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE);
				criteria.addEqualExpression(alias, DateUtils.addDays(searchDate, -1));
				return prrdBean.getList(criteria);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}
	
	public void onRefresh(ActionEvent event) {
		onSearch(event);
	}
	
	public void onSelectRoom(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Room room = getRoomList().get(new Integer(params.get(ROOM_IDX)));
		if(room.getStatus()==RoomStatus.DIRTY){
			room.setStatus(RoomStatus.CLEAN);
			room.setLastCleaningDate(new Date());
		} else if(room.getStatus()==RoomStatus.CLEAN){
			room.setStatus(RoomStatus.DO_NOT_DISTURB);
		} else {
			room.setStatus(RoomStatus.DIRTY);
		}
		BeanManager.getManagerBean(Room.class).update(room);
	}
	
}
