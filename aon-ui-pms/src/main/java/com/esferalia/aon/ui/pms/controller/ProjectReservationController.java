package com.esferalia.aon.ui.pms.controller;

import java.util.Date;

import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;

import org.apache.commons.lang.time.DateUtils;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.util.CommonUtil;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.product.Item;
import com.code.aon.ui.common.components.LookupChangeEvent;
import com.code.aon.ui.form.BasicController;
import com.code.aon.ui.util.AonUtil;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.ProjectReservation;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationStatus;

public class ProjectReservationController extends BasicController {

	private String selectedTab;
	private int nights;
	private String guestName;
	private String guestSurname;
	private Item roomItem;
	private Tariff roomTariff;

	public String getSelectedTab() {
		return selectedTab;
	}
	public void setSelectedTab(String selectedTab) {
		this.selectedTab = selectedTab;
	}

	public int getNights() {
		if (nights == 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			if (reservation.getStartDate() != null && reservation.getEndDate() != null) {
				nights = (int)CommonUtil.getDaysBetweenDates(reservation.getStartDate(), reservation.getEndDate());
			}
		}
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public void resetNights() {
		setNights(0);
	}

	public String getGuestName() {
		return guestName;
	}
	public void setGuestName(String guestName) {
		this.guestName = guestName;
	}

	public String getGuestSurname() {
		return guestSurname;
	}
	public void setGuestSurname(String guestSurname) {
		this.guestSurname = guestSurname;
	}
	public void resetGuestName() {
		setGuestName(null);
		setGuestSurname(null);
	}

	public Item getRoomItem() {
		return roomItem;
	}
	public void setRoomItem(Item roomItem) {
		this.roomItem = roomItem;
	}
	public void resetRoomItem() {
		setRoomItem(null);
	}

	public Tariff getRoomTariff() {
		return roomTariff;
	}
	public void setRoomTariff(Tariff roomTariff) {
		this.roomTariff = roomTariff;
	}
	public void resetRoomTariff() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		setRoomTariff(obtainReservationTariff(reservation));
	}

	public void resetHotel() throws ManagerBeanException {
		PmsCollectionsController collections = (PmsCollectionsController)AonUtil.getRegisteredBean(IPmsConstants.COLLECTIONS_CONTROLLER_NAME);
		if (collections.getCurrentUserHotelsCount() > 0) {
			ProjectReservation reservation = (ProjectReservation)getTo();
			reservation.setHotel((Hotel)collections.getCurrentUserHotels().get(0).getValue());
		}
	}

	public void onHotelChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setHotel((Hotel)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onStartDateChanged(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getStartDate() != null) {
			reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
		} else {
			resetNights();
		}
	}

	public void onNightsChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			setNights((Integer)event.getNewValue());
		} else {
			resetNights();
		}
		reservation.setEndDate(DateUtils.addDays(reservation.getStartDate(), getNights()));
	}

	public void onEndDateChanged(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (reservation.getEndDate() != null) {
			reservation.setStartDate(DateUtils.addDays(reservation.getEndDate(), 0-getNights()));
		} else {
			resetNights();
		}
	}

	public void onAgencyChanged(LookupChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setAgency((Customer)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onCompanyChanged(LookupChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setCompany((Customer)event.getNewValue());
			resetRoomTariff();
		}
	}

	public void onHolderChanged(ValueChangeEvent event) {
		ProjectReservation reservation = (ProjectReservation)getTo();
		if (event.getNewValue() != null && !event.getNewValue().toString().equals("")) {
			reservation.setBookingHolder((BookingHolder)event.getNewValue());
			resetRoomTariff();
		}
	}

	private Tariff obtainReservationTariff(ProjectReservation reservation) {
		Tariff tariff = null;
		if (reservation.getBookingHolder() == BookingHolder.AGENCY && reservation.getAgency() != null && reservation.getAgency().getId() != null) {
			tariff = reservation.getAgency().getTariff();
		} else if (reservation.getBookingHolder() == BookingHolder.COMPANY && reservation.getCompany() != null && reservation.getCompany().getId() != null) {
			tariff = reservation.getCompany().getTariff();
		} else if (reservation.getHotel() != null && reservation.getHotel().getCustomer() != null) {
			tariff = reservation.getHotel().getCustomer().getTariff();
		}
		return tariff;
	}

	public boolean isActive() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return reservation.isActive();
	}

	public boolean isBlocked() {
		ProjectReservation reservation = (ProjectReservation)getTo();
		return reservation.isBlocked();
	}

	public void onLoad(ActionEvent event) throws ManagerBeanException {
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_START_DATE), new Date());
		getCriteria().addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.ACTIVE);
		onSearch(event);
	}

	public void onBlock(ActionEvent event) {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.BLOCKED);
		accept(event);
	}
	
	public void onUnblock(ActionEvent event) throws ManagerBeanException {
		ProjectReservation reservation = (ProjectReservation)this.getTo();
		reservation.setStatus(ReservationStatus.ACTIVE);
		accept(event);
	}

}