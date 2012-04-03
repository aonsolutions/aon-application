package com.esferalia.aon.ui.pms.controller;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionUtilities;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.form.FormUtil;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class DivertReceptionController extends BasicController {
	
	private boolean showRoomDetailWindow;
	private Room availableRoom;
	private ProjectReservationRoom pendingRoom;
	private List<ProjectReservationRoom> reallocationList;
	private List<RoomReallocation> asignedList;
	
	
	public List<RoomReallocation> getAsignedList() {
		if(asignedList==null){
			asignedList = new LinkedList<DivertReceptionController.RoomReallocation>();
		}
		return asignedList;
	}
	public void setAsignedList(List<RoomReallocation> asignedList) {
		this.asignedList = asignedList;
	}
	public List<ProjectReservationRoom> getReallocationList() {
		return reallocationList;
	}
	public void setReallocationList(List<ProjectReservationRoom> reallocationList) {
		this.reallocationList = reallocationList;
	}
	public boolean isShowRoomDetailWindow() {
		return showRoomDetailWindow;
	}
	public void setShowRoomDetailWindow(boolean showRoomDetailWindow) {
		this.showRoomDetailWindow = showRoomDetailWindow;
	}
	public Room getAvailableRoom() {
		return availableRoom;
	}
	public void setAvailableRoom(Room availableRoom) {
		this.availableRoom = availableRoom;
	}
	public ProjectReservationRoom getPendingRoom() {
		return pendingRoom;
	}
	public void setPendingRoom(ProjectReservationRoom pendingRoom) {
		this.pendingRoom = pendingRoom;
	}
	
	public boolean isAllReallocated(){
		return getReallocationList().size()==0;
	}
	
	public void onConfirmDivert(ActionEvent event){
		setPendingRoom(null);
		setReallocationList(null);
		setAsignedList(null);
		try {
			buildReallocationRoomList(event);
			ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
			RoomAvailabilityController roomAvailabilityController = (RoomAvailabilityController) FormUtil.getController(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			roomAvailabilityController.getFilterParams().setViewerStartDate(divert.getDivertDate());
			roomAvailabilityController.getFilterParams().setViewerEndDate(divert.getProjectReservation().getEndDate());
			roomAvailabilityController.getFilterParams().setHotel(divert.getDivertHotel());
			if(getHotelRoomItems()!=null && getHotelRoomItems().size()>0){
				roomAvailabilityController.getFilterParams().setItem((Item) getHotelRoomItems().get(0).getValue());
			}
			roomAvailabilityController.onFilter(event);
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void buildReallocationRoomList(ActionEvent event) throws ManagerBeanException {
		ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
		ProjectReservationController reservationController = (ProjectReservationController) FormUtil.getController(IPmsConstants.RESERVATION_CONTROLLER_NAME);
		reservationController.select(event, divert.getProjectReservation());
		ProjectReservationRoomController roomController = (ProjectReservationRoomController) FormUtil.getController(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
//		roomController.onSelectFirst(event);
		
		setReallocationList(new LinkedList<ProjectReservationRoom>());
		for(ITransferObject to: roomController.getWrappedList()){
//			RoomReallocation rr = new RoomReallocation();
//			rr.setReservationRoom((ProjectReservationRoom) to);
			getReallocationList().add((ProjectReservationRoom) to);
		}
		
	}
	
	public List<SelectItem> getPendingRooms() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>(); 
		for(ProjectReservationRoom r: getReallocationList()){
//			if(rr.getRoom()==null){
				String label = r.getRoomIndex()+" - "+r.getItem().getProduct().getCode()+" - "+r.getItem().getProduct().getName();
				SelectItem item = new SelectItem(r, label);
				list.add(item);
//			}
		}
		return list;
	}
	
	public void onAssignReservationRoom(ActionEvent event){
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();

//			setAvailableRoom((Room)BeanManager.getManagerBean(Room.class).get(new Integer(params.get(IPmsConstants.AVAILABLE_ROOM))));
			
			RoomReallocation rr = new RoomReallocation();
			rr.setReservationRoom(getPendingRoom());
			rr.setRoom((Room)BeanManager.getManagerBean(Room.class).get(new Integer(params.get(IPmsConstants.AVAILABLE_ROOM))));
			
			getAsignedList().add(rr);
			getReallocationList().remove(getPendingRoom());
			setPendingRoom(null);
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onAcceptDivert(ActionEvent event){
		try {
//			ProjectReservationRoomController roomController = (ProjectReservationRoomController) FormUtil.getController(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
//			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)roomController.getTo();
			
			ReservationUtils reservationUtils = new ReservationUtils();

			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			Date startDate = roomAvailability.getFilterParams().getViewerStartDate();
			Date endDate = roomAvailability.getFilterParams().getViewerEndDate();
			
//			reservationUtils.updateProjectReservationRoomDetails(reservationRoom, startDate, endDate, getAvailableRoom());
			
			for(RoomReallocation rr: getAsignedList()){
				if(rr.getReservationRoom().getRoomNumber()!=null){
					
					reservationUtils.updateProjectReservationRoomDetails(rr.getReservationRoom(), startDate, endDate, rr.getRoom());
				}
			}
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ManagerBeanException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		changeReservationHotel();
		updateStatus(ReservationDivertStatus.ACCEPTED);
	}

	public void onCancelDivert(ActionEvent event){
		cancelDivert();
	}

	public void onPendingRoomChanged(ValueChangeEvent event){
//		ProjectReservationRoomController roomController = (ProjectReservationRoomController) FormUtil.getController(IPmsConstants.RESERVATION_ROOM_CONTROLLER_NAME);
//		try {
//			if(getPendingRoom()!=null){
//				roomController.select(null, getPendingRoom());
//			} else {
//				roomController.onCancel(null);
//			}
//		} catch (ManagerBeanException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}

	private void changeReservationHotel() {
		try {
			ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
			ProjectReservation reservation = divert.getProjectReservation();
			reservation.setHotel(divert.getDivertHotel());
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservation.class);
			bean.update(reservation);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar la reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	private void cancelDivert() {
		updateStatus(ReservationDivertStatus.REFUSED);
	}
	
	private void updateStatus(ReservationDivertStatus status){
		try {
			ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
			divert.setStatus(status);
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationDivert.class);
			bean.update(divert);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar el estado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}
	
	
	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		List<Integer> items = new LinkedList<Integer>();
		IManagerBean roomBean = BeanManager.getManagerBean(Room.class);
		Criteria criteria = new Criteria();
		ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
		criteria.addEqualExpression(roomBean.getFieldName(IEntityAlias.ROOM_HOTEL_ID), divert.getDivertHotel().getId());
		for (ITransferObject ito : roomBean.getList(criteria)) {
			Room room = (Room)ito;
			if (!items.contains(room.getItem().getId())) {
				items.add(room.getItem().getId());
			}
		}

		List<SelectItem> roomItems = new LinkedList<SelectItem>();
		if (items.size() > 0) {
			IManagerBean itemBean = BeanManager.getManagerBean(Item.class);
			criteria = new Criteria();
			criteria.addExpression(ExpressionUtilities.getInExpression(itemBean.getFieldName(IEntityAlias.ITEM_ID), items));
			criteria.addOrder(itemBean.getFieldName(IEntityAlias.ITEM_PRODUCT_NAME));
			for (ITransferObject ito : itemBean.getList(criteria)) {
				Item item = (Item)ito;
				SelectItem roomItem = new SelectItem(item, item.getProduct().getCode() + " - " + item.getProduct().getName());
				roomItems.add(roomItem);
			}
		}
		return roomItems;
	}
	
	public List<Room> getAvailableRoomList() throws ManagerBeanException {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		List<Room> list = roomAvailability.getAvailableRoomList();
		for(RoomReallocation rr: getAsignedList()){
			for(Room r: list){
				if(r.equals(rr.getRoom())){
					list.remove(rr.getRoom());
					break;
				}
			}
		}
		return list;
	}
	
	public class RoomReallocation {
		private ProjectReservationRoom reservationRoom;
		private Room room;
	
		public ProjectReservationRoom getReservationRoom() {
			return reservationRoom;
		}
		public void setReservationRoom(ProjectReservationRoom reservationRoom) {
			this.reservationRoom = reservationRoom;
		}
		public Room getRoom() {
			return room;
		}
		public void setRoom(Room room) {
			this.room = room;
		}
	}

}