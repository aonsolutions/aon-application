package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.code.aon.asset.AssetActivity;
import com.code.aon.asset.enumeration.ActivityStatus;
import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ICollectionProvider;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.common.serialize.SerializableListDataModel;
import com.code.aon.ui.form.DataScrollerState;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.Room;

public class EmptyRoomController extends DataScrollerState implements ICollectionProvider {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmptyRoomController.class.getName());
	
	private Hotel hotel;
	private Date date;
	
	private List<RoomStatus> roomStatusList;
	
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
	
	private void buildRoomStatusList() throws ManagerBeanException {
		IManagerBean bean = BeanManager.getManagerBean(AssetActivity.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_DATE), getDate());
		criteria.addInExpression(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_ID), getAssetIds());
		criteria.addOrder(bean.getFieldName(IEntityAlias.ASSET_ACTIVITY_ASSET_NAME));
		
		Iterator<ITransferObject> activityIt = bean.getList(criteria).iterator();
		Iterator<ITransferObject> roomIt = getRoomList().iterator();
		
		AssetActivity activity = activityIt.hasNext()?(AssetActivity)activityIt.next():null;
		
		setRoomStatusList(new LinkedList<RoomStatus>());
		
		while(roomIt.hasNext()){
			RoomStatus rs = new RoomStatus();;
			Room room = (Room) roomIt.next();
			if(activity!=null && room.getAsset().getId().equals(activity.getAsset().getId())){
				// solo se tiene en cuenta que el estado no sea acupado, esto es, que se encuentre vacia (libre, bloqueado, mnto, ...)
				if(activity.getStatus()!=ActivityStatus.BUSY){
					rs.setRoom(room);
					rs.setFree(false);
					getRoomStatusList().add(rs);
				}
				while( activity!=null && room.getAsset().getId().equals(activity.getAsset().getId()) ){
					activity = activityIt.hasNext()?(AssetActivity)activityIt.next():null;
				}
			} else {
				rs.setRoom(room);
				rs.setFree(true);
				getRoomStatusList().add(rs);
			}
		}
	}
	
	private List<Integer> getAssetIds() throws ManagerBeanException {
		List<Integer> list = new LinkedList<Integer>();
		for(ITransferObject to: getRoomList() ){
			Room r = (Room) to;
			list.add(r.getAsset().getId());
		}
		return list;
	}
	private List<ITransferObject> getRoomList() throws ManagerBeanException {
		setRoomStatusList(new LinkedList<RoomStatus>());
		IManagerBean bean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), getHotel().getId());
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.ROOM_ACTIVE), Boolean.TRUE);
		criteria.addOrder(bean.getFieldName(IEntityAlias.ROOM_ASSET_NAME));
		return bean.getList(criteria);
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
		setModel(new SerializableListDataModel(getRoomStatusList()));
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
	
	public static class RoomStatus implements Serializable {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
		private Room room;
		private boolean free;
		public Room getRoom() {
			return room;
		}
		public void setRoom(Room room) {
			this.room = room;
		}
		public boolean isFree() {
			return free;
		}
		public void setFree(boolean free) {
			this.free = free;
		}
		
	}
	
}
