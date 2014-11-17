package com.esferalia.aon.ui.pms.event;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.product.Item;
import com.code.aon.ui.form.event.ControllerAdapter;
import com.code.aon.ui.form.event.ControllerEvent;
import com.code.aon.ui.form.event.ControllerListenerException;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.ProjectReservationGuest;
import com.esferalia.aon.pms.ProjectReservationRoom;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationSource;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.ProjectReservationController;

public class ProjectReservationControllerListener extends ControllerAdapter {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	@Override
	public void afterBeanCreated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		reservation.setStartDate(DateUtils.truncate(new Date(), Calendar.DATE));
		reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), 1));
		reservation.setSource(ReservationSource.MANUAL);
		reservation.setCheckStatus(ReservationCheckStatus.NO_CHECK);
		reservation.setStatus(ReservationStatus.ACTIVE);

		try {
			controller.getReservationPermission().setReservation(reservation);
			controller.resetHotel();
			controller.resetStartTime();
			controller.resetEndTime();
			controller.resetNights();
			controller.resetGuestName();
			controller.resetRoomItem();
			controller.resetRoomTariff();
			controller.setInvoiceModel(null);
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void afterBeanSelected(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();

		controller.getReservationPermission().setReservation(reservation);
		controller.resetStartTime();
		controller.resetEndTime();
		controller.resetNights();
		controller.setInvoiceModel(null);
	}

	@Override
	public void beforeBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		int currentYear = CommonUtil.getYear(new Date());
		if (CommonUtil.getYear(reservation.getStartDate()) > (currentYear + 1) || CommonUtil.getYear(reservation.getStartDate()) < (currentYear - 1)) {
			throw new ControllerListenerException("Fecha de Entrada de la Reserva incorrecta.");
		}
		if (!reservation.getStartDate().before(reservation.getEndDate())) {
			throw new ControllerListenerException("Fecha de Salida de la Reserva incorrecta.");
		}
		if (CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate()) > 90) {
			throw new ControllerListenerException("La Estancia no puede ser superior a 90 días.");
		}

		reservation.setStartTime(obtainDateTime(reservation.getStartDate(), controller.getStartTime()));
		reservation.setEndTime(obtainDateTime(reservation.getEndDate(), controller.getEndTime()));
		reservation.setHotelReservation(reservation.getHotel());
	}

	@Override
	public void afterBeanAdded(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		try {
			insertProjectReservationGuest(reservation, controller.getGuestName(), controller.getGuestSurname());
			if (controller.getRoomItem() != null) {
				insertProjectReservationRoom(reservation, controller.getRoomItem(), controller.getRoomTariff());
			}
		} catch(ManagerBeanException e) {
			throw new ControllerListenerException(e.getMessage(), e);
		}
	}

	@Override
	public void beforeBeanUpdated(ControllerEvent event) throws ControllerListenerException {
		ProjectReservationController controller = (ProjectReservationController)event.getController();
		ProjectReservation reservation = (ProjectReservation)controller.getTo();
		reservation.setStartTime(obtainDateTime(reservation.getStartDate(), controller.getStartTime()));
		reservation.setEndTime(obtainDateTime(reservation.getEndDate(), controller.getEndTime()));
	}

	private Date obtainDateTime(Date date, String time) {
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(time.substring(0, 2)));
		calendar.set(Calendar.MINUTE, Integer.parseInt(time.substring(3, time.length())));
		return calendar.getTime();
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