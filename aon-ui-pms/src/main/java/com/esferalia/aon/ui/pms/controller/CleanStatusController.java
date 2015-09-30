package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import org.apache.commons.lang.StringUtils;

import com.code.aon.AonVersion;
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
	
	private List<Room> roomList;
	
	private List<String> floorList;
	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
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
	
	public List<SelectItem> getHotelRooms() throws ManagerBeanException {
		List<SelectItem> hotelRooms = new LinkedList<SelectItem>();
		if (getHotel() != null && getHotel().getId() != null) {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
//			if (StringUtils.isNotBlank(getRoomFilter()) && !getRoomFilter().equals("*")) {
//				String filter = getRoomFilter().replace("*", "%");
//				criteria.addExpression(ExpressionUtilities.getLikeExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), filter));
//			}
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
			for (ITransferObject ito : roomBean.getList(criteria)) {
				Room room = (Room)ito;
				hotelRooms.add(new SelectItem(room, room.getAsset().getName()));
			}
		}
		return hotelRooms;
	}
	
	public void onInit(ActionEvent event){
		setHotel(null);	
	}
	
	public void onSearch(ActionEvent event) {
		setRoomList(new LinkedList<Room>());
		setFloorList(new LinkedList<String>());
		try {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
			for (ITransferObject ito : roomBean.getList(criteria)) {
				Room room = (Room)ito;
				getRoomList().add(room);
				char floor = room.getAsset().getName().replaceAll("[^0-9]", "").charAt(0);
				if(!getFloorList().contains(String.valueOf(floor))){
					getFloorList().add(String.valueOf(floor));
				}
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getRoomList()));
	}
	
	public void onSelectFloor(ActionEvent event) {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		String floor = String.valueOf(new Integer(params.get(FLOOR_IDX))+1);
		
		setRoomList(new LinkedList<Room>());
		
		try {
			IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
			Criteria criteria = new Criteria();
			criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
			if (StringUtils.isNotBlank(floor)) {
				criteria.addExpression(ExpressionUtilities.getLikeExpression(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME), floor+"%"));
			}
			criteria.addOrder(roomBean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
			for (ITransferObject ito : roomBean.getList(criteria)) {
				Room room = (Room)ito;
				getRoomList().add(room);
			}
		} catch (ManagerBeanException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		setModel(new SerializableListDataModel(getRoomList()));
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
