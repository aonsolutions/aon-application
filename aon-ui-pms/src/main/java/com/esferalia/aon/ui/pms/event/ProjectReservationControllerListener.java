package com.esferalia.aon.ui.pms.event;

import java.util.Date;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;

public class ProjectReservationControllerListener extends ControllerAdapter {
	
	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		try {
			controller.resetHotel();
			controller.resetNights();
			controller.resetGuestName();
			controller.resetRoomItem();
			controller.resetRoomTariff();
			controller.setInvoiceModel(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}

		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		reservation.setStartDate(new Date());
		reservation.setEndDate(DateUtils.addDays(new Date(), 1));
		reservation.setCrs(false);
		reservation.setStatus(ReservationStatus.ACTIVE);
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		controller.resetNights();
		controller.setInvoiceModel(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservation reservation = (ProjectReservation)event.getController().getTo();
		reservation.setCreationDate(new Date());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		try {
			insertProjectReservationGuest((ProjectReservation)controller.getTo(), controller.getGuestName(), controller.getGuestSurname());
			if (controller.getRoomItem() != null) {
				insertProjectReservationRoom((ProjectReservation)controller.getTo(), controller.getRoomItem(), controller.getRoomTariff());
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservation reservation = (ProjectReservation)event.getController().getTo();
		reservation.setModificationDate(new Date());
	}

	private void insertProjectReservationGuest(ProjectReservation reservation, String name, String surname) throws ManagerBeanException {
		ProjectReservationGuest reservationGuest = new ProjectReservationGuest();
		reservationGuest.setProjectReservation(reservation);
		reservationGuest.setGuestIndex(1);
		reservationGuest.setName(name);
		reservationGuest.setSurname(surname);
		
		IManagerBean reservationGuestBean = BeanManager.getManagerBean(ProjectReservationGuest.class);
		reservationGuestBean.insert(reservationGuest);
	}

	private void insertProjectReservationRoom(ProjectReservation reservation, Item roomItem, Tariff roomTariff) throws ManagerBeanException {
		ProjectReservationRoom reservationRoom = new ProjectReservationRoom();
		reservationRoom.setProjectReservation(reservation);
		reservationRoom.setRoomIndex(1);
		reservationRoom.setItem(roomItem);
		reservationRoom.setTariff(roomTariff);
		
		IManagerBean reservationRoomBean = BeanManager.getManagerBean(ProjectReservationRoom.class);
		reservationRoomBean.insert(reservationRoom);
	}

}