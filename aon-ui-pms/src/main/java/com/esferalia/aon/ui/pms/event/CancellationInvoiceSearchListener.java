package com.esferalia.aon.ui.pms.event;

import com.code.aon.AonVersion;
import com.code.aon.common.BeanManager;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.config.Tariff;
import com.code.aon.customer.Customer;
import com.code.aon.ql.Criteria;
import com.code.aon.ql.util.ExpressionException;
import com.code.aon.ui.form.event.ControllerSearchListener;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.pms.Hotel;
import com.esferalia.aon.pms.enumeration.BookingHolder;
import com.esferalia.aon.pms.enumeration.ReservationCheckStatus;
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.CancellationInvoiceController;

public class CancellationInvoiceSearchListener extends ControllerSearchListener {
	
	private static final long serialVersionUID = AonVersion.SERIAL_VERSION_UID;

	private boolean guestReservationSearch;
	private Hotel hotelReservation;
	private Customer agency;
	private Tariff tariff;
	private boolean manual;

	public boolean isGuestReservationSearch() {
		return guestReservationSearch;
	}
	public void setGuestReservationSearch(boolean guestReservationSearch) {
		this.guestReservationSearch = guestReservationSearch;
	}
	
	public Hotel getHotelReservation() {
		return hotelReservation;
	}
	public void setHotelReservation(Hotel hotelReservation) {
		this.hotelReservation = hotelReservation;
	}

	public Customer getAgency() {
		return agency;
	}
	public void setAgency(Customer agency) {
		this.agency = agency;
	}
	
	public Tariff getTariff() {
		return tariff;
	}
	public void setTariff(Tariff tariff) {
		this.tariff = tariff;
	}

	public boolean isManual() {
		return manual;
	}
	public void setManual(boolean manual) {
		this.manual = manual;
	}
	
	@Override
	protected void init() throws ManagerBeanException {
		setGuestReservationSearch(true);
		setHotelReservation((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setTariff((Tariff)BeanManager.getManagerBean(Tariff.class).createNewTo());
		setManual(false);

		((CancellationInvoiceController)getController()).clearCheckedReservations();
	}

	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.CANCELLED);
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_CHECK_STATUS), ReservationCheckStatus.CANCEL_INVOICEABLE);
		if (getHotelReservation() != null && getHotelReservation().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_RESERVATION_ID), getHotelReservation().getId());			
		}
		if (isGuestReservationSearch()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
		} else {
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
		}
		if (getAgency() != null && getAgency().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
		}
		if (getTariff() != null && getTariff().getId() != null) {
			criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.rooms.tariff.id"), getTariff().getId());			
		}
		if (!isManual()) {
			criteria.addNotNullExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_PENALTY_VALUE));
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_PENALTY_VALUE), String.valueOf(0));
		}
	}

}