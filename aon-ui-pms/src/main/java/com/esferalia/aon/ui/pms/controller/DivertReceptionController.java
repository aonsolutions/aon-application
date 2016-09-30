package com.esferalia.aon.ui.pms.controller;

import java.io.Serializable;
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

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.dbutils.AonSQLException;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.config.util.UserUtils;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationDivert;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.enumeration.ReservationDivertStatus;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;
import com.esferalia.aon.ui.pms.util.PmsUtils;

public class DivertReceptionController extends BasicController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
	
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
	
	public boolean isNoRoomAssigned() throws ManagerBeanException{
		for(ProjectReservationRoom r: getReallocationList()){
			IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
			Criteria criteria = new Criteria();
			String alias = reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID);
			criteria.addEqualExpression(alias, r.getProjectReservation().getId());
			if(reservationRoomDetailBean.getCount(criteria)>0){
				return false;
			}
		}
		return true;
	}
	
	public void onConfirmDivert(ActionEvent event){
		setPendingRoom(null);
		setReallocationList(null);
		setAsignedList(null);
		try {
			buildReallocationRoomList(event);
			ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
			RoomAvailabilityController roomAvailabilityController = (RoomAvailabilityController) AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			roomAvailabilityController.getFilterParams().setViewerStartDate(divert.getDivertDate());
			roomAvailabilityController.getFilterParams().setViewerEndDate(divert.getProjectReservation().getEndDate());
			roomAvailabilityController.getFilterParams().setHotel(divert.getDivertHotel());
			if(getHotelRoomItems()!=null && getHotelRoomItems().size()>0){
				roomAvailabilityController.getFilterParams().setItem((Item) getHotelRoomItems().get(0).getValue());
			}
			roomAvailabilityController.onFilter(event);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar la reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	private void buildReallocationRoomList(ActionEvent event) throws ManagerBeanException {
		ProjectReservationDivert divert = (ProjectReservationDivert) getTo();
		IManagerBean bean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(bean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_PROJECT_RESERVATION_ID), divert.getProjectReservation().getId());
		setReallocationList(new LinkedList<ProjectReservationRoom>());
		for(ITransferObject to: bean.getList(criteria)){
			getReallocationList().add((ProjectReservationRoom) to);
		}
	}
	
	public List<SelectItem> getPendingRooms() throws ManagerBeanException {
		List<SelectItem> list = new LinkedList<SelectItem>(); 
		for(ProjectReservationRoom r: getReallocationList()){
			String label = r.getRoomIndex()+" - "+r.getItem().getProduct().getCode()+" - "+r.getItem().getProduct().getName();
			SelectItem item = new SelectItem(r, label);
			list.add(item);
		}
		return list;
	}
	
	public void onAssignReservationRoom(ActionEvent event){
		try {
			ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
			Map<String, String> params = ec.getRequestParameterMap();
			RoomReallocation rr = new RoomReallocation();
			rr.setReservationRoom(getPendingRoom());
			rr.setRoom((Room)BeanManager.getManagerBean(Room.class).get(new Integer(params.get(IPmsConstants.AVAILABLE_ROOM))));
			getAsignedList().add(rr);
			getReallocationList().remove(getPendingRoom());
			setPendingRoom(null);
		} catch (NumberFormatException e) {
			String msg = "No se ha podido asignar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido asignar.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}
	
	public void onAcceptDivert(ActionEvent event) throws ManagerBeanException{
		try {
			changeReservationHotel();

			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			Date startDate = roomAvailability.getFilterParams().getViewerStartDate();
			Date endDate = roomAvailability.getFilterParams().getViewerEndDate();
			ReservationUtils reservationUtils = new ReservationUtils();
			for(RoomReallocation rr: getAsignedList()){
				if(rr.getReservationRoom().getRoomNumber()==null){
					reservationUtils.insertProjectReservationRoomDetails(rr.getReservationRoom(), startDate, endDate, rr.getRoom(), getAvailableServicesList(rr.getReservationRoom()));

					sendInventoryData(rr.getReservationRoom(), rr.getReservationRoom().getHotel(), rr.getReservationRoom().getItem(), startDate, endDate);
					sendInventoryData(rr.getReservationRoom(), rr.getRoom().getHotel(), rr.getRoom().getItem(), startDate, endDate);
				} else {
					List<Item> inventoryItems = reservationUtils.getProjectReservationRoomDetailItems(rr.getReservationRoom(), startDate, endDate);
			    	if (!inventoryItems.contains(rr.getReservationRoom().getItem())) {
			    		inventoryItems.add(rr.getReservationRoom().getItem());
			    	}
					reservationUtils.updateProjectReservationRoomDetails(rr.getReservationRoom(), startDate, endDate, rr.getRoom());

					sendInventoryData(rr.getReservationRoom(), rr.getReservationRoom().getHotel(), inventoryItems, startDate, endDate);
					sendInventoryData(rr.getReservationRoom(), rr.getRoom().getHotel(), rr.getRoom().getItem(), startDate, endDate);
				}
			}
		} catch (NumberFormatException e) {
			String msg = "No se ha podido actualizar la reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar la reserva.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
		updateStatus((ProjectReservationDivert) getTo(), ReservationDivertStatus.ACCEPTED);
	}

    private void sendInventoryData(ProjectReservationRoom reservationRoom, Hotel hotel, Item item, Date startDate, Date endDate) {
    	InventoryManager manager = new InventoryManager();
       	manager.processInventoryQuery(reservationRoom, hotel, item, startDate, DateUtils.addDays(endDate, -1));
    }

    private void sendInventoryData(ProjectReservationRoom reservationRoom, Hotel hotel, List<Item> items, Date startDate, Date endDate) {
		for (Item item : items) {
			sendInventoryData(reservationRoom, hotel, item, startDate, endDate);
		}
    }

	public void onCancelDivert(ActionEvent event) throws ManagerBeanException{
		cancelDivert((ProjectReservationDivert) getModel().getRowData());
	}

	public void onPendingRoomChanged(ValueChangeEvent event){
		
	}
	
	private Integer[] getAvailableServicesList(ProjectReservationRoom reservationRoom) throws ManagerBeanException {
		List<ProjectReservationService> servicesList = new LinkedList<ProjectReservationService>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, reservationRoom.getProjectReservation().getId());
		criteria.addNullExpression(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL));
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID));
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationService reservationService = ((ProjectReservationServiceDetail)ito).getProjectReservationService();
			if (!servicesList.contains(reservationService)) {
				servicesList.add(reservationService);
			}
		}
		Integer[] services = new Integer[servicesList.size()];
		for (ProjectReservationService reservationService : servicesList) {
			int roomCount = reservationService.getProjectReservation().getRoomCount();
			if ((isNevv() && roomCount == 0) || (!isNevv() && roomCount == 1)) {
				services[servicesList.indexOf(reservationService)] = reservationService.getId();
			}
		}
		return services;
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

	private void cancelDivert(ProjectReservationDivert divert) {
		updateStatus(divert, ReservationDivertStatus.REFUSED);
	}
	
	private void updateStatus(ProjectReservationDivert divert, ReservationDivertStatus status){
		try {
			divert.setStatus(status);
			divert.setResponseUser(UserUtils.getInstance().getLoggedUser());
			IManagerBean bean = BeanManager.getManagerBean(ProjectReservationDivert.class);
			bean.update(divert);
		} catch (ManagerBeanException e) {
			String msg = "No se ha podido actualizar el estado.";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		} 
	}
	
	
	public List<SelectItem> getHotelRoomItems() throws ManagerBeanException {
		return PmsUtils.getHotelRoomItems(((ProjectReservationDivert)getTo()).getDivertHotel());
	}
	
	public List<Room> getAvailableRoomList() throws AonSQLException {
		if(getPendingRoom()==null){
			return null;
		}
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
	
	public boolean isAcceptable(){
		try {
			if(getModel().getRowIndex()>-1){
				ProjectReservationDivert divert = (ProjectReservationDivert)getModel().getRowData();
				return divert.isPending()
						&& ( AonUtil.getRoleManager().isSaleOperator() || AonUtil.getRoleManager().isConfig()
						|| ( DateUtils.addDays(divert.getDivertDate(), 2).after(new Date())) );
			}
		} catch (ManagerBeanException e) {
			// NADA
		} 
		return false;
	}
	
	public static class RoomReallocation implements Serializable  {
		
		private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;
		
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