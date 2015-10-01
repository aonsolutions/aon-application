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
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.RoomStatus;

public class CleanStatusController extends BasicController {

	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private final String ROOM_IDX = "selectedRoomId";
	
	private final String FLOOR_IDX = "selectedFloorId";
	
	private Hotel hotel;
	
	private String selectedFloor;
	
	private List<Room> roomList;
	
	private List<String> floorList;

	private Map<Integer, ActivityStatus> roomStatus;
	
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
	
	public Map<Integer, ActivityStatus> getRoomStatus() {
		return roomStatus;
	}
	public void setRoomStatus(Map<Integer, ActivityStatus> roomStatus) {
		this.roomStatus = roomStatus;
	}
	
	
	public void onInit(ActionEvent event){
		setHotel(null);	
	}
	
	public void onSearch(ActionEvent event) {
		setSelectedFloor(null);
		setRoomList(new LinkedList<Room>());
		setFloorList(new LinkedList<String>());
		List<ITransferObject> roomList = getActiveRoomList(null);
		for (ITransferObject ito : roomList) {
			Room room = (Room)ito;
			getRoomList().add(room);
//			char floor = room.getAsset().getName().replaceAll("[^0-9]", "").charAt(0);
			String floor = room.getAsset().getName().replaceAll("([^0-9]*[0-9]).*", "$1");
			if(!getFloorList().contains(floor)){
				getFloorList().add(floor);
			}
		}
		roomStatus = new HashMap<>();
		for (ITransferObject ito : getRoomStatusList(roomList)) {
			AssetActivity activity = (AssetActivity) ito;
			roomStatus.put(activity.getAsset().getId(), activity.getStatus());
		}
		setModel(new SerializableListDataModel(getRoomList()));
	}
	
	public void onSelectFloor(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String newFloor = String.valueOf(params.get(FLOOR_IDX));
		selectedFloor = newFloor.equals(selectedFloor)?null:newFloor;
		setRoomList(new LinkedList<Room>());
		for (ITransferObject ito : getActiveRoomList(selectedFloor)) {
			Room room = (Room)ito;
			getRoomList().add(room);
		}
		setModel(new SerializableListDataModel(getRoomList()));
	}
	
//	private List<ITransferObject> getActiveRoomList(Hotel hotel, String floor) {
	private List<ITransferObject> getActiveRoomList(String floor) {
		try {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel()!=null?getHotel().getId():-1);
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

	private List<ITransferObject> getRoomStatusList(List<ITransferObject> roomList) {
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
				criteria.addEqualExpression(aaBean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), new Date());
				return aaBean.getList(criteria);
			} catch (ManagerBeanException e) {
				throw new AbortProcessingException(e.getMessage(), e);
			}
		}
		return Collections.emptyList();
	}
	
	public void onRefresh(ActionEvent event) {
		onSearch(event);
	}
	
	public void onCleanRoom(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Room room = getRoomList().get(new Integer(params.get(ROOM_IDX)));
		room.setStatus(RoomStatus.CLEAN);
		room.setLastCleaningDate(new Date());
		BeanManager.getManagerBean(Room.class).update(room);
	}

	public void onDirtyRoom(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Room room = getRoomList().get(new Integer(params.get(ROOM_IDX)));
		room.setStatus(RoomStatus.DIRTY);
		BeanManager.getManagerBean(Room.class).update(room);
	}
	
}
