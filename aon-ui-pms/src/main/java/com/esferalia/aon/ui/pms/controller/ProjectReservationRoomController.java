package com.esferalia.aon.ui.pms.controller;

import java.util.List;

import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;

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
import com.esferalia.aon.pms.Room;
import com.esferalia.aon.pms.reservation.ReservationUtils;

public class ProjectReservationRoomController extends LinesController {

	private boolean showRoomDetailWindow;

	public boolean isShowRoomDetailWindow() {
		return showRoomDetailWindow;
	}

	public void setShowRoomDetailWindow(boolean showRoomDetailWindow) {
		this.showRoomDetailWindow = showRoomDetailWindow;
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

			ReservationTableController controller = (ReservationTableController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_TABLE_CONTROLLER_NAME);
			controller.onInitializeRoomList((ProjectReservationRoom)getTo());

			setNew(true);
		} catch (ManagerBeanException ex) {
			throw new AbortProcessingException("No es posible asignar Habitación");
		}
	}

	public void onSelect(ActionEvent event) {
		try {
			if (getModel().isRowAvailable()) {
				setTo((ITransferObject)getModel().getRowData());

				ReservationTableController tableController = (ReservationTableController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_TABLE_CONTROLLER_NAME);
				tableController.onInitializeRoomList((ProjectReservationRoom)getTo());
			}
		} catch (ManagerBeanException ex) {
			throw new AbortProcessingException("No es posible asignar Habitación");
		}
	}

	public void onAssignReservationRoom(ActionEvent event) throws ManagerBeanException {
		ReservationTableController tableController = (ReservationTableController)AonUtil.getRegisteredBean(IPmsConstants.RESERVATION_TABLE_CONTROLLER_NAME);
		Room availableRoom = tableController.getAvailableRoom();

		ProjectReservationRoom reservationRoom = (ProjectReservationRoom)getTo();
		if (isNew()) {
			reservationRoom.setItem((availableRoom != null) ? availableRoom.getItem() : tableController.getFilterParams().getItem());
		}
		onAccept(event);

		if (reservationRoom.getRoomNumber() == null && availableRoom != null) {
			ReservationUtils reservationUtils = new ReservationUtils();
	    	reservationUtils.insertProjectReservationRoomDetails(reservationRoom, availableRoom);
		}
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
		IManagerBean roomDetailBean = BeanManager.getManagerBean(ProjectReservationRoomDetail.class);
		Criteria criteria = new Criteria();
		criteria.addEqualExpression(roomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_PROJECT_RESERVATION_ROOM_ID), room.getId());
		criteria.addOrder(roomDetailBean.getFieldName(IEntityAlias.PROJECT_RESERVATION_ROOM_DETAIL_ASSET_ACTIVITY_DATE));
		return roomDetailBean.getList(criteria);
	}

}