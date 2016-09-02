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

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ITransferObject;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ql.Criteria;
import com.code.aon.ui.form.IController;
import com.code.aon.ui.form.LinesController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.ProjectReservationRoomDetail;
import com.esferalia.aon.pms.ProjectReservationService;
import com.esferalia.aon.pms.ProjectReservationServiceDetail;
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.reservation.InventoryManager;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationRoomController extends LinesController {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean showRoomDetailWindow;
	private boolean showFeatures;
	private Integer[] linkedServices;

	public boolean isShowRoomDetailWindow() {
		return showRoomDetailWindow;
	}

	public void setShowRoomDetailWindow(boolean showRoomDetailWindow) {
		this.showRoomDetailWindow = showRoomDetailWindow;
	}

	public boolean isShowFeatures() {
		return showFeatures;
	}

	public void setShowFeatures(boolean showFeatures) {
		this.showFeatures = showFeatures;
	}

	public Integer[] getLinkedServices() {
		return linkedServices;
	}

	public void setLinkedServices(Integer[] linkedServices) {
		this.linkedServices = linkedServices;
	}

	@Override
	public void onReset(ActionEvent event) {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		masterController.accept(event);
		try {
			ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
			reservationRoom.setProjectReservation((ProjectReservation)getMasterController().getTo());
			PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
			if (collections.getRoomItems().size() > 1) {
				reservationRoom.setItem((Item)collections.getRoomItems().get(0).getValue());
			}
			if (collections.getTariffs().size() > 1) {
				Customer customer = (Customer)BeanManager.getManagerBean(Customer.class).get(reservationRoom.getProjectReservation().getProject().getRegistry().getId());
				Tariff tariff = (customer != null && customer.getTariff() != null) ? customer.getTariff() : (Tariff)collections.getTariffs().get(0).getValue();
				reservationRoom.setTariff(tariff);
			}
			setTo(reservationRoom);
			masterController.getReservationPermission().setReservationRoom(reservationRoom);

			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			roomAvailability.onInitializeRoomList(reservationRoom, null, null);

			setNevv(true);
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Habitación";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	@Override
	public void onSelect(ActionEvent event) {
		ProjectReservationController masterController = (ProjectReservationController)getMasterController();
		masterController.accept(event);
		try {
			if (getModel().isRowAvailable()) {
				ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getModel().getRowData();
				reservationRoom.setProjectReservation((ProjectReservation)getMasterController().getTo());
				setTo(reservationRoom);
				masterController.getReservationPermission().setReservationRoom(reservationRoom);

				Date startDate = reservationRoom.getProjectReservation().getStartDate();
				Date endDate = reservationRoom.getProjectReservation().getEndDate();
				Date date = new Date();
				if (reservationRoom.getRoomNumber() == null || date.compareTo(startDate) < 0 || date.compareTo(endDate) > 0) {
					date = null;
				}
				RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
				roomAvailability.onInitializeRoomList((ProjectReservationRoom)getTo(), date, null);
			}
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Habitación";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onAcceptReservationRoom(ActionEvent event) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (isNevv()) {
			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			reservationRoom.setItem(roomAvailability.getFilterParams().getItem());
		}
		onAccept(event);
	}

	public void onAssignReservationRoom(ActionEvent event) throws ManagerBeanException {
		ExternalContext ec = FacesContext.getCurrentInstance().getExternalContext();
		Map<String, String> params = ec.getRequestParameterMap();
		Room availableRoom = (Room)BeanManager.getManagerBean(Room.class).get(new Integer(params.get(IPmsConstants.AVAILABLE_ROOM)));

		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (isNevv()) {
			reservationRoom.setItem(availableRoom.getItem());
		}
		onAccept(event);

		ReservationUtils reservationUtils = new ReservationUtils();
		if (reservationRoom.getRoomNumber() == null) {
	    	reservationUtils.insertProjectReservationRoomDetails(reservationRoom, availableRoom, getLinkedServices());
	    	if (!availableRoom.getItem().getId().equals(reservationRoom.getItem().getId())) {
	        	InventoryManager manager = new InventoryManager();
	        	manager.processInventoryQuery(reservationRoom);
	        	manager.processInventoryQuery(reservationRoom, availableRoom.getHotel(), availableRoom.getItem());
	    	}
		} else {
			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			Date startDate = roomAvailability.getFilterParams().getViewerStartDate();
			Date endDate = roomAvailability.getFilterParams().getViewerEndDate();
			List<Item> inventoryItems = reservationUtils.getProjectReservationRoomDetailItems(reservationRoom, startDate, endDate);
			reservationUtils.updateProjectReservationRoomDetails(reservationRoom, startDate, endDate, availableRoom);
	    	if (inventoryItems.size() > 1 || (inventoryItems.size() == 1 && !availableRoom.getItem().getId().equals(inventoryItems.get(0).getId()))) {
	        	InventoryManager manager = new InventoryManager();
	        	for (Item roomItem : inventoryItems) {
		        	manager.processInventoryQuery(reservationRoom, reservationRoom.getHotel(), roomItem, startDate, DateUtils.addDays(endDate, -1));
	        	}
	        	manager.processInventoryQuery(reservationRoom, availableRoom.getHotel(), availableRoom.getItem(), startDate, DateUtils.addDays(endDate, -1));
	    	}
		}
		reservationRoom.setRoomNumber(availableRoom.getAsset().getName());

    	IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onCancelReservationRoom(ActionEvent event) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		reservationRoom.setRoomNumber(null);

		ReservationUtils reservationUtils = new ReservationUtils();
		List<Item> inventoryItems = reservationUtils.getProjectReservationRoomDetailItems(reservationRoom);
    	reservationUtils.removeProjectReservationRoomDetails(reservationRoom, false, null);
    	if (inventoryItems.size() > 1 || (inventoryItems.size() == 1 && !reservationRoom.getItem().getId().equals(inventoryItems.get(0).getId()))) {
	    	InventoryManager manager = new InventoryManager();
	    	for (Item roomItem : inventoryItems) {
		    	if (!roomItem.getId().equals(reservationRoom.getItem().getId())) {
		        	manager.processInventoryQuery(reservationRoom, reservationRoom.getHotel(), roomItem);
		    	}
	    	}
	    	manager.processInventoryQuery(reservationRoom);
    	}

    	IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onRemoveReservationRoom(ActionEvent event) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();

		ReservationUtils reservationUtils = new ReservationUtils();
    	reservationUtils.removeProjectReservationRoomDetails(reservationRoom, false, null);

    	onRemove(event);

    	IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onShowRoomDetails(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getModel().getRowData();
			reservationRoom.setShowRoomDetail(true);
		}
	}

	public void onHideRoomDetails(ActionEvent event) throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getModel().getRowData();
			reservationRoom.setShowRoomDetail(false);
		}
	}

	public boolean isShowRoomDetail() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getModel().getRowData();
			return reservationRoom.isShowRoomDetail();
		}
		return false;
	}

	public List<ITransferObject> getRoomDetailList() throws ManagerBeanException {
		if (getModel().isRowAvailable()) {
			ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getModel().getRowData();
			return getRoomDetailList(reservationRoom);
		}
		return null;
	}

	private List<ITransferObject> getRoomDetailList(ProjectReservationRoom room) throws ManagerBeanException {
		IManagerBean reservationRoomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID), room.getId());
		criteria.addOrder(reservationRoomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE));
		return reservationRoomDetailBean.getList(criteria);
	}

	public List<SelectItem> getAvailableServicesList() throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
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

		List<SelectItem> serviceItemList = new LinkedList<SelectItem>();
		Integer[] services = new Integer[servicesList.size()];
		for (ProjectReservationService reservationService : servicesList) {
			SelectItem serviceItem = new SelectItem(reservationService.getId(), reservationService.getDescription());
			serviceItemList.add(serviceItem);

			int roomCount = reservationService.getProjectReservation().getRoomCount();
			int serviceRoom = (reservationService.getProjectReservationRoom() == null) ? 0 : reservationService.getProjectReservationRoom().intValue();
			if ((isNevv() && roomCount == 0) || (!isNevv() && (roomCount == 1 || serviceRoom == reservationRoom.getId().intValue()))) {
				services[servicesList.indexOf(reservationService)] = reservationService.getId();
			}
		}
		setLinkedServices(services);
		return serviceItemList;
	}

	public List<ProjectReservationService> getLinkedServicesList() throws ManagerBeanException {
		List<ProjectReservationService> servicesList = new LinkedList<ProjectReservationService>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, ((ProjectReservationRoom)getTo()).getProjectReservation().getId());
		alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID);
		criteria.addEqualExpression(alias, ((ProjectReservationRoom)getTo()).getId());
		criteria.addOrder(reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_ID));
		for (ITransferObject ito : reservationServiceDetailBean.getList(criteria)) {
			ProjectReservationService reservationService = ((ProjectReservationServiceDetail)ito).getProjectReservationService();
			if (!servicesList.contains(reservationService)) {
				servicesList.add(reservationService);
			}
		}
		return servicesList;
	}

	public void onMinusStartDate(ActionEvent event) {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		Date startDate = DateUtils.addDays(roomAvailability.getFilterParams().getViewerStartDate(), -1);
		if (startDate.compareTo(((ProjectReservationRoom)getTo()).getProjectReservation().getStartDate()) >= 0) {
			roomAvailability.getFilterParams().setViewerStartDate(startDate);
			roomAvailability.onFilter(event);
		}
	}

	public void onPlusStartDate(ActionEvent event) {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		Date startDate = DateUtils.addDays(roomAvailability.getFilterParams().getViewerStartDate(), 1);
		if (startDate.compareTo(roomAvailability.getFilterParams().getViewerEndDate()) < 0) {
			roomAvailability.getFilterParams().setViewerStartDate(startDate);
			roomAvailability.onFilter(event);
		}
	}

	public void onMinusEndDate(ActionEvent event) {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		Date endDate = DateUtils.addDays(roomAvailability.getFilterParams().getViewerEndDate(), -1);
		if (endDate.compareTo(roomAvailability.getFilterParams().getViewerStartDate()) > 0) {
			roomAvailability.getFilterParams().setViewerEndDate(endDate);
			roomAvailability.onFilter(event);
		}
	}

	public void onPlusEndDate(ActionEvent event) {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		Date endDate = DateUtils.addDays(roomAvailability.getFilterParams().getViewerEndDate(), 1);
		if (endDate.compareTo(((ProjectReservationRoom)getTo()).getProjectReservation().getEndDate()) <= 0) {
			roomAvailability.getFilterParams().setViewerEndDate(endDate);
			roomAvailability.onFilter(event);
		}
	}

}