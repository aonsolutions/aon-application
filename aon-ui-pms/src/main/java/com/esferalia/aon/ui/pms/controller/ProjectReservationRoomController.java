package com.esferalia.aon.ui.pms.controller;

import java.util.LinkedList;
import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

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
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationRoomController extends LinesController {

	private boolean showRoomDetailWindow;
	private Integer[] linkedServices;

	public boolean isShowRoomDetailWindow() {
		return showRoomDetailWindow;
	}

	public void setShowRoomDetailWindow(boolean showRoomDetailWindow) {
		this.showRoomDetailWindow = showRoomDetailWindow;
	}

	public Integer[] getLinkedServices() {
		return linkedServices;
	}

	public void setLinkedServices(Integer[] linkedServices) {
		this.linkedServices = linkedServices;
	}

	@Override
	public void onReset(ActionEvent event) {
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

			RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
			roomAvailability.onInitializeRoomList((ProjectReservationRoom)getTo());

			setNew(true);
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Habitación";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onSelect(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				setTo((ITransferObject)getModel().getRowData());

				RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
				roomAvailability.onInitializeRoomList((ProjectReservationRoom)getTo());
			}
		} catch (ManagerBeanException ex) {
			String msg = "No es posible asignar Habitación";
			AonUtil.addErrorMessage(msg);
			throw new AbortProcessingException(msg);
		}
	}

	public void onAssignReservationRoom(ActionEvent event) throws ManagerBeanException {
		RoomAvailabilityController roomAvailability = (RoomAvailabilityController)AonUtil.getRegisteredBean(IPmsConstants.ROOM_AVAILABILITY_CONTROLLER_NAME);
		Room availableRoom = roomAvailability.getAvailableRoom();

		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (isNew()) {
			reservationRoom.setItem((availableRoom != null) ? availableRoom.getItem() : roomAvailability.getFilterParams().getItem());
		}
		onAccept(event);

		if (reservationRoom.getRoomNumber() == null && availableRoom != null) {
			ReservationUtils reservationUtils = new ReservationUtils();
	    	reservationUtils.insertProjectReservationRoomDetails(reservationRoom, availableRoom, getLinkedServices());
		}
		setLinkedServices(null);
	}

	public void onCancelReservationRoom(ActionEvent event) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		reservationRoom.setRoomNumber(null);

		ReservationUtils reservationUtils = new ReservationUtils();
    	reservationUtils.removeProjectReservationRoomDetails(reservationRoom, false);

    	IController reservationServiceController = (IController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_SERVICE_CONTROLLER_NAME);
    	reservationServiceController.onSearch(event);
	}

	public void onRemoveReservationRoom(ActionEvent event) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();

		ReservationUtils reservationUtils = new ReservationUtils();
    	reservationUtils.removeProjectReservationRoomDetails(reservationRoom, false);

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
		List<ProjectReservationService> servicesList = new LinkedList<ProjectReservationService>();
		IManagerBean reservationServiceDetailBean = BeanManager.getManagerBean(ProjectReservationServiceDetail.class);
		Criteria criteria = new Criteria();
		String alias = reservationServiceDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_SERVICE_DETAIL_PROJECT_RESERVATION_SERVICE_PROJECT_RESERVATION_ID);
		criteria.addEqualExpression(alias, ((ProjectReservationRoom)getTo()).getProjectReservation().getId());
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
			if (reservationService.getProjectReservation().getRoomCount() == 1) {
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

}