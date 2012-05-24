package com.esferalia.aon.ui.pms.event;

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
import com.esferalia.aon.pms.enumeration.ReservationStatus;
import com.esferalia.aon.ui.pms.controller.AdvanceInvoiceController;

public class AdvanceInvoiceSearchListener extends ControllerSearchListener {

	private boolean guestReservationSearch;
	private Hotel hotel;
	private Customer agency;
	private Tariff tariff;

	public boolean isGuestReservationSearch() {
		return guestReservationSearch;
	}
	public void setGuestReservationSearch(boolean guestReservationSearch) {
		this.guestReservationSearch = guestReservationSearch;
	}
	
	public Hotel getHotel() {
		return hotel;
	}
	public void setHotel(Hotel hotel) {
		this.hotel = hotel;
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

	@Override
	protected void init() throws ManagerBeanException {
		setGuestReservationSearch(true);
		setHotel((Hotel)BeanManager.getManagerBean(Hotel.class).createNewTo());
		setAgency((Customer)BeanManager.getManagerBean(Customer.class).createNewTo());
		setTariff((Tariff)BeanManager.getManagerBean(Tariff.class).createNewTo());

		((AdvanceInvoiceController)getController()).clearCheckedReservations();
	}
	
	@Override
	protected void completeCriteria(Criteria criteria) throws ManagerBeanException, ExpressionException {
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_STATUS), ReservationStatus.ACTIVE);
		criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ADVANCE_INVOICED), false);
		criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_TOTAL), 0.0);
		if (getHotel() != null && getHotel().getId() != null) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_HOTEL_ID), getHotel().getId());			
		}
		if (isGuestReservationSearch()) {
			criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_ADVANCE), 0.0);		
		} else {
			criteria.addNotEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_BOOKING_HOLDER), BookingHolder.GUEST);
			if (getAgency() != null && getAgency().getId() != null) {
				criteria.addEqualExpression(getFieldName(IEntityAlias.PROJECT_RESERVATION_AGENCY_ID), getAgency().getId());			
			}
			if (getTariff() != null && getTariff().getId() != null) {
				criteria.addEqualExpression(getController().resolveAlias("ProjectReservation.rooms.tariff.id"), getTariff().getId());			
			}
		}
	}

}